package controllers;

import accessors.UserAccessor;
import io.ebean.Query;
import models.Destination;
import models.Nationality;
import play.libs.Json;
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
import javax.swing.text.html.Option;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
     * @param user The User currently logged in
     * @return renders the search profile page
     */
    private Result displayRenderedFilterPage(Set<User> resultProfiles, User user) {

        DynamicForm dynamicForm = formFactory.form();

        List<TravellerType> travellerTypes = TravellerType.find().all();
        List<String> convertedTravellerTypes = new ArrayList<>();
        convertedTravellerTypes.add("");
        for (TravellerType traveller : travellerTypes) {
            convertedTravellerTypes.add(traveller.getTravellerTypeName());
        }

        List<Nationality> nationalities = Nationality.find().all();
        List<String> convertedNationalities = new ArrayList<>();
        convertedNationalities.add("");
        for (Nationality nationality : nationalities) {
            convertedNationalities.add(nationality.getNationalityName());
        }

        Map<String, Boolean> genderMap = new TreeMap<>();
        genderMap.put("Male", true);
        genderMap.put("Female", true);
        genderMap.put("Other", true);

        return ok(searchprofile.render(dynamicForm, convertedTravellerTypes, convertedNationalities, genderMap, resultProfiles, user));
    }

    /**
     * This method is called when the user first hits the searchprofile page.
     * Initialises an empty User list as there user has not searched anything yet.
     *
     * @param request the http request
     * @return the filter page
     */
    public Result renderFilterPage(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Set<User> resultProfiles = new TreeSet<>();

            return displayRenderedFilterPage(resultProfiles, user);
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * From the form retrieve all users from the database that have the given traveler type
     *
     * @param filterForm the form object containing the users search selections
     * @return A list of all users that match the traveler type
     */
    private Set<User> travelerTypeResults(DynamicForm filterForm) {
        String travellerType = filterForm.get("travellertype");

        if (travellerType != null) {
            if (travellerType.equals("")) {
                return new HashSet<>();

            } else {
                List<TravellerType> travellerTypes = TravellerType.find().query().where().eq("travellerTypeName", travellerType).findList();
                if (!travellerTypes.isEmpty()) {
                    return TravellerType.find().byId(travellerTypes.get(0).getTtypeid()).getUsers();
                }

            }
        }
        return new HashSet<>();
    }

    /**
     * This function returns a paginated list of users based on passed parameters
     * @param request
     * @param offset
     * @param quantity
     * @param travellerType
     * @param nationality
     * @return returns a Json response with any users that match the passed parameters
     */
    public Result travellerTypePaginated(
            Http.Request request, int offset, int quantity, String travellerType, String nationality, String bornAfter, String bornBefore, String gender){
        System.out.println("traveller type is " + travellerType);
        User user = User.getCurrentUser(request);
        if(user == null){
            return unauthorized("You need to be logged in to use this api");
        }

        if(quantity > 1000){
            return badRequest("Limit is 1000 users per request");
        }

        if(quantity < 0){

        }
        if (bornAfter == null) {
            bornAfter = "";
        }
        if (bornBefore == null) {
            bornBefore = "";
        }
        Set<User> users = UserAccessor.getUsersByQuery(travellerType, offset,quantity,nationality, bornAfter, bornBefore, gender);

        for(User userTemp : users){
            System.out.println(userTemp.toString());
        }

        return ok(Json.toJson(users));
    }


    /**
     * From the form retrieve all users from the database that have the given nationality
     *
     * @param filterForm the form object containing the users search selections
     * @return A list of all users that match the nationality
     */
    private Set<User> nationalityResults(DynamicForm filterForm) {

        String nationality = filterForm.get("nationality");
        if (nationality != null) {
            if (nationality.equals("")) {
                return new HashSet<>();

            } else {
                List<Nationality> nationalities = Nationality.find().query().where().eq("nationalityName", nationality).findList();
                if (nationalities.size() > 0) {
                    return Nationality.find().byId(nationalities.get(0).getNatId()).getUsers();
                }
            }
        }
        return new HashSet<>();
    }

    /**
     * From the form retrieve all users from the database that have the given genders selected
     *
     * @param filterForm the form object containing the users search selections
     * @return A list of all users that match the genders
     */
    private Set<User> genderResults(DynamicForm filterForm) {

        List<String> genderSelections = new ArrayList<>();
        genderSelections.add(filterForm.get("gender[0"));
        genderSelections.add(filterForm.get("gender[1"));
        genderSelections.add(filterForm.get("gender[2"));

        Set<User> results = new TreeSet<>();

        for (String gender: genderSelections) {
            if (gender != null) {

                List<User> query = User.find().query().where().eq("gender", gender).findList();
                results.addAll(query);
            }
        }

        return results;
    }

    /**
     * From the form retrieve all users from the database that have the given date range.
     * If one of the date selector has not been chosen then that field is not used as a bound
     *
     * @param filterForm the form object containing the users search selections
     * @return A list of all users that match the date range
     */
    private Set<User> ageRangeResults(DynamicForm filterForm) {

        String agerange1 = filterForm.get("agerange1");
        String agerange2 = filterForm.get("agerange2");
        return UserAccessor.getUsersWithAgeRange(agerange1, agerange2);
    }


    /**
     * Handles the request to search users by attributes.
     * Add results from all parameters to a list, find all common users between each search criteria.
     *
     * @param request the HTTP request
     * @return List of users or error message
     */
    public Result searchByAttribute(Http.Request request){

        DynamicForm filterForm = formFactory.form().bindFromRequest();


        User user = User.getCurrentUser(request);

        if (user != null) {
            List<Set<User>> userLists = new ArrayList<>();

            Set<User> travelerTypeMatches = travelerTypeResults(filterForm);
            if (!travelerTypeMatches.isEmpty()) {
                userLists.add(travelerTypeMatches);
            }

            Set<User> nationalityMatches = nationalityResults(filterForm);
            if (!nationalityMatches.isEmpty()) {
                userLists.add(nationalityMatches);
            }

            userLists.add(genderResults(filterForm));

            Set<User> ageRangeMatches = ageRangeResults(filterForm);
            if (!ageRangeMatches.isEmpty()) {
                userLists.add(ageRangeMatches);
            }

            //Gets all common users from each search
            Set<User> resultProfiles = UtilityFunctions.retainFromLists(userLists);


            //remove the current user from the list
            resultProfiles.remove(user);


            //Redisplay the page, but this time with the search results
            return displayRenderedFilterPage(resultProfiles,user);

        } else{
            return redirect(routes.UserController.userindex());
        }
    }


}
