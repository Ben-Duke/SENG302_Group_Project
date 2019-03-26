package models;

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
    public Integer tripid;

    public String tripName;

    @Column(columnDefinition = "integer default 0")
    public Integer removedVisits;

    @OneToMany(mappedBy = "trip")
    public List<Visit> visits;

    public static Finder<Integer,Trip> find = new Finder<>(Trip.class);

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    public static Trip makeInstance(TripFormData formData, User user){
        Trip trip = new Trip();
        trip.tripName = formData.tripName;
        trip.user = user;
        trip.removedVisits = 0;
        trip.visits = new ArrayList<Visit>();
        return trip;
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
        visits.sort(Comparator.comparing(Visit::getVisitorder));
        return visits;
    }

    public void deleteVisit(Visit visit){
        this.visits.remove(visit);
    }

    public void addVisit(Visit visit){
        this.visits.add(visit);
    }

    public String getTripStart(){
        String startDate = Ebean.find(Visit.class).where().eq("trip", this).orderBy("arrival DESC").findList().get(0).getArrival();
        return startDate;
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
        if (visits != null) {
            if (! visits.isEmpty()) {
                return true;
            }
        }
        return false;
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
