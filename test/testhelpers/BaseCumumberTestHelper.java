package testhelpers;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import controllers.ApplicationManager;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import utilities.TestDatabaseManager;

import javax.inject.Inject;

public class BaseCumumberTestHelper extends BaseTestWithApplicationAndDatabase {
    private TestDatabaseManager testDatabaseManager = new TestDatabaseManager();

    @Inject
    private static Application application;

    @Before
    public void setup(){
        super.setupDatabase();

        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
            }
        };
        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);

        Helpers.start(application);
        ApplicationManager.setIsTest(true);
        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");

    }

    @After
    public void tearDown(){
        Helpers.stop(application);
    }

    public static Application getApplication() {
        return application;
    }
}
