package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;

import javax.persistence.*;
import java.util.List;

@Entity
public class TreasureHunt {

    /**
     * The id of the treasure hunt
     */
    @Id
    public Integer thuntid;

    /**
     * The title of the treasure hunt
     */
    public String title;

    /**
     * The treasure hunt's riddle
     */
    public String riddle;

    /**
     * The destination that is the correct answer to the treasure hunt
     */
    public Destination destination;

    /**
     * The starting date of the treasure hunt
     */
    public String startDate;

    /**
     * The end date of the treasure hunt;
     */
    public String endDate;

    /**
     * The owner of the treasure hunt;
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    /**
     * The users who have made a guess
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "guessedTHunts")
    public List<User> users;

    public static Finder<Integer,TreasureHunt> find = new Finder<>(TreasureHunt.class);

    public Integer getThuntid() {
        return thuntid;
    }

    public void setThuntid(Integer thuntid) {
        this.thuntid = thuntid;
    }

    public String getRiddle() {
        return riddle;
    }

    public void setRiddle(String riddle) {
        this.riddle = riddle;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
