package ru.elifantiev.fga;


import android.text.Html;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuckinGreatAdvice {

    public FuckinGreatAdvice() {
    }

    public String getRandomAdvice() {
        return getAdviceUrl("http://fucking-great-advice.ru/api/random");
    }

    public String getLastAdvice() {
        return getAdviceUrl("http://fucking-great-advice.ru/api/latest");
    }

    private String getAdviceUrl(String url) {
        String result = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        StringBuilder responseBuilder = new StringBuilder();
        try {
            client.execute(get).getEntity().getContent();
            String line;
            HttpResponse httpResponse = client.execute(get);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(httpResponse.getEntity().getContent()), 8192);
                while ((line = reader.readLine()) != null)
                    responseBuilder.append(line);
                reader.close();

                Object response = new JSONTokener(responseBuilder.toString()).nextValue();
                JSONObject advice;
                if(response instanceof JSONArray) {
                    if(((JSONArray) response).length() > 0) {
                        advice = ((JSONArray)response).getJSONObject(0);
                    } else
                        return null;
                } else if(response instanceof JSONObject) {
                    advice = (JSONObject) response;
                } else
                    return null;

                result = Html.fromHtml(advice.getString("text")).toString();
            }
        } catch (IOException e) {
            // ignore
        } catch (JSONException e) {
            // ignore
        }
        return result;
    }
}
