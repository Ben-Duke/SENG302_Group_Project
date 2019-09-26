import org.junit.Test;
import org.slf4j.Logger;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.UtilityFunctions;

import static org.junit.Assert.*;

public class DatabaseTest extends BaseTestWithApplicationAndDatabase {
    private Logger logger = UtilityFunctions.getLogger();

    /* Sanity check that testing architecture is functioning properly */
    @Test
    public void test() {
        assertTrue(true);
    }
}
