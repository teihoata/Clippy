/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.SearchEngine;

import db.clippy.Vo.CountryVo;
import db.clippy.Vo.GoogleNaviVo;
import db.clippy.utils.CountryComparator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
public class GoogleNavigation {
    /**
     * Got navigation result from google
     * @param start     start location
     * @param end       destination
     * @return          A list of 'GoogleNaviVo'
     * @throws IOException
     * @throws JSONException 
     */
    public static List<GoogleNaviVo> GoogleNavigation(String start, String end) throws IOException, JSONException {
        if (!defineCountry(start)) {
            start += ",New Zealand";
            System.out.println("***************** " + start);
        }
        start = java.net.URLEncoder.encode(start, "UTF-8");
        if (!defineCountry(end)) {
            end += ",New Zealand";
            System.out.println("***************** " + end);
        }
        end = java.net.URLEncoder.encode(end, "UTF-8");
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://maps.googleapis.com/maps/api/directions/json?origin=" + start + "&destination=" + end + "&sensor=false");
        HttpResponse response = httpclient.execute(httpget);
        System.out.println(response.getStatusLine());
        
        HttpEntity entity = response.getEntity();
        
        StringBuilder json = new StringBuilder();
        if (entity != null) {
            try (BufferedReader reader = new BufferedReader(
                         new InputStreamReader(entity.getContent(), "UTF-8"))) {
                String readline;
                while ((readline = reader.readLine()) != null) {
                    json.append(readline);
                }
            } catch (IOException ex) {
                throw ex;
            } catch (RuntimeException ex) {
                httpget.abort();
                throw ex;
            }
        }
        httpclient.getConnectionManager().shutdown();
        System.out.println(json);
        JSONObject jsonobj = new JSONObject(json.toString());
        JSONArray jarray = jsonobj.getJSONArray("routes");
        jsonobj = jarray.getJSONObject(0);
        jarray = jsonobj.getJSONArray("legs");
        jsonobj = jarray.getJSONObject(0);
        jarray = jsonobj.getJSONArray("steps");
        
        JSONObject obj;
        List<GoogleNaviVo> gnvol = new ArrayList<>();
        for(int i = 0; i < jarray.length(); i++) {
            obj = jarray.getJSONObject(i);
            GoogleNaviVo gnvo = new GoogleNaviVo();
            gnvo.setCount(i);
            gnvo.setDistance(obj.getJSONObject("distance").getString("text"));
            gnvo.setHtml_instructions(obj.getString("html_instructions").replace("<b>", "").replace("</b>", "").replace("<div style=\"font-size:0.9em\">", " ").replace("</div>", ""));
            JSONObject location = obj.getJSONObject("end_location");
            gnvo.setLat(String.valueOf(location.getDouble("lat")));
            gnvo.setLng(String.valueOf(location.getDouble("lng")));
            gnvol.add(gnvo);
        }
    
        return gnvol;
    }
    
    /**
     * Check if the address contains country
     * @param addr
     * @return 
     */
    private static boolean defineCountry(String addr) {
        boolean flag = false;
        List<CountryVo> countries = new ArrayList<>();
        Locale.setDefault(Locale.ENGLISH);
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String iso = locale.getDisplayName();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry();

            if (!"".equals(iso) && !"".equals(code) && !"".equals(name)) {
                countries.add(new CountryVo(iso, code, name));
            }
        }

        Collections.sort(countries, new CountryComparator());
        for (CountryVo country : countries) {
            String addrs = addr.replace(" ", "").toUpperCase();
            String name = country.name.replace(" ", "");
            if (addrs.contains(name.toUpperCase())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
