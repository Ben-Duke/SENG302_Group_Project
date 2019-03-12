package controllers;

import models.Nationality;
import utilities.UtilityFunctions;
import models.TravellerType;
import models.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import utilities.UtilityFunctions;
import views.html.users.travelpartner.searchprofile;

import javax.inject.Inject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.mvc.Results.*;

public class TravelPartnerController {

    @Inject
    FormFactory formFactory;

    /**
     * Renders the page to search for users by attributes.
     * @param request the http request
     * @return the filter page
     */
    public Result renderFilterPage(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            DynamicForm dynamicForm = formFactory.form();
            List<TravellerType> travellerTypes = TravellerType.find.all();
            List<Nationality> nationalities = Nationality.find.all();
            return ok(searchprofile.render(dynamicForm, travellerTypes, nationalities));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Handles the request to search users by attributes.
     * If the user's attributes fits within the given input, the user is added to a list which is returned.
     * If there's no users in the list then return a bad request error.
     * @param request the HTTP request
     * @return List of users or error message
     */
    public Result searchByAttribute(Http.Request request){
        DynamicForm filterForm = formFactory.form().bindFromRequest();
        String travellerType = filterForm.get("travellertype");
        String nationality = filterForm.get("nationality");
        String gender = filterForm.get("gender");
        //change into slider thing in the future i guesss
        String agerange1 = filterForm.get("agerange1");
        String agerange2 = filterForm.get("agerange2");
        Date date1 = null;
        Date date2 = null;
        if(agerange1 != null && agerange2 != null) {
            try {
                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange1);
                date2 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange2);
            } catch (ParseException e) {
                //don't do anything here
            }
        }
        User user = User.getCurrentUser(request);
        if (user != null) {
            ArrayList<List<User>> userLists = new ArrayList<>();
            if(nationality != null) {
                List<User> userNationality = Nationality.find.byId(Integer.parseInt(nationality)).getUsers();
                userLists.add(userNationality);
            }
            if(gender != null){
                List<User> userGender = User.find.query().where().eq("gender", gender).findList();
                userLists.add(userGender);
            }
            if(date1 != null && date2 != null){
                List<User> userAgeRange = User.find.query().where().gt("dateOfBirth", date1).lt("dateOfBirth", date2).findList();
                userLists.add(userAgeRange);
            }
            if(travellerType != null){
                List<User> userTravellerType = TravellerType.find.byId(Integer.parseInt(travellerType)).getUsers();
                userLists.add(userTravellerType);
            }

            List<User> userList = UtilityFunctions.retainFromLists(userLists);
            //remove the current user from the list
            if(userList.contains(user)){
                userList.remove(user);
            }
            if(userList.size() == 0){
                return badRequest("No users found!");
            }
            return ok("user found! This will later be converted to show the user's profile: " + userList.toString());
        }
        else{
            return unauthorized("Oops, you're not logged in!");
        }
    }
}
