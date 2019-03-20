package controllers;

import models.User;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import views.html.home.home;

import javax.inject.Inject;

import static play.mvc.Results.*;

public class HomeController {

    @Inject
    FormFactory formFactory;

    /**
     * The home page where currently users can access other creation pages (also displays their profile).
     * If the user has completed his profile, renders the home page.
     * If the user has not completed his profile, renders the profile creation page.
     * If the user it not logged in (doesn't have a login session), display error message.
     * @param request the HTTP request
     * @return homepage, profile page or error page
     */
    public Result showhome(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user != null){
            if(user.hasEmptyField()){
                return redirect(routes.ProfileController.createprofile());
            } else if (! user.hasTravellerTypes()) {
                return redirect(routes.TravellerTypeController.updateTravellerType());
            } else if(! user.hasNationality()){
                return redirect(routes.ProfileController.updateNatPass());
            }
            else {
                return ok(home.render(user));
            }
        }
        return unauthorized("Oops, you are not logged in");
    }
}
