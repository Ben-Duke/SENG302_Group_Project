package models;

import accessors.VisitAccessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import formdata.TripFormData;
import io.ebean.Ebean;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
public class Trip extends Model {

    @Id
    private Integer tripid;

    private String tripName;

    @Column(columnDefinition = "integer default 0")
    private Integer removedVisits;

    @JsonIgnore
    @OneToMany(mappedBy = "trip")
    private List<Visit> visits;

    private static Finder<Integer,Trip> find = new Finder<>(Trip.class);

    private boolean isPublic = true;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    private User user;



    /**
     * Default Constructor
     */
    public Trip(){
    }

    public Trip(Trip trip, List<Visit> visits) {
        this.tripName = trip.getTripName();
        this.removedVisits = trip.getRemovedVisits();
        this.isPublic = trip.getIsPublic();
        this.user = trip.getUser();
        this.visits = visits;
    }

    /**
     * Trip Constructor
     * @param tripName Name of the trip
     * @param isPublic the public attribute for the trip
     * @param user The owner of the trip
     */
    public Trip(String tripName, boolean isPublic, User user) {
        this.removedVisits = 0;
        this.tripName = tripName;
        this.isPublic = isPublic;
        this.user = user;
        this.visits = new ArrayList<>();
    }

    /**
     * Gets finder object for Trip.
     *
     * @return Finder<Integer,Trip> object
     */
    public static Finder<Integer,Trip> find() {
        return find;
    }


    public Integer getTripid() {
        return tripid;
    }

    public void setTripid(Integer tripid) {
        this.tripid = tripid;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public boolean getIsPublic() { return isPublic; }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Visit> getOrderedVisits(){
        List<Visit> tempVisits = visits;
        tempVisits.sort(Comparator.comparing(Visit::getVisitOrder));
        return tempVisits;
    }

    public void deleteVisit(Visit visit){
        this.visits.remove(visit);
    }

    public void addVisit(Visit visit){
        this.visits.add(visit);
    }

    /**
     * Returns the start date of the trip
     * @return The date of the start of the trip as a string
     */
    public String getTripStart(){
        if(this.visits.isEmpty()){
            return null;
        }
        else {
            String startDate = Ebean.find(Visit.class).where().eq("trip", this).orderBy("arrival DESC").findList().get(0).getArrival();
            return startDate;
        }
    }

    /**
     * Returns the end date of the trip
     * @return The date of the end of the trip as a string
     */
    public String getTripEnd(){
        String endDate = Ebean.find(Visit.class).where().eq("trip", this).orderBy("departure ASC").findList().get(0).getDeparture();
        return endDate;
    }

    public Integer getRemovedVisits() {
        return removedVisits;
    }

    public void setRemovedVisits(Integer removedVisits) {
        this.removedVisits = removedVisits;
    }

    public void removeAllVisits() {
        visits = new ArrayList<>();
    }



    public boolean hasVisit(){
        return (visits != null) && (!visits.isEmpty());
    }



    /**
     * Checks if the given user id (of the currently logged in user) is the same as the owner of the entity.
     * @param userid the user id to be tested
     * @return true if userid is the owner of the entity, false if owner has a different user id.
     */
    public boolean isUserOwner(Integer userid){
        if(this.user.getUserid() == userid){
            return true;
        }
        else{
            return false;
        }
    }
}
