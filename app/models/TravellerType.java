package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;
@Table(
        uniqueConstraints=
                @UniqueConstraint(columnNames={"traveller_type_name"})
)
@Entity
public class TravellerType extends Model {

    public TravellerType(String travellerTypeName){
        this.travellerTypeName = travellerTypeName;
    }

    @Id
    public Integer ttypeid;

    public String travellerTypeName;

    @Override
    public String toString() {
        return travellerTypeName;
    }

    @ManyToMany(mappedBy = "travellerTypes")
    public List<User> users;

    public Integer getTtypeid() {
        return ttypeid;
    }

    public void setTtypeid(Integer ttypeid) {
        this.ttypeid = ttypeid;
    }

    public String getTravellerTypeName() {
        return travellerTypeName;
    }

    public void setTravellerTypeName(String travellerTypeName) {
        this.travellerTypeName = travellerTypeName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public static Finder<Integer,TravellerType> find = new Finder<>(TravellerType.class);



}
