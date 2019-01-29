package com.nchauzov.gn;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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


    public static String query_zapros(String[] sql) throws IOException {

        BufferedReader reader = null;
        try {
            String login = "admin";
            String password = "4";

            String params = formir_params_sql(login, password, sql);
            Log.d("sql_params",params);

            byte[] data = null;
            URL url = new URL("http://www.work.gn/otdelka/query");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setReadTimeout(10000);

            c.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
            OutputStream os = c.getOutputStream();
            data = params.getBytes("UTF-8");

            os.write(data);


            c.connect();
            reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder buf = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line + "\n");
            }


            Log.d("sql_otvet",buf.toString());

            return (buf.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }


    }
}


