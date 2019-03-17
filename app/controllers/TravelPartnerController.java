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
import java.util.*;

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
        List<Nationality> nationalities = Nationality.find.all();
        Map<String, Boolean> genderMap = new TreeMap<>();
        genderMap.put("Male", true);
        genderMap.put("Female", true);
        genderMap.put("Other", true);


        return ok(searchprofile.render(dynamicForm, travellerTypes, nationalities, genderMap, resultProfiles));
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


    private List<User> nationalityResults(DynamicForm filterForm) {

        String nationality = filterForm.get("nationality");

        if (nationality != null) {
            List<User> results = Nationality.find.byId(Integer.parseInt(nationality)).getUsers();
            return results;
        }
        return new ArrayList<>();
    }

    private List<User> genderResults(DynamicForm filterForm) {

        List<String> genderSelections = new ArrayList<>();
        genderSelections.add(filterForm.get("gender[0"));
        genderSelections.add(filterForm.get("gender[1"));
        genderSelections.add(filterForm.get("gender[2"));

        List<User> results = new ArrayList<>();

        for (String gender: genderSelections) {
            if (gender != null) {

                List<User> query = User.find.query().where().eq("gender", gender).findList();
                for (User user: query) {
                    results.add(user);
                }
            }
        }

        return results;
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
//        String travellerType = filterForm.get("travellertype");












//        String gender = filterForm.get("gender");



        //change into slider thing in the future i guesss
//        String agerange1 = filterForm.get("agerange1");
//        String agerange2 = filterForm.get("agerange2");
//        Date date1 = null;
//        Date date2 = null;
//
//        if(agerange1 != null && agerange2 != null) {
//            try {
//                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange1);
//                date2 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange2);
//            } catch (ParseException e) {
//                //don't do anything here
//            }
//        }


        User user = User.getCurrentUser(request);

        if (user != null) {
            ArrayList<List<User>> userLists = new ArrayList<>();

            userLists.add(nationalityResults(filterForm));
            userLists.add(genderResults(filterForm));


            List<User> resultProfiles = UtilityFunctions.retainFromLists(userLists);


            //remove the current user from the list
            if(resultProfiles.contains(user)){
                resultProfiles.remove(user);
            }

            if(resultProfiles.size() == 0){
                resultProfiles.add(null);
            }

            //Redisplay the page, but this time with the search results
            return displayRenderedFilterPage(resultProfiles);


//            if(date1 != null && date2 != null){
//                List<User> userAgeRange = User.find.query().where().gt("dateOfBirth", date1).lt("dateOfBirth", date2).findList();
//                userLists.add(userAgeRange);
//            }
//            if(travellerType != null){
//                List<User> userTravellerType = TravellerType.find.byId(Integer.parseInt(travellerType)).getUsers();
//                userLists.add(userTravellerType);
//            }

        }
        else{
            return unauthorized("Oops, you're not logged in!");
        }
    }




}
