package controllers;

import models.Admin;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;


import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class AdminControllerTest extends WithApplication {

    /**
     * The fake database.
     */
    Database database;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    /**
     * Sets up the fake database before each test.
     */
    @Before
    public void setUpDatabase() {
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));


        //Initialises test users and default admin and saves it to the database.
        User user = new User("testAdmin");
        user.save();
        Admin admin = new Admin(1, true);
        admin.save();
        User user1 = new User("testUser1");
        user1.save();
        User user2 = new User("testUser2");
        user2.save();
    }

    /**
     * Clears the fake database after each test.
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    /**
     * Test to render destination index with no login session.
     */
    @Test
    public void indexAdminWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to render destination index with a login session.
     */
    @Test
    public void indexAdminWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to make a User an Admin without a login session.
     */
    @Test
    public void userToAdminWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/make/2").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to make a User an Admin by the default admin with a login session.
     */
    @Test
    public void userToAdminWithLoginSession() {
        assertEquals(1, Admin.find.all().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET).uri("/users/admin/make/2")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(2, Admin.find.all().size());
    }

    /**
     * Test to make a User an Admin by a non-admin user with a login session.
     */
    @Test
    public void userToAdminByNormalUser() {
        assertEquals(1, Admin.find.all().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET).uri("/users/admin/make/3")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(1, Admin.find.all().size());
    }

    /**
     * Test to make a non-admin User an Admin by itself with a login session.
     */
    @Test
    public void userToAdminByUserItself() {
        assertEquals(1, Admin.find.all().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET).uri("/users/admin/make/2")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(1, Admin.find.all().size());
    }

    /**
     * Test to make an Admin an Admin by itself with a login session.
     */
    @Test
    public void userToAdminByAdminItself() {
        assertEquals(1, Admin.find.all().size());
        Admin admin = new Admin(2,false);
        admin.save();
        assertEquals(2, Admin.find.all().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET).uri("/users/admin/make/2")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(2, Admin.find.all().size());
    }

    /**
     * Test to remove Admin rights from an User without a login session.
     */
    @Test
    public void adminToUserWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/remove/2").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to remove Admin rights from an User with a login session.
     */
    @Test
    public void adminToUserWithLoginSession() {
        userToAdminWithLoginSession();
        assertEquals(2, Admin.find.all().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET).uri("/users/admin/remove/2")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(1, Admin.find.all().size());
    }

    /**
     * Test to remove Admin rights from the default admin without a login session.
     */
    @Test
    public void defaultAdminToUserWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/remove/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to remove Admin rights from the default admin with a login session.
     */
    @Test
    public void defaultAdminToUserWithLoginSession() {
        assertEquals(1, Admin.find.all().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET).uri("/users/admin/remove/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(1, Admin.find.all().size());
    }


    /**
     * Test to remove Admin rights from an admin by a normal user with a login session.
     */
    @Test
    public void adminToUserByNormalUser() {
        assertEquals(1, Admin.find.all().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET).uri("/users/admin/remove/3")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(1, Admin.find.all().size());
    }

    /**
     * Test to remove Admin rights from an admin user by itself with a login session.
     */
    @Test
    public void adminToUserByAdminItself() {
        assertEquals(1, Admin.find.all().size());
        Admin admin = new Admin(2,false);
        admin.save();
        assertEquals(2, Admin.find.all().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET).uri("/users/admin/remove/2")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(2, Admin.find.all().size());
    }

}
