package models.commands.Destinations;

import accessors.*;
import models.*;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;
import org.slf4j.Logger;
import utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Command to delete a destination */
public class DeleteDestinationCommand extends UndoableCommand {
    private Destination destination;
    private Boolean deletedByAdmin;

    // Using sets as the items do not need to be ordered and are u nique
    private List<Visit> deletedVisits = new ArrayList<>();
    private List<TreasureHunt> deletedTreasureHunts = new ArrayList<>();
    private List<Album> deletedAlbums = new ArrayList<>();
    private Set<Tag> deletedTags = new HashSet<>();

    private final Logger logger = UtilityFunctions.getLogger();

    public DeleteDestinationCommand(Destination destination, Boolean deletedByAdmin) {
        super(CommandPage.MAP);
        this.destination = destination;
        this.deletedByAdmin = deletedByAdmin;
    }

    /**
     * Deletes the command's destination
     */
    public void execute() {

        // If admin, cascade deletion to visits and trips which use the destination
        if (deletedByAdmin) {
            List<Visit> visitsCopy = new ArrayList<>(destination.getVisits());

            for (Visit visit : visitsCopy) {
                deletedVisits.add(new Visit(visit));
                VisitAccessor.delete(visit);
            }
            List<TreasureHunt> treasureHunts = TreasureHuntAccessor.getByDestination(destination);
            for (TreasureHunt treasureHunt : treasureHunts) {
                deletedTreasureHunts.add(new TreasureHunt(treasureHunt));
                TreasureHuntAccessor.delete(treasureHunt);
            }
        }
        List<Album> albumsCopy = AlbumAccessor.getAlbumsByOwner(destination);
        for (Album album : albumsCopy) {
            deletedAlbums.add(new Album(album));
            AlbumAccessor.delete(album);
        }
        for (Tag tag : destination.getTags()) {
            deletedTags.add(tag);
        }
        destination.getTags().clear();
        destination.update();
        for (Tag tag: deletedTags) {
            TagAccessor.update(tag);
        }
        DestinationAccessor.delete(destination);
    }

    /**
     * Undoes the deletion of a Destination
     */
    public void undo() {
        this.destination = new Destination(destination, deletedVisits);
        DestinationAccessor.insert(destination);

        for (TreasureHunt treasureHunt : deletedTreasureHunts) {
            treasureHunt.setDestination(destination);
            TreasureHuntAccessor.insert(treasureHunt);
        }

        for (Visit visit : deletedVisits) {
            visit.setDestination(destination);
            VisitAccessor.insert(visit);
        }
        for (Album album : deletedAlbums) {
           // album
            album.setOwner(destination);
            AlbumAccessor.insert(album);
        }
        for (Tag tag: deletedTags) {
            Tag tagExisting = TagAccessor.getTagByName(tag.getName());
            boolean exists = tagExisting != null;
            if (!exists) {
                tag = new Tag(tag.getName());
                TagAccessor.insert(tag);
            }
            destination.addTag(tag);
            TagAccessor.update(tag);
            DestinationAccessor.update(destination);
        }
        deletedTags.clear();




    }

    /**
     * Redoes the previously executed undo
     */
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "Destination " + this.destination.getDestName() + " deletion";
    }
}



























