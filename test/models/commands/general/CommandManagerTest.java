package models.commands.general;

import accessors.UserAccessor;
import models.User;
import models.commands.Profile.EditProfileCommand;
import models.commands.Profile.EditProfileCommandTest;
import models.commands.UserPhotos.UploadPhotoCommandTest;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.route;

public class CommandManagerTest extends BaseTestWithApplicationAndDatabase {


    @Test
    public void checkEmptyStacksAtStart() {
        assertTrue(UserAccessor.getById(1).getCommandManager().isUndoStackEmpty());
        assertTrue(UserAccessor.getById(1).getCommandManager().isRedoStackEmpty());
    }

    @Test
    public void executeCommandCheckUndoNotEmptyAndRedoEmptyAfterExecute() {
        UploadPhotoCommandTest test = new UploadPhotoCommandTest();
        test.setUpDatabase();
        test.testExecute();
        assertFalse(UserAccessor.getById(1).getCommandManager().isUndoStackEmpty());
        assertTrue(UserAccessor.getById(1).getCommandManager().isRedoStackEmpty());
    }

    @Test
    public void undoCheckUndoEmptyAndRedoNotEmptyAfterExecuteAndUndo() {
        UploadPhotoCommandTest test = new UploadPhotoCommandTest();
        test.setUpDatabase();
        test.testUndo();
        assertTrue(UserAccessor.getById(1).getCommandManager().isUndoStackEmpty());
        assertFalse(UserAccessor.getById(1).getCommandManager().isRedoStackEmpty());
    }

    @Test
    public void redoCheckUndoNotEmptyAndRedoEmptyAfterExecuteAndUndoAndRedo() {
        UploadPhotoCommandTest test = new UploadPhotoCommandTest();
        test.setUpDatabase();
        test.testRedo();
        assertFalse(UserAccessor.getById(1).getCommandManager().isUndoStackEmpty());
        assertTrue(UserAccessor.getById(1).getCommandManager().isRedoStackEmpty());
    }

    @Test
    public void resetUndoRedoStackCheckBothStackEmptyAfterReset() {
        UploadPhotoCommandTest test = new UploadPhotoCommandTest();
        test.setUpDatabase();
        test.testRedo();
        UserAccessor.getById(1).getCommandManager().resetUndoRedoStack();
        assertTrue(UserAccessor.getById(1).getCommandManager().isUndoStackEmpty());
        assertTrue(UserAccessor.getById(1).getCommandManager().isRedoStackEmpty());
    }

    @Test
    public void resetUndoRedoStackCheckBothStackStillEmptyAfterReset() {
        UserAccessor.getById(1).getCommandManager().resetUndoRedoStack();
        assertTrue(UserAccessor.getById(1).getCommandManager().isUndoStackEmpty());
        assertTrue(UserAccessor.getById(1).getCommandManager().isRedoStackEmpty());
    }
}