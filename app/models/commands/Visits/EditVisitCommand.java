package models.commands.Visits;

import accessors.VisitAccessor;
import models.Visit;
import models.commands.Trips.TripPageCommand;

/**
 * Command to edit a visit
 */
public class EditVisitCommand extends TripPageCommand {

    private Visit uneditedVisit;
    private Visit editedVisit;
    private Visit actualVisit;

    /**
     * Create an edit visit command with a visit that has been edited
     * but changes have not been saved
     * @param visit the visit that is being edited
     */
    public EditVisitCommand(Visit visit) {
        this.editedVisit = new Visit();
        actualVisit = visit;
        this.editedVisit.applyEditChanges(visit);
        this.uneditedVisit = VisitAccessor.getById(visit.getVisitid());
    }

    /**
     * Execute the edit visit command
     */
    public void execute() {
        actualVisit.applyEditChanges(editedVisit);
        VisitAccessor.update(actualVisit);
    }

    /**
     * Undo the edit visit command (reverting to the old version of the visit)
     */
    public void undo() {
        actualVisit.applyEditChanges(uneditedVisit);
        VisitAccessor.update(actualVisit);
    }

    /**
     * Redo the edit visit command to go from the unedited version to the edited version
     */
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "Visit " + this.actualVisit.getVisitName() + " editing";
    }
}
