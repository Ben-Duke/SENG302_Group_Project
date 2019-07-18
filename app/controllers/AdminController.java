package controllers;

import models.Admin;
import models.DestinationModificationRequest;
import models.User;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.destination.*;
import views.html.users.profile.*;

import javax.inject.Inject;
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
        List<User> userList = User.getCurrentUser(request, true);
        if (!userList.isEmpty()) {
            User currentUser = userList.get(0);
            if (currentUser != null) {
                Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.getUserid()).findOne();
                if (currentAdmin != null) {
                    List<User> users = User.find().all();
                    List<DestinationModificationRequest> allReqs = DestinationModificationRequest.find.all();
                    return ok(indexAdmin.render(currentUser, userList.get(1), users,allReqs));
                }
            } else {
                return redirect(routes.UserController.userindex());
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
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.getUserid()).findOne();
            if (currentAdmin != null && !currentAdmin.getUserId().equals(requestedUserId)) {
                Admin admin1 = Admin.find.query().where().eq("userId", requestedUserId).findOne();
                if (admin1 != null && !admin1.isDefault()) {
                    admin1.delete();
                    return redirect(routes.AdminController.indexAdmin());
                } else {
                    return unauthorized("You can not revoke default admin's rights.");
                }
            }
        }
        return redirect(routes.UserController.userindex());
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
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.getUserid()).findOne();
            if (currentAdmin != null && !currentAdmin.getUserId().equals(requestedUserId)) {
                Admin admin = new Admin(requestedUserId, false);
                admin.insert();
                return redirect(routes.AdminController.indexAdmin());
            }
        }
        return redirect(routes.UserController.userindex());
    }

    public Result viewDestinationModificationRequest(Http.Request request, Integer destModReqId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.getUserid()).findOne();
            if (currentAdmin != null) {
                DestinationModificationRequest modReq = DestinationModificationRequest.find.query().where().eq("id", destModReqId).findOne();
                if (modReq != null) {
                    User user = User.find().byId(currentAdmin.getUserId());
                    return ok(viewDestinationModificationRequest.render(modReq, user));
                } else {
                    return notFound("Destination Modification Request does not exist");
                }
            } else {
                return unauthorized("Oops, you are not authorised.");
            }
        } else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Sets the user that the admin wants to act as.
     * Returns to the user's home page.
     * @param request the HTTP request
     * @param userId the user id of the user that the admin wants to act as
     * @return redirect if the request user is an admin, unauthorized otherwise
     */
    public Result setUserToActAs(Http.Request request, Integer userId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null && currentUser.userIsAdmin()) {
            User userToEdit = User.find().byId(userId);
            List<Admin> adminList = Admin.find.query().where()
                    .eq("userId", currentUser.getUserid()).findList();
            if(adminList.size() == 1) {
                Admin admin = adminList.get(0);
                admin.setUserToEdit(userToEdit.getUserid());
                admin.update();
            }
            return redirect(routes.HomeController.showhome());
        } else {
            return unauthorized("Unauthorized: You are not an Admin");
        }

    }

    /**
     * If the admin is acting as a user, sets the user back to the admin.
     * Returns to the admin's home page.
     * @param request the HTTP request
     * @param adminsUserId the user id of the admin
     * @return redirect if the request user is an admin, unauthorized otherwise
     */
    public Result setUserBackToAdmin(Http.Request request, Integer adminsUserId) {
        List<User> users = User.getCurrentUser(request, true);
        if(users.size() == 0){
            return redirect(routes.UserController.userindex());
        }
        User currentUser = users.get(1);

        if (currentUser != null && currentUser.userIsAdmin() && currentUser.getUserid() == adminsUserId) {
            User adminUser = User.find().byId(adminsUserId);
            List<Admin> adminList = Admin.find.query().where()
                    .eq("userId", adminUser.getUserid()).findList();
            if(adminList.size() == 1) {
                Admin admin = adminList.get(0);
                admin.setUserToEdit(null);
                admin.update();
            }
            return redirect(routes.HomeController.showhome());
        } else {
            return unauthorized("Unauthorized: You are not an Admin");
        }

    }
}
