package controllers;

import accessors.FollowAccessor;
import accessors.UserAccessor;
import models.Follow;
import models.User;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import static play.mvc.Results.*;

public class FollowController {

    /**
     * Follow the user with given Id
     * @param request the http request
     * @param userIdToFollow the id of the user to follow
     * @return the http status according to what action has been made
     */
    public Result followUser(Http.Request request, Integer userIdToFollow) {
        User user = User.getCurrentUser(request);
        User userToFollow = UserAccessor.getById(userIdToFollow);

        if (user == null) { return redirect(routes.UserController.userindex()); }
        if (userToFollow == null) { return badRequest();}

        Follow newFollow = user.follow(userToFollow);
        FollowAccessor.insert(newFollow);
        UserAccessor.update(user);
        UserAccessor.update(userToFollow);


        return ok();
    }

    /**
     * Unfollow the user with given Id
     * @param request the http request
     * @param userIdToUnfollow the id of the user to ufollow
     * @return the http status according to what action has been made
     */
    public Result unfollowUser(Http.Request request, Integer userIdToUnfollow) {
        User user = User.getCurrentUser(request);
        User userToUnfollow = UserAccessor.getById(userIdToUnfollow);

        if (user == null) { return redirect(routes.UserController.userindex()); }
        if (userToUnfollow == null) { return badRequest();}

        Follow removedFollow = user.unfollow(userToUnfollow);
        if (removedFollow == null) {
            return badRequest();
        }
        UserAccessor.update(user);
        UserAccessor.update(userToUnfollow);
        FollowAccessor.delete(removedFollow);
        return ok();


    }

    public Result isFollowing(Http.Request request, int profileId) {
        User user = User.getCurrentUser(request);
        User other = UserAccessor.getById(profileId);
        if (FollowAccessor.follows(user, other)) {
            return ok(Json.toJson(true));
        }
        return ok(Json.toJson(false));
    }

    public Result isFollowed(Http.Request request, int profileId) {
        User user = User.getCurrentUser(request);
        User other = UserAccessor.getById(profileId);
        if (FollowAccessor.follows(other, user)) {
            return ok(Json.toJson(true));
        }
        return ok(Json.toJson(false));
    }

}
