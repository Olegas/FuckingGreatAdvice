package ru.elifantiev.fga;


import android.text.Html;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuckinGreatAdvice {

    private String url, errorText;

    public FuckinGreatAdvice(String adviceUrl, String errorText) {
        this.url = adviceUrl;
        this.errorText = errorText;
    }

    public String getAdviceOrThrow() throws Exception {
        String page = null, retval = null;
        try {
            page = loadPage();
        } catch (IOException e) {
            throw new Exception("No new data");
        }

        if (page != null) {
            Pattern p = Pattern.compile("id=\"advice\">([^<]+)</");
            Matcher m = p.matcher(page);
            while (m.find()) { // Find each match in turn; String can't do this.
                // current advice
                retval = m.group(1).replace("&nbsp;", " ");
            }

            p = Pattern.compile("\"another\"><a href=\"([^\"]+)\"");
            m = p.matcher(page);
            while (m.find()) { // Find each match in turn; String can't do this.
                // next advice
                url = m.group(1);
            }
        }

        if(retval == null)
            throw new Exception("Page parsing error");

        return Html.fromHtml(retval).toString();
    }

    public String getAdvice() {
        String retval;
        try {
            retval = getAdviceOrThrow();
        } catch(Exception e) {
            retval = errorText;
        }
        return retval;
    }

    private String loadPage() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;

        DefaultHttpClient httpclient = new DefaultHttpClient();


        InputStream is = httpclient.execute(new HttpGet(url)).getEntity().getContent();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(is, "windows-1251"));

        while ((line = rdr.readLine()) != null)
            response.append(line);

        rdr.close();

        return response.toString();
    }

    /**
     * Function return URL of next advice
     *
     * @return String url
     */
    public String getNextURL() {
        return this.url;
    }

}
