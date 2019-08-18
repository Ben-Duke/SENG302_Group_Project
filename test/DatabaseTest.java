import models.Nationality;
import models.User;
import org.junit.Test;
import org.slf4j.Logger;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DatabaseTest extends BaseTestWithApplicationAndDatabase {

    private final Logger logger = UtilityFunctions.getLogger();


    @Override
    public void populateDatabase() {
        TestDatabaseManager.clearAllData();
        User user = new User("Noel");
        user.save();

        List<User> users = User.find().all();
    }

    @Test
    public void test() {
        Nationality nat = new Nationality("Hello");
        nat.save();

        List<Nationality> nats = Nationality.find().all();

        assertTrue(true);
    }
}


