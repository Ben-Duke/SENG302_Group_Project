package accessors;

import models.Follow;
import models.User;

import java.util.List;

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

    public static boolean follows(User follower, User followed) {
        return Follow.find().query().where().eq("follower", follower).eq("followed",followed) != null;
    }
}
