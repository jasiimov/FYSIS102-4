package database;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.JOptionPane;

/**
 *
 * @author Jasper Baars
 */
public class DBconnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/corendon";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Paardekop2709";

    private static DBconnection instance = null;

    /**
     * getter for the static database connection existing the entire application
     * lifetime
     * @return an instance of a database connection
     */
    public static DBconnection get() {
        if (instance == null) {
            instance = new DBconnection();
        }
        return instance;
    }

    private Connection conn;

    /**
     * Constructor, connects to the database
     */
    public DBconnection() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (CommunicationsException e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to database."
                    + "\nContact your system administrator for help.");
            System.err.println("SQL Error: cannot connect to database!");
            System.err.println(e);
            //Exits application, otherwise a blank screen will be opened and
            //you cannot close it with clicking the closing 'X'.
            System.exit(0);
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e);
        }
    }

    /**
     * closes the database connection gracefully
     */
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e);
        }
    }

    /**
     * query to select stuff from the database, do not use for insert
     *
     * @param query the query to use, with ?'s at the location of any user input
     * @param args put any user input variables here, the ?'s in the query will
     * be replaced with them
     * @return the result set of the query
     */
    public ResultSet query(String query, Object... args) {
        ResultSet result = null;
        try {
            result = prepareStatement(query, args).executeQuery();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e);
        }
        return result;
    }

    /**
     * prepare the statement to be query/insertquery'd
     *
     * @param query the string with ?'s at the location of variables
     * @param args list of the variables
     * @return the prepared statement, ready for execution
     */
    private PreparedStatement prepareStatement(String query, Object... args) {

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            System.out.println("preparing statement " + query);
            for (int i = 0; i < args.length; i++) {
                System.out.println("setting arg " + i + " to " + args[i]);
                if (args[i] == null) {
                    stmt.setNull(i + 1, Types.VARCHAR);
                } else {
                    switch (args[i].getClass().getName()) {
                        case "java.lang.Integer":
                            stmt.setInt(i + 1, (int) args[i]);
                            break;
                        case "java.lang.Boolean":
                            stmt.setBoolean(i + 1, (boolean) args[i]);
                            break;
                        case "java.lang.String":
                            stmt.setString(i + 1, args[i].toString());
                            break;
                        default:
                            System.err.println(
                                    "Warning: setting unknown object type in prepared statement");
                            System.err.println("Trying to use toString");
                            stmt.setString(i + 1, args[i].toString());
                            break;
                    }
                    System.out.println("which is an " + args[i].getClass().getName());
                }
            }
            return stmt;
        } catch (SQLException e) {
            System.err.println(e);
        }
        return null;
    }

    /**
     * query to use to insert/update/delete stuff from the database.
     *
     * @param query the query to use, with ?'s at the location of any user input
     * @param args put any user input variables here, the ?'s in the query will
     * be replaced with them
     * @return last_insert_id
     */
    public int insertQuery(String query, Object... args) {
        int last_insert_id = -1;
        try {
            prepareStatement(query, args).executeUpdate();
            ResultSet res = prepareStatement("SELECT LAST_INSERT_ID() AS `id`").executeQuery();
            res.next();
            last_insert_id = res.getInt("id");
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e);
        }
        return last_insert_id;
    }
}

