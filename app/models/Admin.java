package models;

import io.ebean.Finder;
import io.ebean.Model;
import org.checkerframework.common.aliasing.qual.Unique;
import play.mvc.Http;

import javax.persistence.*;

@Entity
public class Admin extends Model {

    /**
     * The ID of the admin. This is the primary key.
     */
    @Id
    public Integer adminId;

    /**
     * The ID of the admin. This is the primary key.
     */
    @Unique
    @OneToOne(mappedBy = "userid")
    public Integer userId;

    private boolean isDefault;

    public Admin(Integer userId, boolean isDefault){
        this.userId = userId;
        this.isDefault = isDefault;
    }

    public static Finder<Integer,Admin> find = new Finder<>(Admin.class);

    public Admin userToAdmin(Integer userId) {
        Admin admin = new Admin(userId, false);
        return admin;
    }

    public void adminToUser(Http.Request request, Admin admin) {
        Integer userId = admin.userId;
        Admin admin1 = Admin.find.query().where().eq("userid", userId).findOne();
        admin1.delete();
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
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
}
