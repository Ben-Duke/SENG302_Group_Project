package models;

import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import play.data.format.Formats;
import play.mvc.Http;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static play.mvc.Results.badRequest;

@Entity
public class User extends Model {

    /**
     * The formEmail of the User
     */
    public String email;

    /**
     * The constructor for the User that takes the parameters, formEmail, password, first name, last name, date of birth,
     * gender, nationality and passport.
     * @param email
     * @param password
     * @param fName A String parameter that is used to set the first name of the User.
     * @param lName A String parameter that is used to set the last name of the User.
     * @param dateOfBirth A LocalDate parameter that is used to set the User's dob.
     * @param gender A String paramters that is used to set the gender of the User.
     */
    public User(String email, String password, String fName, String lName, LocalDate dateOfBirth, String gender){
        this.email = email.toLowerCase();
        this.password = password;
        this.fName = fName;
        this.lName = lName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.isAdmin = false;
    }

    public User(String email){
        this.email = email.toLowerCase();
    }

    /**
     * The ID of the user. This is the primary key.
     */
    @Id
    public Integer userid;

    public User(String email, String password){
        this.email = email.toLowerCase();
        this.password = password;
    }
    //TOdo to be ENCRYPTED I THINK - gav
    /**
     * The password of the user
     */
    public String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
    //date was protected not public/private for some reason
    @CreatedTimestamp
    public Date creationDate;

    /**
     * The nationality of the user.
     * The user doesn't need to have a nationality.
     */
    @ManyToMany
    public List<Nationality> nationality;

    /**
     * The date of birth of the user
     */
    public LocalDate dateOfBirth;
    /**
     * The gender of the user.
     */
    public String gender;
    /**
     * The first name of the user.
     */
    public String fName;
    /**
     * The last name of the user
     */
    public String lName;
    /**
     * The passport of the user.
     */
    @ManyToMany
    public List<Passport> passports;

    @OneToMany(mappedBy = "user")
    public List<Trip> trips;

    @OneToMany(mappedBy = "user")
    public List<Destination> destinations;

    public Map<String, Boolean> getMappedDestinations() {
        SortedMap<String, Boolean> destMap = new TreeMap<>();
        for (Destination destination : destinations) {
            destMap.put(destination.getDestName(), false);
        }
        return destMap;
    }

    @ManyToMany
    public List<TravellerType> travellerTypes;

    /**
     * Get's a List<UserPhoto> containing all the photos of the user.
     *
     * @return A List<UserPhoto> containing all the photos of the user.
     */
    public List<UserPhoto> getUserPhotos() {
        return userPhotos;
    }

    @OneToMany(mappedBy = "user")
    public List<UserPhoto> userPhotos;

    public static Finder<Integer,User> find = new Finder<>(User.class);

    //TODO remove this attribute along with getters, setters and checkboxes in create/update user story[229] tasks[1284,1301]
    public Boolean isAdmin = false;


    //GETTERS AND SETTERS

    public Boolean isAdmin() {return isAdmin;}

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public int getUserid() {
        return userid;
    }

    public static int checkUser(String email){
        ExpressionList<User> usersExpressionList = User.find.query().where().eq("formEmail", email.toLowerCase());
        int userPresent = 0;
        if (usersExpressionList.findCount() > 0) {
            userPresent = 1;
        }
        return userPresent;


    }

    public String getPassword(){
        return password;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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
        List<User> users= User.find.all();
        return  users;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public List<Passport> getPassport() {
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


    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }


    //OTHER METHODS
    public boolean authenticate(String password){
        if(this.password.equals(password)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean hasEmptyField(){
        if(fName == null || lName == null
        || gender == null || dateOfBirth == null){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean hasNationality(){
        if (nationality != null) {
            if (! nationality.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTravellerTypes() {
        if (travellerTypes != null) {
            if (! travellerTypes.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * From an http request this method extracts the current user in the session.
     * If there is no current user, then null is returned.
     *
     * @param request the HTTP request
     * @return the current user in the session
     */
    public static User getCurrentUser(Http.Request request) {
        String userId = request.session().getOptional("connected").orElse(null);
        if (userId != null) {
            User user = User.find.query().where().eq("userid", userId).findOne();
            return user;
        }
        return null;
    }

    /** Returns the id of the user and returns it if for some reason the user doesnt exist it will return -1.
     *
     * @param request
     * @return userid on success or -1.
     */
    public static int getCurrentUserById(Http.Request request) {
        String userId = request.session().getOptional("connected").orElse(null);
        if (userId != null) {
            User user = User.find.query().where().eq("userid", userId).findOne();
            return user.getUserid();
        }
        return -1;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets a list of the user's trips, sorted by the earliest arrival date of their visits within each trip.
     * If there are no arrival dates set within a trip, the trip is placed at the bottom of the list.
     * @return the list of sorted trips
     */
    public List<Trip> getTripsSorted()
    {
        HashMap<Trip,LocalDate> datesMap = new HashMap<>();
        for(Trip trip: trips){
            ArrayList<LocalDate> datesList = new ArrayList<>();
            for(Visit visit: trip.getVisits()){
                if(visit.getArrival() != null && !(visit.getArrival().isEmpty())) {
                    String arrival = visit.getArrival();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate arrivalDate = LocalDate.parse(arrival, formatter);
                    datesList.add(arrivalDate);
                }
                else{
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate arrivalDate = LocalDate.parse("2100-12-25", formatter);
                    datesList.add(arrivalDate);
                }
            }
            Collections.sort(datesList);
            if (! datesList.isEmpty()) {
                datesMap.put(trip, datesList.get(0));
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
        List<Admin> admins = Admin.find.all();
        for (Admin admin : admins) {
            if (admin.userId == userid) {
                return true;
            }
        }
        return false;
    }
}


