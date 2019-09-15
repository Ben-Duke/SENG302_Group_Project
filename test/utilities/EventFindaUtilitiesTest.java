package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import models.Destination;
import models.TravellerType;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.Map;
import java.util.regex.*;

import static org.junit.Assert.*;

public class EventFindaUtilitiesTest  extends BaseTestWithApplicationAndDatabase {


    @Test
    public void checkAtLeastOneItemEventFindaGetResponse(){
        JsonNode response = EventFindaUtilities.getEvents(43.5321,172.6362,"Christchurch", 0);
        assertFalse(Integer.parseInt(response.get("@attributes").get("count").toString()) == 0);
    }

    @Test
    public void CheckResponseItemEventFindaGetResponse(){
        JsonNode response = EventFindaUtilities.getEvents(43.5321,172.6362,"Christchurch", 0);
        JsonNode resultTest = response.get("events").get(0);
        String idCheck = resultTest.get("id").toString();
        String urlCheck = resultTest.get("url").toString();
        String url_slugCheck = resultTest.get("url_slug").toString();
        String nameCheck = resultTest.get("name").toString();
        assertTrue((idCheck != null) & (urlCheck != null) & (url_slugCheck != null) & (nameCheck != null));
    }

    @Test
    public void nullResultOfBadInputEventFindaGetResponse(){
        assertNull(EventFindaUtilities.getEvents(43.5321,172.6362,"VERY VERY bad input 78", 0));
    }

    @Test
    public void testForGetLocationId(){
        assertNotNull(EventFindaUtilities.getLocationId(37.0902, 95.7129, "America"));
    }
    @Test (expected = NullPointerException.class)
    public void testForBadInputGetLocationId() {
        assertNotNull(EventFindaUtilities.getLocationId(37.0902, 95.7129, "nknnknknk"));
    }


    @Test
    public void addKeyWordFilterToQuery() {
        String outputFromMethod = EventFindaUtilities.addKeyWordFilterToQuery("currentUrl",
                "cycling    Southland    ,   running, alpha");
        assertEquals("currentUrl&q=(cycling+AND+Southland)+OR+(running)+OR+(alpha)", outputFromMethod);
    }

    @Test
    public void addKeyWordFilterToQueryWithLeadingSpaces() {
        String outputFromMethod = EventFindaUtilities.addKeyWordFilterToQuery("currentUrl",
                "   cycling    Southland    ,   running, alpha");
        assertEquals("currentUrl&q=(cycling+AND+Southland)+OR+(running)+OR+(alpha)", outputFromMethod);
    }

    @Test
    public void addKeyWordFilterToQueryWithEmptyInputWithinKeywords() {
        String outputFromMethod = EventFindaUtilities.addKeyWordFilterToQuery("currentUrl",
                "   cycling  , ,  Southland    ,   running, alpha");
        assertEquals("currentUrl&q=(cycling)+OR+()+OR+(Southland)+OR+(running)+OR+(alpha)", outputFromMethod);
    }

    @Test
    public void addKeyWordFilterToQueryOneKeyword() {
        String outputFromMethod = EventFindaUtilities.addKeyWordFilterToQuery("currentUrl",
                "cycling");
        assertEquals("currentUrl&q=(cycling)", outputFromMethod);
    }


    @Test
    public void getLocations() {
        JsonNode result = EventFindaUtilities.getLocations(43.5321, 172.6362, "Christchurch", 0);
        assertTrue(result.get("locations").size() > 0);
    }

    @Test
    public void getMainCategories() {
        Map categoryIdsToName = EventFindaUtilities.getMainCategories();
        assertTrue(categoryIdsToName.size()==6);
    }

    @Test
    public void getEventsSearchTest() {
        Destination destination = Destination.find().query().where().eq("dest_name", "Christchurch").findOne();
        JsonNode result = EventFindaUtilities.getEvents("", "", "", "", "", "", destination, "",0);
        assertTrue(Integer.parseInt(result.get("@attributes").get("count").asText())>0);
    }

    @Test
    public void getEventsSearchBadTest() {
        Destination destination = Destination.find().query().where().eq("dest_name", "Lincoln Memorial").findOne();
        JsonNode result = EventFindaUtilities.getEvents("", "", "", "", "", "", destination, "",0);
        assertTrue(Integer.parseInt(result.get("@attributes").get("count").asText())==0);
    }

    @Test
    public void getEventsSearchByRandomAttributesTest() {
        Destination destination = Destination.find().query().where().eq("dest_name", "Auckland").findOne();
        JsonNode result = EventFindaUtilities.getEvents("", "1", "", "", "5", "20", destination, "",0);
        assertTrue(Integer.parseInt(result.get("@attributes").get("count").asText())>=0);
    }
}
