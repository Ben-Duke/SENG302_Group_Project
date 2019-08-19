package utilities;

//This code requires Apache HttpComponents (http://hc.apache.org/downloads.cgi) to be working.
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;


import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;


public class EventFindaUtilities {

    private EventFindaUtilities() {

    }

    /**
     * pass in rows to get a certain amount data otherwise it will give all events
     * @param targetUrl
     * @return
     * @throws Exception
     */
    public static JsonNode eventFindaGetResponse(String targetUrl) {
        JsonNode responseJSON = null;
        try {
            String url = "https://api.eventfinda.co.nz" + targetUrl;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String userCredentials = "travelea:kq4mf8czrp92";
            String credentials = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));


            // optional default is GET
            con.setRequestMethod("GET");
            con.setRequestProperty ("Authorization", credentials);
            con.setConnectTimeout(5000); // 5 seconds

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //Json comes out cleaner using this code.
            responseJSON = Json.parse(response.toString());
            return responseJSON;
        } catch (Exception e) {
            return responseJSON;
        }
    }





    public static void main(String[] args) {
        System.out.println(eventFindaGetResponse("/v2/events.json?Rows"));
    }
}


