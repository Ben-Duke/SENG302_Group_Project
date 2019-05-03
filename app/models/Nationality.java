package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;
import java.util.Set;

@Entity
public class Nationality extends Model {
    public Nationality(String nationality){
        this.nationalityName = nationality;
    }

    @Id
    public Integer natid;

    public String nationalityName;

    @ManyToMany(mappedBy = "nationality")
    @JsonIgnore
    public Set<User> users;

    public String getNationalityName() { return nationalityName; }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public static Finder<Integer,Nationality> find = new Finder<>(Nationality.class);
}
