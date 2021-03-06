package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import play.data.format.Formats;
import utilities.UtilityFunctions;
import utilities.exceptions.EbeanDateParseException;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Model class for treasure hunt construction */
@Entity
public class TreasureHunt extends BaseModel {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * The id of the treasure hunt
     */
    @Id
    private Integer thuntid;

    /**
     * The title of the treasure hunt
     */
    private String title;

    /**
     * The treasure hunt's riddle
     */
    private String riddle;

    /**
     * The destination that is the correct answer to the treasure hunt
     */
    @ManyToOne
    private Destination destination;

    /**
     * The starting date of the treasure hunt
     */
    @Temporal(TemporalType.DATE)
    @Formats.DateTime(pattern = DATE_PATTERN)
    private LocalDate startDate;

    /**
     * The end date of the treasure hunt;
     */
    @Temporal(TemporalType.DATE)
    @Formats.DateTime(pattern = DATE_PATTERN)
    private LocalDate endDate;

    /**
     * The owner of the treasure hunt;
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    private User user;

    /**
     * The users who have made a guess
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "guessedTHunts")
    private List<User> users;

    private static Finder<Integer, TreasureHunt> find = new Finder<>(TreasureHunt.class,
            ApplicationManager.getDatabaseName());


    /**
     * Default Constructor
     */
    public TreasureHunt() {
    }

    /**
     * Constructor to create a treasure hunt
     *
     * @param title       the treasure hunt's title
     * @param riddle      the treasure hunt's riddle
     * @param destination the correct destination that the users should be guessing
     * @param startDate   when the treasure hunt opens
     * @param endDate     when the treasure hunt closes
     */
    public TreasureHunt(String title, String riddle, Destination destination,
                        String startDate, String endDate, User user) throws EbeanDateParseException {
        this.title = title;
        this.riddle = riddle;
        this.destination = destination;

        try {
            this.startDate = UtilityFunctions.parseLocalDate(startDate);
            this.endDate = UtilityFunctions.parseLocalDate(endDate);
        } catch (DateTimeParseException e) {
            throw new EbeanDateParseException(e);
        }
        this.user = user;
        this.users = new ArrayList<>();
    }

    public TreasureHunt(String title, String riddle, Destination destination,
                        LocalDate startDate, LocalDate endDate, User user) {
        this.title = title;
        this.riddle = riddle;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.users = new ArrayList<>();
    }

    /**
     * Constructor to create a treasure hunt from an existing treasure hunt object
     *
     * @param treasureHunt The treasure hunt object being created
     */
    public TreasureHunt(TreasureHunt treasureHunt) {
        this(treasureHunt.getTitle(),
                treasureHunt.getRiddle(),
                treasureHunt.getDestination(),
                treasureHunt.getStartDate(),
                treasureHunt.getEndDate(),
                treasureHunt.getUser()
        );
    }


     /** Gets the Ebeans finder object for TreasureHunt
     *
     * @return Finder<Integer,TreasureHunt> object.
      */
    public static Finder<Integer,TreasureHunt> find() {
        return find;
    }
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(String startDate) throws EbeanDateParseException {
        try {
            this.startDate = UtilityFunctions.parseLocalDate(startDate);
        } catch (DateTimeParseException e) {
            throw new EbeanDateParseException(e);
        }
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(String endDate) throws EbeanDateParseException {
        try {
            this.endDate = UtilityFunctions.parseLocalDate(endDate);;
        } catch (DateTimeParseException e) {
            throw new EbeanDateParseException(e);
        }
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

    /**
     *The unique hashcode of a treasure hunt given it's attributes
     *
     * @return The full hash code of the treasure hunt
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + title.hashCode();
        result = 31 * result + riddle.hashCode();
        result = 31 * result + destination.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        return result;
    }

    /**
     * Method to check equal treasure hunt objects
     *
     * @param obj The object being checked
     * @return True of the object is equal to this treasure hunt,
     * False otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TreasureHunt)) return false;
        TreasureHunt other = (TreasureHunt) obj;
        return title.equals(other.title) &&
                riddle.equals(other.riddle) &&
                destination.equals(other.destination) &&
                startDate.equals(other.startDate) &&
                endDate.equals(other.endDate);
    }

    /**
     * Modifies the fields of this Treasure Hunt which are included in the
     *   treasure hunt editing form to be equal to those fields of the TreasureHunt
     *   passed in
     *
     * @param editedTreasureHunt The changed treasure hunt
     */
    public void applyEditChanges(TreasureHunt editedTreasureHunt) {
        this.title = editedTreasureHunt.getTitle();
        this.destination = editedTreasureHunt.getDestination();
        this.riddle = editedTreasureHunt.getRiddle();
        this.startDate = editedTreasureHunt.getStartDate();
        this.endDate = editedTreasureHunt.getEndDate();
        this.users = editedTreasureHunt.getUsers();
    }

    /**
     * Sets the TreasureHunt ID to null.
     */
    public void setThuntIdNull() {
        this.thuntid = null;
    }

    /**
     * Checks if a treasure hunt is currently active.
     * @return true if the treasure hunt is currently open, else false
     */
    public boolean isOpen() {
        LocalDate currentDate = LocalDate.now(ZoneId.of("Pacific/Auckland"));
        boolean isStartBeforeOrOnCurrent = this.getStartDate().isBefore(currentDate)
                                    || this.getStartDate().isEqual(currentDate);
        boolean isEndAfterCurrentDate = this.getEndDate().isAfter(currentDate);

        return isStartBeforeOrOnCurrent && isEndAfterCurrentDate;
    }
}
