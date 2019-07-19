package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "passport",
        uniqueConstraints = @UniqueConstraint(columnNames = "passport_name")
)
public class Passport extends CountryItem {

    public Passport(String passportName){
        super();
        this.passportName = passportName;
    }

    @Id
    public Integer passid;

    @Column(name="passport_name")
    public String passportName;

    @JsonIgnore
    @ManyToMany(mappedBy = "passports")
    public List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Integer getPassportId() {
        return passid;
    }

    public String getName(){
        return passportName;
    }

    public final static Finder<String,Passport> findByName = new Finder<>(Passport.class);

    public final static Finder<Integer,Passport> find = new Finder<>(Passport.class, ApplicationManager.getDatabaseName());
}
