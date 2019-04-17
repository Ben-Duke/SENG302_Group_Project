package controllers;

import models.Admin;
import models.DestinationModificationRequest;
import models.User;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.destination.viewDestinationModificationRequest;
import views.html.users.profile.indexAdmin;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class AdminController extends Controller {

    @Inject
    FormFactory formFactory;


    /**
     * Gets all the users and admins renders the index Admin page displaying them.
     *
     * @param request the http request
     * @return the admin index page which displays all the users and admins.
     * or an unauthorized message is "Oops, you are not authorised.".
     */
    public Result indexAdmin(Http.Request request) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.userid).findOne();
            if (currentAdmin != null) {
                List<Admin> admins = Admin.find.all();
                List<User> users = User.find.all();
                List<User> adminUsers = new ArrayList<>();
                for (int i = 0; i < admins.size(); i++) {
                    User user1 = User.find.byId(admins.get(i).userId);
                    users.remove(user1);
                    adminUsers.add(user1);
                }
                List<DestinationModificationRequest> allReqs = DestinationModificationRequest.find.all();
                return ok(indexAdmin.render(currentUser, users, admins, adminUsers, allReqs));
            }

        }
        return unauthorized("Oops, you are not authorised.");
    }

    /**
     *  The method to remove a user from the admins and re-render the index Admin.
     *
     * @param request The http request
     * @param requestedUserId The user id of the Admin being removed.
     * @return the admin index page which displays all the users and admins.
     * or an unauthorized message is "Oops, you are not authorised.".
     */
    public Result adminToUser(Http.Request request, Integer requestedUserId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.userid).findOne();
            if (currentAdmin != null && currentAdmin.userId != requestedUserId) {
                Admin admin1 = Admin.find.query().where().eq("userId", requestedUserId).findOne();
                admin1.delete();
                return redirect(routes.AdminController.indexAdmin());
            }
        }
        return unauthorized("Oops, you are not authorised.");
    }

    /**
     *  The method to make a user an admin and re-render the index Admin.
     *
     * @param request The http request
     * @param requestedUserId The user id of the User being made Admin.
     * @return the admin index page which displays all the users and admins.
     * or an unauthorized message is "Oops, you are not authorised.".
     */
    public Result userToAdmin(Http.Request request, Integer requestedUserId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.userid).findOne();
            if (currentAdmin != null && currentUser.userid != requestedUserId) {
                Admin admin = new Admin(requestedUserId, false);
                admin.insert();
                return redirect(routes.AdminController.indexAdmin());
            }
        }
        return unauthorized("Oops, you are not authorised.");
    }

    public Result viewDestinationModificationRequest(Http.Request request, Integer destModReqId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.userid).findOne();
            if (currentAdmin != null) {

                DestinationModificationRequest modReq = DestinationModificationRequest.find.query().where().eq("id", destModReqId).findOne();
                if (modReq != null) {

                    return ok(viewDestinationModificationRequest.render(modReq));
                } else {
                    return badRequest("Destination Modification Request does not exist");
                }
            } else {
                return unauthorized("Oops, you are not authorised.");
            }
        } else {
            return unauthorized("Oops, you are not logged in.");
        }
    }
}