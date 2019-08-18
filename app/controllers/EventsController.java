package controllers;

import models.User;
import models.commands.General.CommandPage;
import play.mvc.Http;
import play.mvc.Result;
import utilities.CountryUtils;
import views.html.home.home;
import views.html.users.events.eventSearch;

import java.util.List;

import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class EventsController {

    public Result indexEvents(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user == null) { return redirect(routes.UserController.userindex()); }

        return ok(eventSearch.render(user));
    }
}
