package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Passport extends Model {

    public Passport(String passportName){
        this.passportName = passportName;
    }

    @Id
    public Integer passid;

    public String passportName;

    @ManyToMany(mappedBy = "passports")
    @JsonIgnoreProperties("passports")
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

    public final static Finder<Integer,Passport> find = new Finder<>(Passport.class);
}
