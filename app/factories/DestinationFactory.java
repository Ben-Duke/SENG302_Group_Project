package factories;

import formdata.DestinationFormData;
import models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle interactions with  the database involving the Destination class.
 */
public class DestinationFactory {

    /**
     * Gets a List of all public destinations.
     *
     * @return a List<Destination> of all public Destinations.
     */
    public List<Destination> getPublicDestinations() {
        List<Destination> allPublicDestinations = Destination.find.query()
                .where().eq("isPublic", true).findList();

        return allPublicDestinations;
    }

    /**
     * Gets the destination's primary photo
     * @param destID the id of the destination
     * @return the primary photo
     */
    public static UserPhoto getPrimaryPicture(int destID) {
        Destination destination = Destination.find.byId(destID);
        if (destination != null) {
            return destination.getPrimaryPhoto();
        }
       return null;
    }

    /**
     * Gets a List of all a users private destinations (excluding their own public
     * destinations).
     *
     * @param userId An int representing the users userId
     *
     * @return a List<Destination> of all the user's private Destinations.
     */
    public List<Destination> getUsersPrivateDestinations(int userId) {
        User user = UserFactory.getUserFromId(userId);

        List<Destination> privateDestinations = Destination.find.query()
                .where().eq("user", user).and().eq("isPublic", false)
                .findList();

        return privateDestinations;
    }

    /**
     * Checks if a destination is already a private destination of a User.
     *
     * Only checks a users destinations that are private (not just owned ones).
     *
     * @param userId An int representing the User's userId.
     * @param newPrivateDestination The new Destination to check.
     * @return A boolean, true if the User does have an equal private Destination,
     *         false otherwise.
     */
    public boolean userHasPrivateDestination(int userId, Destination newPrivateDestination) {
        List<Destination> privateDestinations;
        privateDestinations = this.getUsersPrivateDestinations(userId);

        for (Destination existingDestination: privateDestinations) {
            if (newPrivateDestination.equals(existingDestination)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a destination is already a public destination.
     *
     * Only checks public destinations (including the logged in users public destinations).
     *
     * @param destination The new Destination to check.
     * @return A boolean, true if an equivalent public destination exists, false
     * otherwise.
     */
    public boolean doesPublicDestinationExist(Destination destination) {
        List<Destination> allDestinations = this.getPublicDestinations();

        for (Destination existingDestination: allDestinations) {
            if (destination.equals(existingDestination)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return a DestinationFormData instance constructed from a destination instance
     */
    public DestinationFormData makeDestinationFormData(Destination dest) {
        return new DestinationFormData(dest.getDestName(), dest.getDestType(),
                dest.getDistrict(), dest.getCountry(), dest.getLatitude(),
                dest.getLongitude());
    }

    /**
     * Finds the list of all destinations from other users that are private and match the given destination
     * @param userId the user's own ID
     * @param destination the destination to check if there are matches to
     * @return The list of destinations from other users that match the given destination.
     */
    public List<Destination> getOtherUsersMatchingPrivateDestinations(int userId, Destination destination) {
        User user = UserFactory.getUserFromId(userId);
        // Get all destinations that are private and belong to another user
        List<Destination> allDestinations = Destination.find.query()
                .where().eq("isPublic", false).and()
                .not().eq("user", user).findList();

        List<Destination> matchingDestinations = new ArrayList<>();
        for (Destination existingDestination : allDestinations) {
            if (destination.equals(existingDestination)) {

                matchingDestinations.add(existingDestination);
            }
        }
        return matchingDestinations;
    }


    /**
     * Remove the private photos from a list of photos so that the list can be displayed publicly on a destination
     * Keeping the private content that the viewer owns
     * @param userPhotos the list of photos to display
     * @param viewerId the user Id of the person viewing the photos
     */
    public void removePrivatePhotos(List<UserPhoto> userPhotos, Integer viewerId) {
        ArrayList<UserPhoto> photosToRemove = new ArrayList<UserPhoto>();
        for (UserPhoto photo : userPhotos) {
            if(!photo.isPublic && photo.getUser().getUserid() != viewerId) {
                photosToRemove.add(photo);
            }
        }
        userPhotos.removeAll(photosToRemove);
    }

    /**
     * Returns a list of all public destinations and all private destinations that the user can see
     * @param userId the user accessing the destinations
     * @return the list of all destinations that the user is authorized to see
     */
    public List<Destination> getAllVisibleDestinations(int userId) {
        User user = UserFactory.getUserFromId(userId);
        if (user.userIsAdmin()) {
            return Destination.find.all();
        }

        List<Destination> visibleDestinations = new ArrayList<>();
        visibleDestinations.addAll(getPublicDestinations());
        visibleDestinations.addAll(getUsersPrivateDestinations(userId));

        return visibleDestinations;
    }

    /**
     * Move the photos from one destination to being linked to another
     * @param destinationOne the original destination which will no longer hold photos
     * @param destinationTwo the new destination which will hold new photos
     */
    private void movePhotosToAnotherDestination(Destination destinationOne, Destination destinationTwo) {
        while(!destinationOne.getUserPhotos().isEmpty()) {
            UserPhoto changingPhoto = destinationOne.getUserPhotos().get(0);
            changingPhoto.removeDestination(destinationOne);
            changingPhoto.addDestination(destinationTwo);
            destinationOne.getUserPhotos().remove(changingPhoto);
            changingPhoto.update();
            destinationOne.update();
            destinationTwo.update();
        }
    }

    /**
     * Move the visits from one destination to being linked to another
     * @param destinationOne the original destination which will no longer hold visits
     * @param destinationTwo the new destination which will hold new visits
     */
    private void moveVisitsToAnotherDestination(Destination destinationOne, Destination destinationTwo){
        List<Visit> visitsFrom = Visit.find.query().where().eq("destination", destinationOne).findList();
        for(Visit visit : visitsFrom) {
            visit.delete();
            //Note: Update this if new attributes are ever added to visit
            Visit newVisit = new Visit(visit.getArrival(), visit.getDeparture(), visit.getTrip(), destinationTwo, visit.getVisitOrder());
            newVisit.save();
            try {
                visit.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Merges all matching destination when one private destination is made public, will not merge if destination is used in trip
     * @param destinationList list of all matching private destinations
     * @param destination destination of user making private destination public
     * @return check to see if destinations are used in trips
     */
    public void mergeDestinations(List<Destination> destinationList, Destination destination) {
        Admin defaultAdmin = Admin.find.query().where().eq("isDefault", true).findOne();
        User defaultAdminUser = User.find.query().where().eq("userid", defaultAdmin.getUserId()).findOne();
        destinationList.add(destination);
        for (Destination otherDestination : destinationList) {
            if(otherDestination.getUser() != destination.getUser()) {
                moveVisitsToAnotherDestination(otherDestination, destination);
                otherDestination.setVisits(new ArrayList<>());
                try {
                    otherDestination.update();
                } catch (Exception e) {
                    System.out.println("merge destinations 1");
                    e.printStackTrace();
                }
                List<Visit> visits = Visit.find.query().where().eq("destination", otherDestination).findList();
                movePhotosToAnotherDestination(otherDestination, destination);
                try {
                    otherDestination.delete();
                } catch (Exception e) {
                    System.out.println("merge destinations 2");
                    e.printStackTrace();
                }
                try {
                    destination.update();
                } catch (Exception e) {
                    System.out.println("merge destinations 3");
                    e.printStackTrace();
                }
            }
        }
        destination.setIsPublic(true);
        destination.setUser(defaultAdminUser);
        try {
            destination.update();
        } catch (Exception e) {
            System.out.println("merge destinations 4");
            e.printStackTrace();
        }
    }
}
