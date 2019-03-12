package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
public class Visit extends Model {

    public Visit(Destination destination, Trip trip, Integer visitorder, LocalDate arrival, LocalDate departure){
        this.destination = destination;
        this.trip = trip;
        this.visitorder = visitorder;
        this.arrival = arrival;
        this.departure = departure;
        this.visitName = destination.getDestName();
    }

    public Visit(Destination destination, Trip trip, Integer visitorder){
        this.destination = destination;
        this.trip = trip;
        this.visitorder = visitorder;
        this.visitName = destination.getDestName();
    }

    /**
     * The ID of the visit. This is the primary key.
     */
    @Id
    public Integer visitid;

    @ManyToOne
    @JoinColumn(name = "destination", referencedColumnName = "destid")
    public Destination destination;

    @ManyToOne
    @JoinColumn(name = "trip", referencedColumnName = "tripid")
    public Trip trip;

    public Integer visitorder;

    public LocalDate arrival;

    public LocalDate departure;

    public String visitName;

    public static Finder<Integer,Visit> find = new Finder<>(Visit.class);

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

    public Integer getVisitorder() {
        return visitorder;
    }

    public void setVisitorder(Integer visitorder) {
        this.visitorder = visitorder;
    }

    public LocalDate getArrival() {
        return arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    public LocalDate getDeparture() {
        return departure;
    }

    public void setDeparture(LocalDate departure) {
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
}
