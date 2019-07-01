package models;

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

    public Trip(Trip trip, List<Visit> visits) {
        this.tripName = trip.getTripName();
        this.removedVisits = trip.getRemovedVisits();
        this.isPublic = trip.getIsPublic();
        this.user = trip.getUser();
        this.visits = visits;
    }

    @Id
    public Integer tripid;

    public String tripName;

    @Column(columnDefinition = "integer default 0")
    public Integer removedVisits;

    @JsonIgnore
    @OneToMany(mappedBy = "trip")
    public List<Visit> visits;

    public static Finder<Integer,Trip> find = new Finder<>(Trip.class);

    public boolean isPublic = true;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    public static Trip makeInstance(TripFormData formData){
        Trip trip = new Trip();
        trip.tripName = formData.tripName;
        trip.user = formData.user;
        trip.removedVisits = 0;
        trip.visits = new ArrayList<>();
        return trip;
    }

    public Trip(String tripName, boolean isPublic, User user) {
        this.removedVisits = 0;
        this.tripName = tripName;
        this.isPublic = isPublic;
        this.user = user;
        this.visits = new ArrayList<>();
    }
    public Trip(){
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
        List<Visit> visits = this.getVisits();
        visits.sort(Comparator.comparing(Visit::getVisitOrder));
        return visits;
    }

    public void deleteVisit(Visit visit){
        this.visits.remove(visit);
    }

    public void addVisit(Visit visit){
        this.visits.add(visit);
    }

    public String getTripStart(){
        if(this.visits.isEmpty()){
            return null;
        }
        else {
            String startDate = Ebean.find(Visit.class).where().eq("trip", this).orderBy("arrival DESC").findList().get(0).getArrival();
            return startDate;
        }
    }

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
