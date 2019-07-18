package models;

import accessors.CommandManagerAccessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import models.commands.General.CommandManager;
import org.mindrot.jbcrypt.BCrypt;
import play.data.format.Formats;
import play.mvc.Http;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * NOTE: This class has a natural ordering that is inconsistent with equals
 */
@Entity
@Table(name = "user",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class User extends Model implements Comparable<User> {

    @Column(name="email")
    private String email; // The email of the User


    @Id
    private Integer userid; // The ID of the user. This is the primary key.

    private String passwordHash; // hashed password

    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
    //date was protected not public/private for some reason
    @CreatedTimestamp
    private Date creationDate;

    /**
     * The nationality of the user.
     * The user doesn't need to have a nationality.
     */
    @JsonIgnore
    @ManyToMany
    private List<Nationality> nationality;

    /**
     * The date of birth of the user
     */
    private LocalDate dateOfBirth;
    /**
     * The gender of the user.
     */
    private String gender;
    /**
     * The first name of the user.
     */
    private String fName;
    /**
     * The last name of the user
     */
    private String lName;

    /**
     * True if there was an error undoing or redoing the stack, false otherwise.
     */
    private boolean undoRedoError;

    /**
     * The passport of the user.
     */
    @JsonIgnore
    @ManyToMany
    private List<Passport> passports;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Trip> trips;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<TreasureHunt> treasureHunts;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Destination> destinations;

    @JsonIgnore
    @ManyToMany
    private List<TravellerType> travellerTypes;

    @JsonIgnore
    @ManyToMany
    private List<TreasureHunt> guessedTHunts;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<UserPhoto> userPhotos;

    private static Finder<Integer,User> find = new Finder<>(User.class);

    @Deprecated
    private Boolean isAdmin = false;


    // ^^^^^ Class attributes ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //==========================================================================
    //       Class methods below

    /**
     * Constructor with just two attributes, email and plaintextPassword.
     *
     * @param email The users email (String)
     * @param plaintextPassword The users plaintext Password (String).
     */
    public User(String email, String plaintextPassword){
        this.email = email.toLowerCase();
        this.hashAndSetPassword(plaintextPassword);
        this.isAdmin = false;
    }

    /**
     * The constructor for the User that takes the parameters, email, password, first name, last name, date of birth,
     * gender, nationality and passport.
     * @param email
     * @param plaintextPassword The Users plaintext password
     * @param fName A String parameter that is used to set the first name of the User.
     * @param lName A String parameter that is used to set the last name of the User.
     * @param dateOfBirth A LocalDate parameter that is used to set the User's dob.
     * @param gender A String paramters that is used to set the gender of the User.
     */
    public User(String email,
                String plaintextPassword,
                String fName,
                String lName,
                LocalDate dateOfBirth,
                String gender){

        this.email = email.toLowerCase();
        this.hashAndSetPassword(plaintextPassword);
        this.fName = fName;
        this.lName = lName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.isAdmin = false;    }

    public User(String email){
        this.email = email.toLowerCase();
        this.isAdmin = false;
    }

    /**
     * Empty constructor for users
     */
    public User(){

    }

    /**
     * Method to get EBeans finder object for queries.
     *
     * @return a Finder<Integer, User> object.
     */
    public static Finder<Integer, User> find() {
        return find;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", userid=" + userid +
                ", passwordHash='" + passwordHash + '\'' +
                ", creationDate=" + creationDate +
                ", nationality=" + nationality +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", passports=" + passports +
                ", trips=" + trips +
                ", treasureHunts=" + treasureHunts +
                ", destinations=" + destinations +
                ", travellerTypes=" + travellerTypes +
                ", guessedTHunts=" + guessedTHunts +
                ", commandManager=" + getCommandManager() +
                ", userPhotos=" + userPhotos +
                ", isAdmin=" + isAdmin +
                '}';
    }

    /**
     * Sets the Users password. Automatically hashes the password.
     *
     * The password stored in the database will be the hash.
     *
     * @param plaintextPassword A String, the password in plaintext.
     */
    public void hashAndSetPassword(String plaintextPassword) {
        this.passwordHash = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public Map<String, Boolean> getMappedDestinations() {
        SortedMap<String, Boolean> destMap = new TreeMap<>();
        for (Destination destination : destinations) {
            destMap.put(destination.getDestName(), false);
        }
        return destMap;
    }

    /**
     * Get's a List<UserPhoto> containing all the photos of the user.
     *
     * @return A List<UserPhoto> containing all the photos of the user.
     */
    public List<UserPhoto> getUserPhotos() {
        return userPhotos;
    }

    //GETTERS AND SETTERS
    @Deprecated
    public Boolean isAdmin() {return isAdmin;}

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public int getUserid() {
        return userid;
    }

    public static int checkUser(String email){
        ExpressionList<User> usersExpressionList = User.find().query().where().eq("email", email.toLowerCase());
        int userPresent = 0;
        if (usersExpressionList.findCount() > 0) {
            userPresent = 1;
        }
        return userPresent;


    }

    /**
     * Gets the users password hash (not plaintext pw).
     *
     * @return A String of the hashed password
     */
    public String getPasswordHash(){
        return this.passwordHash;
    }

    public List<TravellerType> getTravellerTypes() {
        return travellerTypes;
    }

    public void setTravellerTypes(List<TravellerType> travellerTypes) {
        this.travellerTypes = travellerTypes;
    }

    public void addTravellerType(TravellerType travellerType){
        this.travellerTypes.add(travellerType);
    }

    public void deleteTravellerType(TravellerType travellerType){
        this.travellerTypes.remove(travellerType);
    }

    public void addNationality(Nationality nationality){
        this.nationality.add(nationality);
    }

    public void deleteNationality(Nationality nationality){
        this.nationality.remove(nationality);
    }

    public void addPassport(Passport passport){
        this.passports.add(passport);
    }

    public void deletePassport(Passport passport){
        this.passports.remove(passport);
    }

    public String getEmail(){
        return email;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isUndoRedoError() {
        return undoRedoError;
    }

    public void setUndoRedoError(boolean undoRedoError) {
        this.undoRedoError = undoRedoError;
    }

    public List<Nationality> getNationality() {
        return nationality;
    }

    public void setNationality(List<Nationality> nationality) {
        this.nationality = nationality;
    }

    public LocalDate getDateOfBirth(){
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }

    public List<User> getUsers() {
        List<User> users= User.find().all();
        return  users;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public List<Passport> getPassport() {
        return passports;
    }

    public List<Passport> getPassports() {
        return passports;
    }

    public void setPassport(List<Passport> passport) {
        this.passports = passport;
    }
    
    public void setUserid(Integer userid) {
        this.userid = userid;
    }
    public List<Trip> getTrips() {
        return trips;
    }

    public List<TreasureHunt> getTreasureHunts() {
        return treasureHunts;
    }

    public void setTreasureHunts(List<TreasureHunt> treasureHunts) {
        this.treasureHunts = treasureHunts;
    }

    public List<TreasureHunt> getGuessedTHunts() {
        return guessedTHunts;
    }

    public void setGuessedTHunts(List<TreasureHunt> guessedTHunts) {
        this.guessedTHunts = guessedTHunts;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    public CommandManager getCommandManager() {
        return CommandManagerAccessor.getCommandManagerByEmail(this.email);
    }

    //OTHER METHODS
    public boolean hasEmptyField(){
        return fName == null || lName == null
                || gender == null || dateOfBirth == null;
    }

    public boolean hasNationality(){
        if (nationality != null) {
            return !nationality.isEmpty();
        }
        return false;
    }

    public boolean hasTravellerTypes() {
        if (travellerTypes != null) {
            return !travellerTypes.isEmpty();
        }
        return false;
    }

    /**
     * From an http request this method extracts the current user in the session.
     * If there is no current user, then null is returned.
     * If the user is an admin and acting as another user, then returns the user
     * they are acting as.
     *
     * @param request the HTTP request
     * @return the current user in the session or the user the admin is acting as
     */
    public static User getCurrentUser(Http.Request request) {
        String userId = request.session()
                .getOptional("connected")
                .orElse(null);
        if (userId != null) {
            User requestUser = User.find().query().where()
                    .eq("userid", userId)
                    .findOne();
            if (requestUser != null && requestUser.userIsAdmin()) {
                List<Admin> adminList = Admin.find().query().where()
                        .eq("userId", userId).findList();
                if (adminList.size() == 1) {
                    Admin admin = adminList.get(0);
                    if (admin.getUserIdToActAs() != null) {
                        User userToEdit = User.find.byId(admin.getUserIdToActAs());
                        return userToEdit;
                    } else {
                        return requestUser;
                    }
                } else {
                    return null;
                }
            }
            return requestUser;
        }
        return null;
    }

    /**
     * Overload method to get the current user from a HTTP request while checking if
     * there is an admin acting as the user.
     * If the admin is acting as a user, returns a list where the first element is
     * the user the admin is acting as and the second element is the admin.
     * Else if the admin is not acting as a user, the first element is the admin and
     * the second element is also the same admin.
     * Else if the request user is not an admin, returns the user as the first and
     * second element.
     * @param request the http request
     * @param checkForAdmin overload parameter (boolean)
     * @return a list of two users
     */
    public static List<User> getCurrentUser(Http.Request request, boolean checkForAdmin) {
        List<User> users = new ArrayList<>();
        String userId = request.session()
                .getOptional("connected")
                .orElse(null);
        if (userId != null) {
            User requestUser = User.find().query().where()
                    .eq("userid", userId)
                    .findOne();
            users.add(requestUser);

            if (requestUser != null && requestUser.userIsAdmin()) {
                List<Admin> adminList = Admin.find().query().where()
                        .eq("userId", userId).findList();
                if (adminList.size() == 1) {
                    Admin admin = adminList.get(0);
                    if (admin.getUserIdToActAs() != null) {
                        User userToEdit = User.find().byId(admin.getUserIdToActAs());
                        users.add(0, userToEdit);
                        return users;
                    } else {
                        users.add(requestUser);
                        return users;
                    }
                } else {
                    //this should never happen
                    return users;
                }
            } else {
                users.add(requestUser);
            }
            return users;
        }
        return users;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets a list of the user's trips, sorted by the earliest arrival date of their visits within each trip.
     * If there are no arrival dates set within a trip, the trip is placed at the bottom of the list.
     * This methods also iterates through the user's list of trips and eliminates all invalid trips
     * (ones with less than two destinations)
     * @return the list of sorted trips
     */
    public List<Trip> getTripsSorted()
    {
        HashMap<Trip,LocalDate> datesMap = new HashMap<>();
        for(Trip trip: trips){
            if(trip.getVisits().size() < 2){
                for(Visit visit : trip.getVisits()){
                    visit.delete();
                }
                trip.delete();
            }
            else {
                ArrayList<LocalDate> datesList = new ArrayList<>();
                for (Visit visit : trip.getVisits()) {
                    if (visit.getArrival() != null && !(visit.getArrival().isEmpty())) {
                        String arrival = visit.getArrival();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate arrivalDate = LocalDate.parse(arrival, formatter);
                        datesList.add(arrivalDate);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate arrivalDate = LocalDate.parse("2100-12-25", formatter);
                        datesList.add(arrivalDate);
                    }
                }
                Collections.sort(datesList);
                if (!datesList.isEmpty()) {
                    datesMap.put(trip, datesList.get(0));
                }
            }
        }
        datesMap = sortByValues(datesMap);
        Set datesSet = datesMap.keySet();
        List<Trip> sortedTrips = new ArrayList(datesSet);
        return sortedTrips;
    }

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    public boolean userIsAdmin() {
        List<Admin> admins = Admin.find().all();
        for (Admin admin : admins) {
            if (admin.getUserId().equals(userid)) {
                return true;
            }
        }
        return false;
    }

    public int compareTo(User other) {
        return this.userid.compareTo(other.getUserid());
    }

    /** Modifies the fields of this User which are included in the
     *   profile editing form to be equal to those fields of the user
     *   passed in */
    public void applyEditChanges(User editedUser) {
        this.fName = editedUser.getFName();
        this.lName = editedUser.getLName();
        this.gender = editedUser.getGender();
        this.dateOfBirth = editedUser.getDateOfBirth();
        this.email = editedUser.getEmail();
        this.passwordHash = editedUser.getPasswordHash();
    }
}


