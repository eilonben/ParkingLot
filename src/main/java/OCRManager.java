
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OCRManager {
    private final String url = "https://api.ocr.space/parse/image";
    private final String apiKey = "142389655f88957";
    public OCRManager() {
    }

    public String sendOCRPost(String imageUrl) throws Exception{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

// Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("apikey", apiKey));
        params.add(new BasicNameValuePair("url", imageUrl));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

//Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        String jsonResponse="";
        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                final int bufferSize = 1024;
                final char[] buffer = new char[bufferSize];
                final StringBuilder out = new StringBuilder();
                Reader in = new InputStreamReader(instream, StandardCharsets.UTF_8);
                int charsRead;
                while((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
                    out.append(buffer, 0, charsRead);
                }
                 jsonResponse = out.toString();
            }


            JSONObject jsonData =new JSONObject(jsonResponse);
            if (jsonData.has("ParsedResults"))
                return handleJSONResponse(jsonData.getJSONArray("ParsedResults").getJSONObject(0));
            else if (jsonData.has("IsErroredOnProcessing")
                    && jsonData.getBoolean("IsErroredOnProcessing"))
                return jsonData.toString();
            return null;
        }
//        URL postURL = new URL(url);
//        HttpsURLConnection con = (HttpsURLConnection) postURL.openConnection();
//
//        //add request header
//        con.setRequestMethod("POST");
//        con.setRequestProperty("User-Agent", "Mozilla/5.0");
//        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//
//        //Chaining the api key and image url to the ocrSpace url
//        String params = "apikey="+apiKey+"&"+"url="+imageUrl;
//
//        // Send post request
//        con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(URLEncoder.encode(params,"UTF-8"));
//        wr.flush();
//        wr.close();
//
//        //retrieve JSON result
//        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//
//        JSONObject responseData =new JSONObject(String.valueOf(response));
//        return handleJSONResponse(responseData);
        return null;
    }


    private String handleJSONResponse(JSONObject response) throws Exception {
        int output = -1;
        if (response.has("ParsedText")) {
            String text = response.getString("ParsedText");
            //TODO write to log
            return text;
        } else if (response.has("ErrorMessage")) {
            String error = response.getString("ErrorMessage");
            //TODO write to ErrorLog
            throw new Exception("There was a problem parsing the picture: "+error);
        }
        return null;
    }
}
