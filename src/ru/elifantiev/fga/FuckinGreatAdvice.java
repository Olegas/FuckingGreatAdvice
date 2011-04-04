package ru.elifantiev.fga;


import android.text.Html;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuckinGreatAdvice {

    private String url, errorText;

    public FuckinGreatAdvice(String adviceUrl, String errorText) {
        this.url = adviceUrl;
        this.errorText = errorText;
    }

    public String getAdvice() {
        String page = null, retval = errorText;
        try {
            page = loadPage();
        } catch (IOException e) {
            // ignore
        }

        if (page != null) {
            Pattern p = Pattern.compile("id=\"advice\">([^<]+)</");
            Matcher m = p.matcher(page);
            while (m.find()) { // Find each match in turn; String can't do this.
                retval =  m.group(1).replace("&nbsp;", " ");
            }

            p = Pattern.compile("\"another\"><a href=\"([^\"]+)\"");
            m = p.matcher(page);
            while (m.find()) { // Find each match in turn; String can't do this.
                url =  m.group(1);
            }
        }
        return Html.fromHtml(retval).toString();
    }

    private Document loadAdvicePage() {

        String input;

        try {
            input = loadPage();
        } catch (IOException e) {
            return null;
        }

        DocumentBuilder docB = null;
        try {
            docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return null;
        }

        Document doc = null;
        if (docB != null) {
            try {
                doc = docB.parse(new ByteArrayInputStream(input.getBytes()));
            } catch (SAXException e) {
                return null;
            } catch (IOException e) {
                // ignore
            }
        }

        return doc;
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

}
