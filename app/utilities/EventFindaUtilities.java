package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.databind.JsonNode;
//import org.apache.commons.lang3.tuple.Pair;
import models.Destination;
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

    public static JsonNode getEvents(String keyword, String category, String artist, String startDate,
                                     String endDate, String minPrice, String maxPrice, Destination destination, String sortBy) {

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

    public static JsonNode getCategories() {
        String url = "categories.json?order=name";
        return eventFindaGetResponse(url);
    }


    private static String addLocationFilterToQuery() {
        return null;
    }

    private static String addArtistFilterToQuery() {
        return null;
    }

    private static String addCategoryFilterToQuery() {
        return null;
    }

    /**
     * Filter by keyword, implemented using the auto complete parameter
     * @param keyword the keyword to filter by
     * @return the updated query
     */
    private static String addKeywordFilterToQuery(String currentQuery, String keyword) {
        return currentQuery + "autocomplete=" + keyword + "&";
    }

    /**
     * Filter by free text (Check the q parameter in https://www.eventfinda.co.nz/api/v2/end-points)
     * The free text should be a Pair of a list of a list of pairs of strings and a string.
     * The left value of the pair should be the keywords and the right value should be the conjunction.
     * If there are no conjunctions (its the last string) it should be left as an empty string.
     * Example:
     * Filter by (cycling and running and swimming) or triathlon should be parsed as the list:
     * [Pair<[Pair<"cycling", "and">, Pair<"running", "and">, Pair<"swimming, "">], "or">, Pair<[Pair<"triathlon", ""], "">>]
     *
     * @param currentQuery the current query string
     * @param freeText the free text, which is a pair of a list of a list of pairs of strings and a string.
     * @return the updated query
     */
//    private static String addFreeTextFilterToQuery(String currentQuery, List<Pair<List<Pair<String, String>>,String>> freeText) {
//        String updatedQuery = currentQuery;
//        updatedQuery += "q=";
//        for (Pair<List<Pair<String, String>>,String> bracketPair : freeText) {
//            List<Pair<String, String>> bracketStrings = bracketPair.getLeft();
//            String bracketConjunction = bracketPair.getRight();
//
//            updatedQuery += "(";
//            for (Pair<String,String> bracketString : bracketStrings) {
//                updatedQuery += bracketString.getLeft();
//                updatedQuery += "+";
//                updatedQuery += bracketString.getRight();
//                updatedQuery += "+";
//            }
//            updatedQuery = removeLastChar(updatedQuery);
//            updatedQuery += ")+";
//            updatedQuery += bracketConjunction;
//        }
//        updatedQuery = removeLastChar(updatedQuery);
//        return updatedQuery;
//    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }
}


