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

    public Result isFollowing(Http.Request request, int profileId) {
        User user = User.getCurrentUser(request);
        User other = UserAccessor.getById(profileId);
        if (FollowAccessor.follows(user, other)) {
            return ok(Json.toJson(true));
        }
        return ok(Json.toJson(false));
    }



    /**
     * Returns the followers for a given user
     * @param request
     * @param userId
     * @return
     */
    public Result getFollowersCount(Http.Request request, int userId){
        //Stub
        return ok();
    }


    /**
     *Method returns a paginated list based on an offset, if the offset is 0 the first 10 are given.
     * With an offset the list will be items after he given offset
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
                int size =  followList.size();
                if ( size < offset){
                    return ok("Offset was larger than the amount of followers");
                }

                List<String> users = new ArrayList<>();
                //Used to get the end of the batch and to see if less than 10 should be given back
                int endCheck = offset + 11;
                if ( endCheck > size){
                    //Give back offset till end of follower list
                    for(Follow follower:followList.subList(offset, size)){
                        users.add( UserAccessor.getJsonReadyStringOfUser(follower.getFolowerUserId()));
                    }
                    return ok(Json.toJson( users ));
                }else{
                    //loop through till 10
                    for(Follow follower:followList.subList(offset, offset + 11)){
                        users.add( UserAccessor.getJsonReadyStringOfUser(follower.getFolowerUserId()));
                    }
                    return ok(Json.toJson( users ));
                }

            }else{
                List<String> users = new ArrayList<>();
                //With no offset give back the first 10
                if(followCount > 10){
                    for(Follow follower:followList.subList(0, 11)){
                        users.add( UserAccessor.getJsonReadyStringOfUser(follower.getFolowerUserId()));
                    }
                    return ok(Json.toJson( users ));
                }
                else{
                    //Reaching this point means there was less than 10 to give back so send all followers
                    for(Follow follower:followList){
                        users.add( UserAccessor.getJsonReadyStringOfUser(follower.getFolowerUserId()));
                    }
                    return ok(Json.toJson( users ));
                }
            }
        }
        else{
            return notFound();
        }

    }
}