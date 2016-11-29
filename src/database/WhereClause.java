package database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jasper Baars
 */
public class WhereClause {

    HashMap<String, Object> clauses;

    public WhereClause() {
        clauses = new HashMap<>();
    }
    
    public void add(String field, Object value) {
        clauses.put(field, value);
    }

     public HashMap<String, Object> getClauses() {
        return clauses;
    }

    
    public String toSQL(List<Object> values) {
        String wheresql = "";
        boolean first = true;
        Iterator it = clauses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
            if (!first) {
                wheresql += " AND ";
            }
            
            if (pair.getValue() == null) {
                wheresql += "ISNULL(`" + pair.getKey() + "`)";
                it.remove();
                first = false;
                continue;
            }
            String key = pair.getKey();
            boolean like = key.contains("LIKE");
            if (like) {
                key = key.replaceAll("LIKE", "");
            }
            wheresql += "`" + key + "`";
            if (like) {
                wheresql += " LIKE ?";
            } else {
                wheresql += " = ?";
            }

            it.remove();
            values.add(pair.getValue());
            first = false;
        }
        return wheresql;
    }

}

