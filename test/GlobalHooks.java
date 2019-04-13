import cucumber.api.java.After;
import cucumber.api.java.Before;
import models.User;
import org.junit.Assert;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import utilities.TestDatabaseManager;

public class GlobalHooks {
    /**
     * The fake database
     */
//    public static Database database;
//
//    @Override
//    protected Application provideApplication() {
//        return new GuiceApplicationBuilder().build();
//    }

//    @Before
//    public void before() {
//        database = Databases.inMemory();
//        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
//                1,
//                "create table test (id bigint not null, name varchar(255));",
//                "drop table test;"
//        )));
//        User user = new User("testUser");
//        user.save();
//        User user2 = new User("testUser2");
//        user2.save();
//        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
//        testDatabaseManager.populateDatabase();
//        Assert.assertEquals(3, User.find.all().size());
//    }
//
//    @After
//    public void after() {
//        Evolutions.cleanupEvolutions(database);
//        database.shutdown();
//    }
}
