package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import models.TravellerType;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;
import java.util.regex.*;

import static org.junit.Assert.*;

public class EventFindaUtilitiesTest  extends BaseTestWithApplicationAndDatabase {

    @Override
    public void populateDatabase() {
        // Clear the database of all data
        // validateValidPassportCountry requires passport table being empty

        TestDatabaseManager.clearAllData();
    }

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
        //TODO: FIX THIS
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

}
