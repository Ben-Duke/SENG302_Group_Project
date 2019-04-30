package models;

import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.*;

@Entity
public class DestinationModificationRequest extends Model {

    @Id
    public Integer id;

    @ManyToOne
    public Destination oldDestination;

    public String newDestName;
    public String newDestType;
    public String newDestCountry;
    public String newDestDistrict;
    public double newDestLatitude;
    public double newDestLongitude;

    @ManyToMany(cascade = CascadeType.ALL)
    public Set<TravellerType> newTravelerTypes;

    @Temporal(TemporalType.TIMESTAMP)
    @Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
    @CreatedTimestamp
    public Date creationDate;

    @ManyToOne
    public User requestAuthor;

    public DestinationModificationRequest(Destination oldDestination, Destination newDestination, User user) {
        this.oldDestination = oldDestination;
        this.newDestName = newDestination.getDestName();
        this.newDestType = newDestination.getDestType();
        this.newDestCountry = newDestination.getCountry();
        this.newDestDistrict = newDestination.getDistrict();
        this.newDestLatitude = newDestination.getLatitude();
        this.newDestLongitude = newDestination.getLongitude();
        this.newTravelerTypes = formNewTravellerTypes(newDestination.getTravellerTypes());

        this.requestAuthor = user;
    }

    /**
     * A work-around due to a bug introduced working with Play. The bug was unexplained
     * but essentially a String object was being jammed into the place of a Set and so
     * this method unpacks the String and rebuilds the Set
     * @param travellerTypes The possibly malformed Set produced by PlayFramework
     * @return A well-formed Set of TravellerTypes
     */
    private Set<TravellerType> formNewTravellerTypes(Set<TravellerType> travellerTypes) {

        Set<TravellerType> travellerTypesSet = new HashSet<>();

        String typesString = travellerTypes.toString();
        if (typesString.equals("[]") || typesString.equals("BeanSet size[0] set[]")) {
            return travellerTypesSet;
        }
        typesString = typesString.replaceAll("\\[|]", ""); //Trim off the set square brackets
        String[] types = typesString.split("\\s*,\\s"); // Split into array by the comma/whitespace delim
        for (String type: types) {
            TravellerType travellerType = TravellerType.find.query()
                    .where().eq("travellerTypeName", type).findOne();
            travellerTypesSet.add(travellerType);
        }
        return travellerTypesSet;
    }

    public static Finder<Integer, DestinationModificationRequest> find = new Finder<>(DestinationModificationRequest.class);

    public Integer getId() { return id; }
    public Destination getOldDestination() { return oldDestination; }
    public String getNewDestName() { return newDestName; }
    public String getNewDestType() { return newDestType; }
    public String getNewDestCountry() { return newDestCountry; }
    public String getNewDestDistrict() { return newDestDistrict; }
    public double getNewDestLatitude() { return newDestLatitude; }
    public double getNewDestLongitude() { return newDestLongitude; }
    public Set<TravellerType> getNewTravellerTypes() { return newTravelerTypes; }
    public Date getCreationDate() { return creationDate; }
    public User getRequestAuthor() { return requestAuthor; }
}
