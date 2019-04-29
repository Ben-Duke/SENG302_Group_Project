package models;

import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public List<TravellerType> newTravelerTypes;

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

        for (TravellerType travellerType : newDestination.getTravellerTypes()) {
            this.newTravelerTypes.add(TravellerType.find.query().where().eq("traveller_type_name", travellerType.getTravellerTypeName()).findOne());
        }

        this.requestAuthor = user;
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
    public List<TravellerType> getNewTravellerTypes() { return newTravelerTypes; }
    public Date getCreationDate() { return creationDate; }
    public User getRequestAuthor() { return requestAuthor; }
}
