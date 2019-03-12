package controllers;

import formdata.DestinationFormData;
import formdata.LoginFormData;
import formdata.TravellerFormData;
import models.Destination;
import models.Traveller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.collection.TraversableView;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private final Form<TravellerFormData> travellerForm;
    private final Form<LoginFormData> loginForm;
    private final Form<DestinationFormData> destinationForm;
    private final Logger logger = LoggerFactory.getLogger("application");
    private static List<Traveller> travellers = new ArrayList<>();
    private static Traveller currentTraveller = new Traveller();



    // populate traveller list
    static {
        ArrayList<String> countries = new ArrayList<>();
        countries.add("New Zealand");
        ArrayList<String> nationalities = new ArrayList<>();
        nationalities.add("Australia");
        String dob = "2018-05-03";
        Traveller t1 = new Traveller("nbi21", "123", "Noel", "Bisson", "Male", countries, nationalities, dob);
        travellers.add(t1);
    }

    public static Traveller getCurrentTraveller() {
        return currentTraveller;
    }

    public static List<Traveller> getTravellers() {
        return travellers;
    }

    @Inject
    public HomeController(FormFactory formFactory) {
        travellerForm = formFactory.form(TravellerFormData.class);
        loginForm = formFactory.form(LoginFormData.class);
        destinationForm = formFactory.form(DestinationFormData.class);
    }

    /**
     * Returns the page with a form to register a new traveller and their details
     *
     * @return The page containing the create traveller form.
     */
    public Result registerTraveller() {
        TravellerFormData travellerFormData =  new TravellerFormData();
        travellerForm.fill(travellerFormData);
        Date today = new Date();
        today.setTime(today.getTime());
        logger.debug("getIndex");
        return ok(views.html.containers.profile.register.render(travellerForm, Traveller.getGenderList(), today.toString(), Traveller.getCountryList()));
    }

    /**
     * Returns the page with a comfirmation that the traveller is registered.
     *
     * @return A view that redirects the traveller to the complete registration page.
     */

    public Result registrationComplete(Http.Request request){
        Form<TravellerFormData> incomingForm = travellerForm.bindFromRequest(request);
        if (incomingForm.hasErrors()) {
            Date today = new Date();
            today.setTime(today.getTime());
            return badRequest(views.html.containers.profile.register.render(incomingForm, Traveller.getGenderList(), today.toString(), Traveller.getCountryList()));
        } else {
            TravellerFormData created = incomingForm.get();
            Traveller createdTraveller = Traveller.makeInstance(created);
            travellers.add(createdTraveller);
            currentTraveller = createdTraveller;
            logger.debug(incomingForm.get().toString());
            return ok(views.html.components.profile.viewProfile.render(createdTraveller));
        }
    }

    /**
     * Returns the login result page, whether there is an error or it is successful
     * @param request the http request
     * @return The corresponding view relating to login success or failure
     */
    public Result submitLogin(Http.Request request) {
        Form<LoginFormData> incomingForm = loginForm.bindFromRequest(request);
        if (incomingForm.hasErrors()) {
            return badRequest(views.html.containers.login.loginhome.render(incomingForm));
        } else {
            LoginFormData created = incomingForm.get();

            currentTraveller = ProfileController.findTraveller(created.username);
            return redirect(routes.ProfileController.viewProfile(currentTraveller.getUsername()));
        }
    }

    /**
     * Returns the page to log in.
     * @return A view that the traveller uses to login
     */
    public Result loginHome() {
        return ok(views.html.containers.login.loginhome.render(loginForm));
    }

    /**
     * Returns the create destination page
     * @return A view that the traveller uses to create a destination
     */
    public Result createDestination() {
        Date today = new Date();
        return ok(views.html.containers.destinations.createdestination.render(destinationForm, DestinationFormData.getTypeList(), today.toString(), Traveller.getCountryList(), currentTraveller));
    }

    /**
     * Returns the result of the destination creation, a failure from form errors or success
     * @param request the http request
     * @return A view that corresponds to the form actions
     */
    public Result submitDestination(Http.Request request) {
        Date today = new Date();
        Form<DestinationFormData> incomingForm = destinationForm.bindFromRequest(request);
        if (incomingForm.hasErrors()) {
            return badRequest(views.html.containers.destinations.createdestination.render(incomingForm, DestinationFormData.getTypeList(), today.toString(), Traveller.getCountryList(), currentTraveller));
        } else {
            Destination newDestination = Destination.makeInstance(incomingForm.get());
            currentTraveller.getDestinations().add(newDestination);
            return ok(views.html.components.profile.viewProfile.render(currentTraveller));
        }
    }

    /**
     * Returns the view destinations page for a particular traveller
     * @param username the user whose destinations are being viewed
     * @return the view destinations page
     */
    public Result viewDestinations(String username) {
        Traveller newTraveller = ProfileController.findTraveller(username);
        return ok(views.html.containers.destinations.viewDestinations.render(newTraveller));
    }

    /**
     * Returns the view all profiles page for all travellers
     * @return the view all profiles page
     */
    public Result viewAllProfiles() {
        return ok(views.html.components.profile.completeregister.render(travellers));
    }

    /** Redirect to login from root **/
    public Result getRoot() {
        return redirect(routes.HomeController.loginHome());
    }
}