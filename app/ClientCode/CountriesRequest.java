package ClientCode;

import akka.actor.UntypedAbstractActor;
import javax.inject.Inject;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;


public class CountriesRequest {

    public void sendGet() throws Exception {

        String url = "https://restcountries.eu/rest/v2/all";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JsonNode jsonJacksonArray = Json.parse(response.toString());
        //print result
        System.out.println(jsonJacksonArray.size());
        System.out.println(jsonJacksonArray.get(0).get("name").textValue());

    }
}