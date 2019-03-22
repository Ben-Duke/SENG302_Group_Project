package controllers;

import factories.LoginFactory;
import formdata.LoginFormData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.login_page.loginPage;

import javax.inject.Inject;

import static play.mvc.Results.*;

/**
 * A controller class for handing user logins.
 */
public class LoginController {

    @Inject
    FormFactory formFactory;
    private final Logger logger = LoggerFactory.getLogger("application");

    /**
     * Renders the login page where the user can log in.
     *
     * @return The login page.
     */
    public Result login(){
        Form<LoginFormData> loginFormData = formFactory.form(LoginFormData.class);
        return ok(loginPage.render(loginFormData));
    }


    /**
     * Handles the user's login request.
     * If the username is not found in the database then returns an error.
     * If the username is found but the password does not match then returns an error.
     * If the username and password matches then stores the user's login session
     *      and redirects the user to the home page.
     * @param request The HTTP request
     * @return The home page or login error page
     */
    public Result loginrequest(Http.Request request)
    {
        Form<LoginFormData> userLoginForm = formFactory.form(LoginFormData.class)
                                            .bindFromRequest();
        if (userLoginForm.hasErrors()) {
            // redirect user to same login page with some errors.
            return badRequest(loginPage.render(userLoginForm));
        } else {
            String username = userLoginForm.get().username;
            logger.debug("---"+ LoginFactory.getUserId(username));
            return redirect(routes.HomeController.showhome())
                    .addingToSession(request, "connected",
                            Integer.toString(LoginFactory.getUserId(username)));
        }


    }

    /**
     * Logs the user out by removing them from the session and redirecting them
     * to the user index page.
     *
     * @param request The HTTP request
     * @return The user index page
     */
    public Result logoutrequest(Http.Request request){
        return redirect(routes.UserController.userindex())
                              .removingFromSession(request, "connected");
    }
}
