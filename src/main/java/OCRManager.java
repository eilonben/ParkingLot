import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class OCRManager {
    private final String url = "https://api.ocr.space/parse/image";
    private final String apiKey = "142389655f88957";
    public OCRManager() {
    }

    /**
     * Sends a url of a license plate image to the OCR space API by POST request
     * @param filePath - path of the image to be sent to the api
     * @return the string of the license plate described in the picture
     * @throws Exception
     */
    public String sendOCRPost(String filePath,String format) throws Exception{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

// Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("apikey", apiKey));
        params.add(new BasicNameValuePair("base64Image", "data:image/"+format+";base64,"+encodedString));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

//Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        ParkingLogger.getInstance().logger.info("sent a post request to OCR space with the image path "+filePath);
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

            //Response from OCR Space given as a json object
            JSONObject jsonData =new JSONObject(jsonResponse);
            if (jsonData.has("ParsedResults"))
                return handleJSONResponse(jsonData.getJSONArray("ParsedResults").getJSONObject(0));
            else if (jsonData.has("IsErroredOnProcessing")
                    && jsonData.getBoolean("IsErroredOnProcessing"))
                throw new OCRException("There has been an error with processing the image.");
        }
        return null;
    }


    private String handleJSONResponse(JSONObject response) throws Exception {

        if (response.has("ParsedText")) {
            String text = response.getString("ParsedText");
            String[] splitLines = text.split("\n");
            if(text.length()==0){
                ParkingLogger.getInstance().logger.info("the response given from the API contained no text");
                throw new OCRException("Error: No text was parsed by the OCR API from the image given.");

            }
            ParkingLogger.getInstance().logger.info("The API parsed the image text. the plate number: "+splitLines[0]);
            return splitLines[0];
        } else if (response.has("ErrorMessage")) {
            String error = response.getString("ErrorMessage");
            ParkingLogger.getInstance().logger.info("An error from OCR space was received: "+error);
            throw new OCRException("Error: There was a problem parsing the picture: "+error);
        }
        return null;
    }
}
