package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TreasureHunt extends Model {


    /**
     * Constructor to create a treasure hunt
     * @param title the treasure hunt's title
     * @param riddle the treasure hunt's riddle
     * @param destination the correct destination that the users should be guessing
     * @param startDate when the treasure hunt opens
     * @param endDate when the treasure hunt closes
     */
    public TreasureHunt(String title, String riddle, Destination destination, String startDate, String endDate, User user){
        this.title = title;
        this.riddle = riddle;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.users = new ArrayList<>();
    }

    public TreasureHunt(TreasureHunt treasureHunt) {
        this(treasureHunt.getTitle(),
                treasureHunt.getRiddle(),
                treasureHunt.getDestination(),
                treasureHunt.getStartDate(),
                treasureHunt.getEndDate(),
                treasureHunt.getUser()
            );
    }

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
    @JsonIgnore
    @ManyToOne
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
