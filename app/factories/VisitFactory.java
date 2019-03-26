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
}
