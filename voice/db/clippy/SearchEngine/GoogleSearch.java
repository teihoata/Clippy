/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.SearchEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Ray
 */
public class GoogleSearch {

    public static Map<String, String> getSearchResult(String keyword) throws IOException, JSONException {
        HttpClient httpclient = new DefaultHttpClient();
        keyword = keyword.replace(" ", "%20");
        HttpGet httpget = new HttpGet("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + keyword);
        HttpResponse response = httpclient.execute(httpget);

        HttpEntity entity = response.getEntity();
        String json = "";
        if (entity != null) {
            try (BufferedReader reader = new BufferedReader(
                         new InputStreamReader(entity.getContent(), "UTF-8"))) {
                json = reader.readLine();
            } catch (IOException ex) {
                throw ex;
            } catch (RuntimeException ex) {
                httpget.abort();
                throw ex;
            }
        }
        httpclient.getConnectionManager().shutdown();
        
        JSONObject jsonobj = new JSONObject(json);
        jsonobj = jsonobj.getJSONObject("responseData");
        JSONArray jarray = jsonobj.getJSONArray("results");
        Map<String, String> resultMap = new HashMap<>();
        JSONObject obj;
        for (int i = 0; i < jarray.length(); i++) {
            obj = jarray.getJSONObject(i);
            resultMap.put(obj.getString("titleNoFormatting"), obj.getString("url"));
        }
        return resultMap;
    }
}
