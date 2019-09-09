package controllers;

import accessors.FollowAccessor;
import accessors.UserAccessor;
import models.Follow;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.*;

import static java.util.Arrays.copyOfRange;
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

    /**
     *
     * @param userId
     * @param offset
     * @return
     */
    public Result getFollowers(Http.Request request, int userId, int offset){
        User user = UserAccessor.getById(userId);
        if(user != null){
            List<Follow> followList = user.getFollowers();

            int followCount = followList.size();
            if (offset > 0){
                return unauthorized("Place holder");
            }else{
                if(followCount < 10){
                    List<String> users = new ArrayList<String>();
                    //for(Follow follower:followList.subList(0, 9)){
                    for(Follow follower:followList){
                       users.add( UserAccessor.getJsonReadyStringOfUser(follower.getFolowerUserId()));

                    }

                    return ok(Json.toJson( users));
                }
                else{
                    return ok(followList.toString());
                }

            }
        }
        else{
            return notFound();
        }

    }
}
