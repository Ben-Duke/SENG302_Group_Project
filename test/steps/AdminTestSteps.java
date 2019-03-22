package steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import models.Admin;

public class AdminTestSteps {

    Database database;

    @Before
    public void setUp() {
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
    }

    @After
    public void tearDown() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    @Given("there is a default administrator with userId={int}")
    public void thereIsADefaultAdministratorWithUserId(Integer int1) {
        Admin admin = Admin.find.byId(int1);
        if (admin != null) {
            Assert.assertTrue(admin.getIsDefault());
        } else {
            Assert.fail();
        }
    }

    @When("I try to delete the default administrator with userId={int}")
    public void iTryToDeleteTheDefaultAdministratorWithUserId(Integer int1) {
        Admin admin = Admin.find.byId(int1);
        if (admin != null) {
            Boolean deleteSuccess = admin.deleteAdmin();
            Assert.assertFalse(deleteSuccess);
        }
    }

    @Then("an error message is shown advising me that I can't delete the default admin with userId={int}")
    public void anErrorMessageIsShownAdvisingMeThatICanTDeleteTheDefaultAdminWithUserId(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the default administrator with userId={int} is not deleted")
    public void theDefaultAdministratorWithUserIdIsNotDeleted(Integer int1) {
        Admin admin = Admin.find.byId(int1);
        Assert.assertNotNull(admin);
    }

    @Given("there is no default administrator")
    public void thereIsNoDefaultAdministrator() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the system starts up")
    public void theSystemStartsUp() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("a default administrator will be created with username {string} and password {string}")
    public void aDefaultAdministratorWillBeCreatedWithUsernameAndPassword(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("there is a default administrator")
    public void thereIsADefaultAdministrator() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("no new default administrator is created")
    public void noNewDefaultAdministratorIsCreated() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }
}
