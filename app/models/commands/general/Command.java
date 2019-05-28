package models.commands.general;

import models.BaseModel;

import javax.persistence.MappedSuperclass;

/** A command (action) which cannot be undone */
public abstract class Command extends BaseModel {
    public abstract void execute();
}

