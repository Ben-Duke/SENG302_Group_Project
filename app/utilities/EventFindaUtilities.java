package utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;


import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;


/**
 * EventFindaUtilities Class.
 */
public class EventFindaUtilities {

    /**
     * Default private constructor
     */
    private EventFindaUtilities() {

    }

    /**
     * Gets the data from the EventFinda API based on the given url.
     * @param targetUrl
     * @return a JsonNode object is the result of the api call
     */
    public JsonNode eventFindaGetResponse(String targetUrl) {
        try {
            JsonNode responseJSON = null;
            String url = "https://api.eventfinda.co.nz/v2/" + targetUrl;
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
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Gets max 20 events' data from the EventFinda API happening at the given location (location id).
     * @param locationId
     * @param offset
     * @return Returns a JsonNode object that has all events for the given location
     */
    public JsonNode getEvents(String locationId, int offset) {
        if (offset < 0) {
            offset = 0;
        }
        String url = "events.json?location=" + locationId + "&rows=20" + "&offset=" + offset;
        JsonNode events = eventFindaGetResponse(url);
        return events;
    }

    /**
     * Gets the location id of the passed place name.
     * @param latitude
     * @param longitude
     * @param place
     * @return returns the id of given place name.
     */
    public String getLocationId(double latitude, double longitude, String place){
        String id;
        JsonNode nodes = getLocations(latitude, longitude, place, 0);
        id = nodes.get("locations").get(0).get("id").asText();
        return id;
    }

    /**
     * Gets max 20 locations from the api based on passed query.
     * @param latitude
     * @param longitude
     * @param query
     * @param offset Offset amount
     * @return a JsonNode object that has all locations based on the passed query.
     */
    public JsonNode getLocations(double latitude, double longitude, String query, int offset) {
        if (offset < 0) {
            offset = 0;
        }
        String url = "locations.json?point=" + latitude + "," + longitude +"&rows=20" + "&offset=" + offset + "&q=" + query;
        JsonNode locations = eventFindaGetResponse(url);
        return locations;
    }


}


