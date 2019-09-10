package models;

import accessors.UserAccessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Follow extends BaseModel{

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "follower", referencedColumnName = "userid")
    private User follower;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="followed", referencedColumnName = "userid")
    private User followed;

    @Id
    private Integer followId;

    private static Finder<Integer,Follow> find = new Finder<>(Follow.class, ApplicationManager.getDatabaseName());

    /**
     * Returns this user objects as a string with it's parameters
     * @return The user objects attributes as a string
     */
    /**
     * Method to get EBeans finder object for queries.
     *
     * @return a Finder<Integer, User> object.
     */
    public static Finder<Integer, Follow> find() {
        return find;
    }


    public Follow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }

    public Integer getFollowId() {
        return followId;
    }

    public User getFollower() {
        return this.follower;
    }

    public User getFollowed() {
        return this.followed;
    }

    public int getFolowerUserId(){
        return this.follower.getUserid();
    }

    public int getFolowedUserId(){
        return this.followed.getUserid();
    }
}
