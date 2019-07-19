package factories;

import formdata.VisitFormData;
import models.Destination;
import models.Trip;
import models.Visit;

/**
 * A class to handle interactions with  the database involving the Visit class.
 */
public class VisitFactory {

    /**
     * ells model to create a visit based on the data it is being passed
     * @param visitformdata formdata containing primitive data type like int, String etc which has been passed from
     *                      the front end
     * @param destination The destination of the visit being created
     * @param trip The trip the visit is being used for
     * @param visitorder The oder the visit is within the given trip
     * @return New visit object that is being created
     */
    public Visit createVisit(VisitFormData visitformdata, Destination destination, Trip trip, Integer visitorder) {
        Visit visit = new Visit();
        visit.trip = trip;
        visit.destination = destination;
        visit.visitorder = visitorder;
        visit.setArrival(visitformdata.arrival);
        visit.setDeparture(visitformdata.departure);
        visit.visitName = destination.destName;
        return visit;
    }

    /**
     * Creates a new visit object for the trip index page
     *
     * @param trip The trip the visit is a part of
     * @param destination The destination of the visit
     * @param visitOrder The order the visit is within the given trip
     * @return returns a new visit object for no arrival or departure date
     */
    public Visit createVisitTable(Trip trip, Destination destination, Integer visitOrder){
        Visit visit = new Visit(null, null, trip, destination, visitOrder);
        return visit;
    }
}
