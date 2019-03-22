package steps;

import backdoor.Backdoor;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AdminTestSteps {


    @Given("there is no default administrator")
    public void thereIsNoDefaultAdministrator() {
        Backdoor.deleteDefaultAdmin();
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
