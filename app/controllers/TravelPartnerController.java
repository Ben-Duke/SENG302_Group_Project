package controllers;

import accessors.FollowAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    private final int PSEUDO_INFINITE_NUMBER = 100000;

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
    private Result displayRenderedFilterPage(Set<User> resultProfiles, Set<User> followingProfiles, Set<User> followerProfiles, User user) {

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

        Map<String, Boolean> followMap = new TreeMap<>();
        followMap.put("Following", true);
        followMap.put("Followers", true);
        followMap.put("Others", true);
        followingProfiles = FollowAccessor.getAllUsersFollowed(user);
        followerProfiles = FollowAccessor.getAllUsersFollowing(user);

        return ok(searchprofile.render(user));
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
            Set<User> followingProfiles = new TreeSet<>();
            Set<User> followerProfiles = new TreeSet<>();
            return displayRenderedFilterPage(resultProfiles, followingProfiles, followerProfiles, user);
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
     * Returns the number of travellers that match the given filters, up to a maximum of 100,000
     * @return
     */
    public Result getTravellerCountWithFilters (
            Http.Request request, String travellerType, String nationality,
            String bornAfter, String bornBefore, String gender1, String gender2,
            String gender3, String getFollowers, String getFollowing) {
        User currentUser = User.getCurrentUser(request);
        if(currentUser == null){
            return unauthorized("You need to be logged in to use this api");
        }
        if (bornAfter == null) {
            bornAfter = "";
        }
        if (bornBefore == null) {
            bornBefore = "";
        }
        Set<User> users = UserAccessor.getUsersByQuery(travellerType, 0,
                PSEUDO_INFINITE_NUMBER, nationality, bornAfter, bornBefore, gender1,
                gender2, gender3, getFollowers, getFollowing);
        return ok(Integer.toString(users.size()));
    }

    /**
     * This function returns a Json object containing paginated travel partner search user data
     * @return returns a Json response with any users that match the passed parameters
     */
    public Result travellerSearchPaginated (
            Http.Request request, int offset, int quantity, String travellerType,
            String nationality, String bornAfter, String bornBefore, String gender1,
            String gender2, String gender3, String getFollowers, String getFollowing){
        User currentUser = User.getCurrentUser(request);
        if(currentUser == null){
            return unauthorized("You need to be logged in to use this api");
        }

        if(quantity > 1000){
            return badRequest("Limit is 1000 users per request");
        }

        if (bornAfter == null) {
            bornAfter = "";
        }
        if (bornBefore == null) {
            bornBefore = "";
        }
        if (getFollowing != null) {
            getFollowing = Integer.toString(currentUser.getUserid());
        }
        if (getFollowers != null) {
            getFollowers = Integer.toString(currentUser.getUserid());
        }
        Set<User> users = UserAccessor.getUsersByQuery(travellerType, offset, quantity,
                nationality, bornAfter, bornBefore, gender1, gender2, gender3, getFollowers,
                getFollowing);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode userNodes = objectMapper.createArrayNode();

        for (User user : users) {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("userId", user.getUserid());
            userNode.put("name", user.getFName() + " " + user.getLName());
            userNode.put("gender", user.getGender());
            userNode.put("dob", user.getDateOfBirth().toString());

            ArrayNode nationalityNodes = objectMapper.createArrayNode();
            for (Nationality userNationality : user.getNationality()) {
                nationalityNodes.add(userNationality.getNationalityName());
            }
            userNode.put("nationalities", nationalityNodes);

            ArrayNode travellerTypeNodes = objectMapper.createArrayNode();
            for (TravellerType userTravellerType : user.getTravellerTypes()) {
                travellerTypeNodes.add(userTravellerType.getTravellerTypeName());
            }
            userNode.put("travellerTypes", travellerTypeNodes);

            userNodes.add(userNode);
        }
        return ok(userNodes);
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
     * From the form retrieve all users from the database based on follow filters where:
     * having "following" checked will return a list of all the users that this user is following
     * having "followed" checked will return a list of all the users following this user
     * having "other" will return a list of all the users not following this user and
     * not being followed by this user
     *
     * @param filterForm the form object containing the users search selections
     * @return A list of all users that match the follow filters
     */
    private Set<User> followResults(DynamicForm filterForm, User user) {

        List<String> followSelections = new ArrayList<>();
        followSelections.add(filterForm.get("follow[0"));
        followSelections.add(filterForm.get("follow[1"));
        followSelections.add(filterForm.get("follow[2"));

        Set<User> results = new TreeSet<>();

        for (String follow: followSelections) {
            if (follow != null) {
                Set<User> query = new HashSet<>();
                if(follow.equalsIgnoreCase("following")) {
                    query = FollowAccessor.getAllUsersFollowed(user);
                }
                else if(follow.equalsIgnoreCase("followers")) {
                    query = FollowAccessor.getAllUsersFollowing(user);
                }
                else if(follow.equalsIgnoreCase("others")) {
                    Set<User> follows = FollowAccessor.getAllUsersFollowed(user);
                    Set<User> followers = FollowAccessor.getAllUsersFollowing(user);
                    follows.addAll(followers);
                    List<User> users = User.find().all();
                    query = new HashSet<>(users);
                    query.removeAll(follows);
                }
                results.addAll(query);
            }
        }

        return results;
    }


    /**
     * From the form retrieve all users from the database that have the given keyword in their name
     *
     * @param filterForm the form object containing the users search selections
     * @return A list of all users that match the name
     */
    private Set<User> travelerNameResults(DynamicForm filterForm) {
        String name = filterForm.get("name");
        if (name != null) {
            String[] splitedName = name.split("\\s+");
            if (name.equals("")) {
                return new HashSet<>();
            } else {
                if (splitedName.length > 1) {
                    return User.find().query().where().or().ilike("f_name", "%" + splitedName[0] + "%")
                            .like("l_name", "%" + splitedName[1] + "%").findSet();
                } else {
                    return User.find().query().where().or().ilike("f_name", "%" + name + "%")
                            .like("l_name", "%" + name + "%").findSet();
                }
            }
        }
        return new HashSet<>();
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

            Set<User> nameMatches = travelerNameResults(filterForm);
            if (!nameMatches.isEmpty()) {
                userLists.add(nameMatches);
            }

            Set<User> travelerTypeMatches = travelerTypeResults(filterForm);
            if (!travelerTypeMatches.isEmpty()) {
                userLists.add(travelerTypeMatches);
            }

            Set<User> nationalityMatches = nationalityResults(filterForm);
            if (!nationalityMatches.isEmpty()) {
                userLists.add(nationalityMatches);
            }

            Set<User> genderMatches = genderResults(filterForm);
            if (!genderMatches.isEmpty()) {
                userLists.add(genderMatches);
            }

            Set<User> followMatches = followResults(filterForm, user);
            if (!followMatches.isEmpty()) {
                userLists.add(followMatches);
            }

            Set<User> ageRangeMatches = ageRangeResults(filterForm);
            if (!ageRangeMatches.isEmpty()) {
                userLists.add(ageRangeMatches);
            }
            //Gets all common users from each search
            Set<User> resultProfiles = new HashSet<>();
            Set<User> followingProfiles = new HashSet<>();
            Set<User> followerProfiles = new HashSet<>();
            if (userLists.size() > 0) {
                resultProfiles = UtilityFunctions.retainFromLists(userLists);
            }


            //remove the current user from the list
            resultProfiles.remove(user);


            //Redisplay the page, but this time with the search results
            return displayRenderedFilterPage(resultProfiles, followingProfiles, followerProfiles,user);

        } else{
            return redirect(routes.UserController.userindex());
        }
    }


}
