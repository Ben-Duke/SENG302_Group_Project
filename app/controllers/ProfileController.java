package controllers;

import models.Traveller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class ProfileController extends Controller {

    private final Logger logger = LoggerFactory.getLogger("application");

    public Result viewProfile(String username) {
        logger.debug("viewing profile");
        Traveller traveller = findTraveller(username);  // find the traveller to display

        return ok(views.html.components.profile.viewProfile.render(traveller));
    }


    /**
     * A function to find a specific traveller using a given username
     *
     * @return The username of the current traveller or selected traveller
     * it will return null.
     */
    public static Traveller findTraveller(String username) {
        List<Traveller> travellers = HomeController.getTravellers();
        for (Traveller traveller : travellers) {
            if (traveller.getUsername().equals(username)) {
                return traveller;
            }
        }
        return null;
    }
}
