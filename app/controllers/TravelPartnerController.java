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
import java.util.SortedMap;

import static play.mvc.Results.*;

public class TravelPartnerController {

    @Inject
    FormFactory formFactory;

    /**
     * Initiates the form teh will take the search parameters.
     * Retrieves a list of all traveler types and all nationalities.
     * Renders the searchprofile page.
     *
     * @param resultProfiles all the profile resulting from the users search.
     *                       This will be empty when the user first enters the page
     * @return renders the search profile page
     */
    private Result displayRenderedFilterPage(List<User> resultProfiles) {

        DynamicForm dynamicForm = formFactory.form();
        List<TravellerType> travellerTypes = TravellerType.find.all();
        List<String> convertedTravellertypes = new ArrayList<>();
        convertedTravellertypes.add("");
        for (TravellerType traveller : travellerTypes) {
            convertedTravellertypes.add(traveller.travellerTypeName);
        }

        List<Nationality> nationalities = Nationality.find.all();




        return ok(searchprofile.render(dynamicForm, convertedTravellertypes, nationalities, resultProfiles));
    }


    /**
     * This method is called when the user first hits the searchprofile page.
     * Initialises an empty User list as there user has not searched anything yet.
     * @param request the http request
     * @return the filter page
     */
    public Result renderFilterPage(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            List<User> resultProfiles = new ArrayList<>();

            return displayRenderedFilterPage(resultProfiles);
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


        if(agerange1.equals("") || agerange2.equals("")) {
            try {
                if (agerange1.equals("") && !agerange2.equals("")) {
                    date1 = new Date(Long.MIN_VALUE);
                    date2 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange2);
                } else if (agerange2.equals("") && !agerange1.equals("")) {
                    date1 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange1);
                    date2 = new Date();
                } else if (!agerange1.equals("") && !agerange2.equals("")){
                    date1 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange1);
                    date2 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange2);
                }
            } catch (ParseException e) {
                //Do Nothing
                System.out.println(e);
            }
        }

        User user = User.getCurrentUser(request);
        if (user != null) {
            ArrayList<List<User>> userLists = new ArrayList<>();
            if(nationality != null) {
                if (nationality.equals("1")) {
                    System.out.println(User.find.all());
                    List<User> userNationality = User.find.all();
                    userLists.add(userNationality);
                } else {
                    List<User> userNationality = Nationality.find.byId(Integer.parseInt(nationality)).getUsers();
                    userLists.add(userNationality);
                }




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
                if (travellerType.equals("")) {
                    List<User> userTravellerType = User.find.all();
                    userLists.add(userTravellerType);
                } else {
                    List<TravellerType> travellerTypes = TravellerType.find.query().where().eq("travellerTypeName", travellerType).findList();
                    if (travellerTypes.size() > 0) {
                        List<User> userTravellerType = TravellerType.find.byId(travellerTypes.get(0).ttypeid).getUsers();
                        userLists.add(userTravellerType);
                    }

                }
            }

            List<User> resultProfiles = UtilityFunctions.retainFromLists(userLists);
            //remove the current user from the list
            if(resultProfiles.contains(user)){
                resultProfiles.remove(user);
            }
            if(resultProfiles.size() == 0){
                return badRequest("No users found!");
            }
            System.out.println(resultProfiles.size());
            //Redisplay the page, but this time with the search results
            return displayRenderedFilterPage(resultProfiles);
//            return ok("user found! This will later be converted to show the user's profile: " + userList.toString());
        }
        else{
            return unauthorized("Oops, you're not logged in!");
        }
    }




}
