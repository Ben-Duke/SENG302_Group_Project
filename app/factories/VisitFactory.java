package factories;

import formdata.VisitFormData;
import models.Destination;
import models.Trip;
import models.Visit;


public class VisitFactory {

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
        visit.trip = trip;
        visit.destination = destination;
        visit.visitName = destination.destName;
        Integer visitSize = trip.getVisits().size();
        visit.visitorder = visitSize + 1;
        return visit;
    }
}
