package factories;

import formdata.TripFormData;
import io.ebean.Transaction;
import models.Trip;
import models.User;
import models.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A class to handle interactions with  the database involving the Trip class.
 */
public class TripFactory {
    private static Logger logger = LoggerFactory.getLogger("application");

    public TripFactory() {//Just used to instanciate

    }


    /**
     * Updates the visit orders of a trip to match a given array list of visit IDs.
     *
     * @param list An ArrayList of visits
     * @param userid the userid of the user
     * @return False if user accessing trip is not owner of trip or if duplicate visits
     * True if visit swapping is valid
     */
    public boolean swapVisitsList(ArrayList<String> list, Integer userid) {

        try (Transaction transaction = Visit.db().beginTransaction()) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Integer currentVisitId = Integer.parseInt(list.get(i));
                Visit visit = Visit.find().byId(currentVisitId);
                if (visit.getTrip().getUser().getUserid() != userid) {
                    return false;
                } else if (i < size - 1) {
                    Visit visit2 = Visit.find().byId(Integer.parseInt(list.get(i + 1)));
                    if (visit.getVisitName().equalsIgnoreCase(visit2.getVisitName())) {
                        return false;
                    }
                }
                visit.setVisitorder(i);
                visit.update();
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Inititalizes new trip using formData
     *
     * @param tripFormData Data to create new trip with
     * @param user User creating new trip
     * @return The new trip id
     */
    public int createTrip(TripFormData tripFormData, User user) {
        Trip trip = new Trip();
        trip.setTripName(tripFormData.tripName);
        trip.setUser(user);
        trip.setRemovedVisits(0);
        trip.setVisits(new ArrayList<Visit>());
        trip.save();
        return trip.getTripid();
    }

    /**
     * Handles actions where a visit within a trip is being changed either by deletion,
     * adding or moving to check for repeated visits being invalid
     *
     * @param visits List of all visits within a trip
     * @param visit the visit being changed within the trip
     * @param operation The way the visit is being changed
     * @return False if user accessing trip is not owner of trip or if duplicate visit
     * True if visit swapping is valid
     */
    public boolean hasRepeatDest(List<Visit> visits, Visit visit, String operation) {
        if (operation.equalsIgnoreCase("DELETE") && visits.size() > 2) {
            visits.sort(Comparator.comparing(Visit::getVisitOrder));
            Integer index = visits.indexOf(visit);
            Boolean deleteIndexCheck = index != 0 && (index + 1 != visits.size());
            if (deleteIndexCheck && visits.get(index - 1).getVisitName().equalsIgnoreCase(visits.get(index + 1).getVisitName())) {
                return true;
            }
        }
        if (operation.equalsIgnoreCase("ADD") && !visits.isEmpty()) {
            visits.sort(Comparator.comparing(Visit::getVisitOrder));
            if (visits.get(visits.size() - 1).getVisitName().equalsIgnoreCase(visit.getVisitName())) {
                //probably the wrong status header
                return true;
            }
        }
        if (operation.equalsIgnoreCase("SWAP")) {
            visits.sort(Comparator.comparing(Visit::getVisitOrder));
            Integer index = visits.indexOf(visit);
            if (index != 0) {
                if (!(visits.get(index - 1).getVisitName().equalsIgnoreCase(visit.getVisitName()))) {
                    return (visits.size() != index + 1) && (visits.get(index + 1).getVisitName().equalsIgnoreCase(visit.getVisitName()));
                } else {
                    return true;
                }
            } else {
                return visits.get(index + 1).getVisitName().equalsIgnoreCase(visit.getVisitName());
            }
        }
        return false;
    }

    public boolean hasRepeatDestSwap(List<Visit> visits, Visit visit1, Visit visit2) {
        visits.sort(Comparator.comparing(Visit::getVisitOrder));
        if (visits.size() > 2) {
            Integer index1 = visits.indexOf(visit1);
            Integer index2 = visits.indexOf(visit2);
            Collections.swap(visits, index1, index2);
            Integer temp1 = index1;
            index1 = index2;
            index2 = temp1;
            visit1 = visits.get(index1);
            visit2 = visits.get(index2);
            if (hasRepeatDest(visits, visit1, "SWAP") || hasRepeatDest(visits, visit2, "SWAP")) {
                return true;
            }
        }
        return false;
    }
}
