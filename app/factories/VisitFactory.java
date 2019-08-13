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
        visit.setTrip(trip);
        visit.setDestination(destination);
        visit.setVisitorder(visitorder);
        visit.setArrival(visitformdata.arrival);
        visit.setDeparture(visitformdata.departure);
        visit.setVisitName(destination.getDestName());
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

    /**
     * Create a visit for a given trip from a destination
     * @param destination the destination that is to be a visit
     * @param trip the trip the visit is to be added to
     * @return the new visit
     */
    public Visit createVisitByJSRequest(Destination destination, Trip trip) {
        Visit visit = new Visit();
        visit.setTrip(trip);
        visit.setDestination(destination);
        visit.setVisitName(destination.getDestName());
        Integer visitSize = 0;
        if (trip.getVisits() != null) {
            visitSize = trip.getVisits().size();
        }
        Integer removedVisits = 0;
        if(trip.getRemovedVisits() != null) {
            removedVisits = trip.getRemovedVisits();
        }
        Integer visitOrder = visitSize + 1 + removedVisits;
        visit.setVisitorder(visitOrder);
        return visit;
    }
}
