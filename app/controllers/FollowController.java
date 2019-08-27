package controllers;

import accessors.FollowAccessor;
import accessors.UserAccessor;
import models.Follow;
import models.User;
import play.mvc.Http;
import play.mvc.Result;

import static play.mvc.Results.*;

public class FollowController {

    public Result FollowUser(Http.Request request, User userToFollow) {
        User user = User.getCurrentUser(request);

        if (user == null) { return redirect(routes.UserController.userindex()); }
        if (userToFollow == null) { return badRequest();}

        Follow newFollow = new Follow(user, userToFollow);
        user.addToFollowing(newFollow);
        userToFollow.addToFollowers(newFollow);
        FollowAccessor.insert(newFollow);
        UserAccessor.update(user);
        UserAccessor.update(userToFollow);

        return ok();
    }

}
