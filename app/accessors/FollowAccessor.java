package accessors;

import models.Follow;
import models.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FollowAccessor {
    public static Follow getFollowById(int id) {
        return Follow.find().byId(id);
    }


    public static void insert(Follow follow) { follow.save(); }

    public static void delete(Follow follow) { follow.delete(); }

    public static void update(Follow follow) { follow.update(); }

    public static List<Follow> getAll() {
        return Follow.find().all();
    }

    public static Set<User> getAllUsersFollowed(User follower) {
        Set<Follow> FollowSet = Follow.find().query().where().eq("follower", follower).findSet();
        Set<User> followedUser = new HashSet<>();
        for (Follow follow: FollowSet) {
            followedUser.add(follow.getFollowed());
        }
        return followedUser;
    }

    public static Set<User> getAllUsersFollowing(User follower) {
        Set<Follow> FollowSet = Follow.find().query().where().eq("followed", follower).findSet();
        Set<User> followedUser = new HashSet<>();
        for (Follow follow: FollowSet) {
            followedUser.add(follow.getFollower());
        }
        return followedUser;
    }

    public static boolean follows(User follower, User followed) {
        return Follow.find().query().where().eq("follower", follower).eq("followed",followed).findSet().size() != 0;
    }
}
