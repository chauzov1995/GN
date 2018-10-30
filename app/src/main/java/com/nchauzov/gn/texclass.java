package com.nchauzov.gn;

public class texclass {
    public static String formir_params_sql(String login, String password, String[] sql) {
        String sql_formir = "";
        int query = sql.length;
        for (int i = 0; i < query; i++) {
            sql_formir += "\"sql" + (i+1) + "\":\"" + sql[i] + "\",";
        }
        String params = "{\"user\":\"" + login + "\",\"password\":\"" + password + "\"," + sql_formir + "\"query\":\"" + query + "\"}";
        return params;
    }
}


