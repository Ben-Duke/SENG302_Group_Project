package models.commands;

import models.BaseModel;

import javax.persistence.MappedSuperclass;

/** A command (action) which cannot be undone */
@MappedSuperclass
public abstract class Command extends BaseModel {
    public abstract void execute();
}