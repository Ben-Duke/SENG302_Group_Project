package models.commands.General;

import models.BaseModel;

/** A command (action) which cannot be undone */
public abstract class Command extends BaseModel {
    public abstract void execute();
}

