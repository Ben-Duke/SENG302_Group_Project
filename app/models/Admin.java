package models;

import io.ebean.Finder;
import io.ebean.Model;
import org.checkerframework.common.aliasing.qual.Unique;
import play.mvc.Http;

import javax.persistence.*;

@Entity
//@Table(name = "admin",
//        uniqueConstraints = @UniqueConstraint(columnNames = "userId")
//)
public class Admin extends BaseModel {

    /**
     * The ID of the user. This is the foreign key to user.
     */
    @Unique
    @OneToOne(mappedBy = "userid")
    private Integer userId;


    /**
     * The user the admin wants to edit as.
     */
    private Integer userIdToEdit;

    /**
     * True if admin is a Default admin.
     */
    private boolean isDefault;

    private static Finder<Integer, Admin> find = new Finder<>(Admin.class);


    /**
     * The constructor for the Admin that takes the parameters, userId, isDefault.
     *
     * @param userId    An Integer which is the UserId of the Admin User.
     * @param isDefault A boolean true if the Admin is a default admin else false.
     */
    public Admin(Integer userId, boolean isDefault) {
        this.userId = userId;
        this.isDefault = isDefault;
    }

    /**
     * Get's the EBeans finder for Admin
     *
     * @return A Finder<Integer, Admin> object.
     */
    public static Finder<Integer, Admin> find() {
        return find;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Integer getUserIdToActAs() {
        return userIdToEdit;
    }

    public void setUserToEdit(Integer userIdToEdit) {
        this.userIdToEdit = userIdToEdit;
    }
}
