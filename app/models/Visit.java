package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import formdata.VisitFormData;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/** The model class for visit construction */
@Entity
public class Visit extends Model {

    private Integer visitorder;

    /**
     * The ID of the visit. This is the primary key.
     */
    @Id
    private Integer visitid;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "destination", referencedColumnName = "destid")
    private Destination destination;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "trip", referencedColumnName = "tripid")
    private Trip trip;
    private String arrival;
    private String departure;
    private String visitName;
    private static Finder<Integer,Visit> find = new Finder<>(Visit.class);



    /**
     * Constructor for visits
     * @param arrival The arrival date of the visit
     * @param departure The departure date of the visit
     * @param trip The trip the visit is a apart of
     * @param destination The destination of the visit
     * @param visitOrder The order this visit in within the trip it is a part of
     */
    public Visit(String arrival, String departure, Trip trip, Destination destination, Integer visitOrder) {
        this.arrival = arrival;
        this.departure = departure;
        this.trip = trip;
        this.visitorder = visitOrder;
        setDestination(destination);
    }

    /**
     * Constructor for visits without a visit order parameter
     * @param arrival The arrival date of the visit
     * @param departure The departure date of the visit
     * @param trip The trip the visit is a apart of
     * @param destination The destination of the visit
     */
    public Visit(String arrival, String departure, Trip trip, Destination destination) {
        this.arrival = arrival;
        this.departure = departure;
        this.trip = trip;
        setDestination(destination);
    }

    /**
     * Empty visit constructor
     */
    public Visit() {
    }

    /**
     * Visit constructor using a visit object
     * @param visit The visit object being used
     */
    public Visit(Visit visit) {
        this(visit.getArrival(),
                visit.getDeparture(),
                visit.getTrip(),
                visit.getDestination(),
                visit.getVisitOrder()
        );
    }

    /**
     * Gets an EBeans finder object for Visit
     *
     * @return A Finder<Integer,Visit> object.
     */
    public static Finder<Integer,Visit> find() {
        return find;
    }

    public Integer getVisitid() {
        return visitid;
    }

    public void setVisitid(Integer visitid) {
        this.visitid = visitid;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
        this.visitName = destination.getDestName();
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Integer getVisitOrder() {
        return visitorder;
    }

    public void setVisitorder(Integer visitorder) {
        this.visitorder = visitorder;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getVisitName() {
        return visitName;
    }

    public void setVisitName(String visitName) {
        this.visitName = visitName;
    }

    /**
     * Checks if the given trip id (of the currently logged in user) is the same as the owner of the entity.
     * @param tripid the trip id to be tested
     * @return true if tripid is the owner of the entity, false if owner has a different user id.
     */
    public boolean isTripOwner(Integer tripid){
        if(this.trip.getTripid() == tripid){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Apply the changes from another visit to this visit
     * @param editedVisit the visit being updated
     */
    public void applyEditChanges(Visit editedVisit) {
        this.arrival = editedVisit.arrival;
        this.departure = editedVisit.departure;
    }
}
