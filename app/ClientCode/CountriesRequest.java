package ClientCode;

import akka.actor.UntypedAbstractActor;
import javax.inject.Inject;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.*;


public class CountriesRequest {

    /**
     * This method sends a get request to the countries api and returns a sorted set of these countries
     * @return
     * @throws Exception
     */
    public Set sendGet() throws Exception {
        String url = "https://restcountries.eu/rest/v2/all";
        Set<String> countries = new HashSet<String>();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

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

        for(JsonNode node:jsonJacksonArray){
            countries.add(node.get("name").textValue());
        }
        TreeSet countrySet = new TreeSet<String>();
        countrySet.addAll(countries);

        return countrySet;
    }
}