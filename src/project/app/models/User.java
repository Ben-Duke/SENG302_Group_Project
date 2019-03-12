package models;

import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import play.data.format.Formats;
import play.mvc.Http;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class User extends Model {

    /**
     * The constructor for the User that takes the parameters, username, password, first name, last name, date of birth,
     * gender, nationality and passport.
     * @param username
     * @param password
     * @param fName A String parameter that is used to set the first name of the User.
     * @param lName A String parameter that is used to set the last name of the User.
     * @param dateOfBirth A LocalDate parameter that is used to set the User's dob.
     * @param gender A String paramters that is used to set the gender of the User.
     */
    public User(String username, String password, String fName, String lName, LocalDate dateOfBirth, String gender){
        this.username = username.toLowerCase();
        this.password = password;
        this.fName = fName;
        this.lName = lName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public User(String username){
        this.username = username.toLowerCase();
    }

    public User(String username, String password){
        this.username = username.toLowerCase();
        this.password = password;
    }

    /**
     * The ID of the user. This is the primary key.
     */
    @Id
    public Integer userid;

    /**
     * The username of the User
     */
    public String username;
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

    @ManyToMany
    public List<TravellerType> travellerTypes;

    public static Finder<Integer,User> find = new Finder<>(User.class);


    //GETTERS AND SETTERS


    public Integer getUserid() {
        return userid;
    }

    /*
    public void setUserid(Integer userid) {
        this.userid = userid;
    }
    */

    public String getUsername(){
        return username;
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

    public void setUsername(String username) {
        this.username = username;
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
}
