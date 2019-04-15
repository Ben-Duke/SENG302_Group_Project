package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

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

    public DestinationModificationRequest(Destination oldDestination, Destination newDestination) {
        this.oldDestination = oldDestination;
        this.newDestName = newDestination.getDestName();
        this.newDestType = newDestination.getDestType();
        this.newDestCountry = newDestination.getCountry();
        this.newDestDistrict = newDestination.getDistrict();
        this.newDestLatitude = newDestination.getLatitude();
        this.newDestLongitude = newDestination.getLongitude();
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
}
