package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;


import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import play.libs.Json;


/**
 * EventFindaUtilities Class.
 */
public class EventFindaUtilities {

    /**
     * Default private constructor
     */
    private EventFindaUtilities() {
        throw new UnsupportedOperationException();
    }

    private static final Logger logger = UtilityFunctions.getLogger();

    /**
     * Gets the data from the EventFinda API based on the given url.
     * @param targetUrl
     * @return a JsonNode object is the result of the api call
     */
    private static JsonNode eventFindaGetResponse(String targetUrl) {
        try {
            String url = "https://api.eventfinda.co.nz/v2/" + targetUrl;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String userCredentials = "travelea:kq4mf8czrp92";
            String credentials = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));


            // optional default is GET
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", credentials);
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
            return Json.parse(response.toString());
        } catch (ProtocolException e) {
            throw new IllegalArgumentException("Incorrect query.");
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }


    /**
     * Gets max 20 events' data from the EventFinda API happening at the given location (location id).
     * @param latitude
     * @param longitude
     * @param place
     * @param offset
     * @return Returns a JsonNode object that has all events for the given location
     */
    public static JsonNode getEvents(double latitude, double longitude, String place, int offset) {
        if (offset < 0) {
            offset = 0;
        }
        int locationId = getLocationId(latitude, longitude, place);
        if (locationId == -1) {
            return null;
        }
        String url = "events.json?location=" + locationId + "&rows=20" + "&offset=" + offset;
        return eventFindaGetResponse(url);
    }

    /**
     * Gets the location id of the passed place name.
     * @param latitude
     * @param longitude
     * @param place
     * @return returns the id of given place name.
     */
    public static int getLocationId(double latitude, double longitude, String place){
        JsonNode nodes = getLocations(latitude, longitude, place, 0);
        if (nodes != null) {
            return Integer.parseInt(nodes.get("locations").get(0).get("id").asText());
        }
        return -1;
    }

    /**
     * Gets max 20 locations from the api based on passed query.
     * @param latitude
     * @param longitude
     * @param query
     * @param offset Offset amount
     * @return a JsonNode object that has all locations based on the passed query.
     */
    public static JsonNode getLocations(double latitude, double longitude, String query, int offset) {
        if (offset < 0) {
            offset = 0;
        }
        String url = "locations.json?point=" + latitude + "," + longitude +"&rows=20" + "&offset=" + offset + "&q=" + query;
        return eventFindaGetResponse(url);
    }


}


