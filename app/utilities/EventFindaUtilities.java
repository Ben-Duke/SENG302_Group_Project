package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


import com.fasterxml.jackson.databind.JsonNode;
import models.Destination;
import org.slf4j.Logger;
import play.libs.Json;
import scala.collection.concurrent.Debug;


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
            targetUrl = URLDecoder.decode(targetUrl, "UTF-8");
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

    public static JsonNode getEvents(String keyword, String category, String artist, String startDate,
                                     String endDate, String minPrice, String maxPrice,
                                     Destination destination, String sortBy) {

        int locationId = getLocationId(-43.53, 172.620278, "Christchurch");
        if (locationId == -1) {
            return null;
        }
        String url = "events.json?location="+locationId+ "&rows=20" + "&offset=" + 0;

        if (!category.isEmpty()) {
            /*TODO use addFreeTextFilterToQuery() to parse input. Then get matching category from eventFinda using this as the q param. Then get the category ID from results of this.
            The category ID is to be used here. */
            url += "&category="+category;
        }
        if (!artist.isEmpty()) {
            /*TODO use addFreeTextFilterToQuery() to parse input. Then get matching artist from eventFinda using this as the q param. Then get the artist ID from results of this.
            The artist ID is to be used here. */
            url += "&artist="+artist;
        }
        if (!startDate.isEmpty()) {
            url += "&start_date="+startDate;
        }
        if (!endDate.isEmpty()) {
            url += "&end_date="+endDate;
        }
        if (!minPrice.isEmpty()) {
            url += "&price_min="+minPrice;
        }
        if (!maxPrice.isEmpty()) {
            url += "&price_max="+maxPrice;
        }
        if (!sortBy.isEmpty()) {
            url += "&order="+sortBy;
        }
        if (!keyword.isEmpty()) {
            url = addKeyWordFilterToQuery(url, keyword);
        }
        return eventFindaGetResponse(url);
    }

    /**
     * Adds keyword parameter to the API given query.
     *
     * The "keywords" parameter should be a string of one or more keywords.
     * If they are separated by "," then an "OR" search is performed, and
     * if they are separated by spaces then an "AND" search is performed.
     * For example:
     * Input: addKeyWordFilterToQuery("currentUrl", "cycling    Southland    ,   running, alpha")
     * Output: currentUrl&q=(cycling+AND+Southland)+OR+(running)+OR+(alpha)
     * @param currentQuery String
     * @param keywords String
     * @return returns the id of given place name.
     */
    public static String addKeyWordFilterToQuery(String currentQuery, String keywords){
        String updatedQuery = currentQuery;
        try {
            keywords = URLDecoder.decode(keywords, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Debug.log(e.getStackTrace());
        }
        String[] qParams = keywords.split("\\s*,\\s*");
        List<String[]> ors = new ArrayList<>();
        for (String words: qParams) {
            String[] splitWords = words.split("\\s+");
            ors.add(splitWords);
        }
        String stringToAdd = "";
        int index = 0;
        for (String[] str : ors) {
            if (str.length > 0) {
                String andString = "(";
                for (String word : str) {
                    andString = andString.concat(word + "+AND+");
                }
                andString = removeCharsFromEnd(andString, 5);
                andString = andString.concat(")");
                stringToAdd = stringToAdd.concat(andString);
            }
            stringToAdd = stringToAdd.concat("+OR+");
            index++;
        }
        stringToAdd = removeCharsFromEnd(stringToAdd, 4);
        updatedQuery = updatedQuery.concat("&q=" + stringToAdd);
        return updatedQuery;
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

    public static JsonNode getCategories() {
        String url = "categories.json?order=name";
        return eventFindaGetResponse(url);
    }


    private static String removeCharsFromEnd(String str, int numOfChars) {
        return str.substring(0, str.length() - numOfChars);
    }

    public static void main(String [] args) {
        System.out.println(addKeyWordFilterToQuery("currentUrl", "cycling    Southland    ,   running, alpha"));
    }
}


