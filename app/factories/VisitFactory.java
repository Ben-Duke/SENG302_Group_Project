package factories;

import formdata.VisitFormData;
import models.Destination;
import models.Trip;
import models.Visit;


public class VisitFactory {

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

    public Visit createVisitTable(Trip trip, Destination destination, Integer visitOrder){
        Visit visit = new Visit(null, null, trip, destination, visitOrder);
        return visit;
    }
}
