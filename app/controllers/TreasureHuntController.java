package controllers;

import formdata.TripFormData;
import models.TreasureHunt;
import models.User;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.treasurehunt.indexTreasureHunt;
import views.html.users.trip.createTrip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.*;

public class TreasureHuntController {

    /**
     * If the user is logged in, renders the treasure hunt index page
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result indexTreasureHunt(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            return ok(indexTreasureHunt.render(user.getTreasureHunts(), getOpenTreasureHunts(), user);
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Gets a list of open treasure hunts. A treasure hunt is open if its start date is before the current date
     * and the end date is after the current date.
     * @return
     */
    public List<TreasureHunt> getOpenTreasureHunts(){
        List<TreasureHunt> treasureHunts = TreasureHunt.find.all();
        List<TreasureHunt> openTreasureHunts = new ArrayList<>();
        for(TreasureHunt treasureHunt : treasureHunts){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(treasureHunt.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(treasureHunt.getEndDate(), formatter);
            LocalDate currentDate = LocalDate.now();
            if(startDate.isBefore(currentDate) && endDate.isAfter(currentDate)){
                openTreasureHunts.add(treasureHunt);
            }
        }
        return openTreasureHunts;
    }
}
