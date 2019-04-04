package controllers;

import models.*;
import play.mvc.Result;
import utilities.UtilityFunctions;
import views.html.users.userIndex;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static play.mvc.Results.ok;

public class UserController {

    /**
     * Renders the user index page, which currently displays all users registered and contains links to logout, login
     * or register.
     * @return the user index page
     */
    public Result userindex(){
        UtilityFunctions.addNatAndPass();
        UtilityFunctions.addTravellerTypes();
        List<User> users = User.find.all();
        if (users.isEmpty()) {
            addDefaultUsers();
        }
        users = User.find.all();
        List<Admin> admins = Admin.find.all();
        return ok(userIndex.render(users, admins));
    }

    public void addDefaultUsers() {
        User user = new User("admin@admin.com", "admin", "admin", "admin", LocalDate.now(), "male");
        user.setDateOfBirth(LocalDate.of(2019, 2, 18));
        user.setTravellerTypes(TravellerType.find.all().subList(5, 6)); // Business Traveller
        user.setNationality(Nationality.find.all().subList(0, 2)); // First two countries alphabetically
        user.save();

        User user1 = new User("user1@admin.com", "user1", "John", "Doe", LocalDate.now(), "male");
        user1.setDateOfBirth(LocalDate.of(2019, 2, 18));
        user1.setTravellerTypes(TravellerType.find.all().subList(5, 6)); // Business Traveller
        user1.setNationality(Nationality.find.all().subList(0, 2)); // First two countries alphabetically
        user1.save();

        User user2 = new User("user2@admin.com", "user2", "James", "Doe", LocalDate.now(), "male");
        user2.setDateOfBirth(LocalDate.of(2019, 2, 18));
        user2.setTravellerTypes(TravellerType.find.all().subList(5, 6)); // Business Traveller
        user2.setNationality(Nationality.find.all().subList(0, 2)); // First two countries alphabetically
        user2.save();

        Admin admin = new Admin(user.userid, true);
        admin.save();
    }


}
