package controllers;

import models.Admin;
import models.User;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.profile.indexAdmin;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class AdminController extends Controller {

    @Inject
    FormFactory formFactory;


    /**
     * Gets all the current users destinations renders the index page displaying them.
     *
     * @param request the http request
     * @return the destinations index page which displays all the users destinations
     * or an unauthorized message is no user is logged in.
     */
    public Result indexAdmin(Http.Request request) {
        User currentUser = User.getCurrentUser(request);
        Admin currentAdmin = Admin.find.byId(currentUser.userid);
        if (currentUser != null && currentAdmin != null) {
            List<Admin> admins = Admin.find.all();
            List<User> users = User.find.all();
            List<User> adminUsers = new ArrayList<>();
            for (int i = 0; i < admins.size(); i++) {
                User user1 = User.find.byId(admins.get(i).userId);
                users.remove(user1);
                adminUsers.add(user1);
            }
            return ok(indexAdmin.render(currentUser, users, admins, adminUsers));
        }
        return unauthorized("Oops, you are not authorised.");
    }

    public Result adminToUser(Http.Request request, Integer userId) {
        User currentUser = User.getCurrentUser(request);
        Admin currentAdmin = Admin.find.byId(currentUser.userid);
        if (currentUser != null && currentAdmin != null && currentAdmin.userId != userId) {
            Admin admin1 = Admin.find.query().where().eq("userId", userId).findOne();
            admin1.delete();
            return redirect(routes.AdminController.indexAdmin());
        }
        return unauthorized("Oops, you are not authorised.");
    }

    public Result userToAdmin(Http.Request request, Integer userId) {
        User currentUser = User.getCurrentUser(request);
        Admin currentAdmin = Admin.find.byId(currentUser.userid);
        if (currentUser != null && currentAdmin != null && currentAdmin.userId != userId) {
            Admin admin = new Admin(userId, false);
            admin.insert();
            return redirect(routes.AdminController.indexAdmin());
        }
        return unauthorized("Oops, you are not authorised.");
    }
}