package models.commands.General;

import models.BaseModel;

/** A command (action) which cannot be undone */
public abstract class Command extends BaseModel {

    private CommandPage commandPage;

    public Command(CommandPage commandPage) {
        this.commandPage = commandPage;
    }

    CommandPage getCommandPage() {
        return commandPage;
    }

    public abstract void execute();
}

