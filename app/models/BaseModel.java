package models;

import controllers.ApplicationManager;
import io.ebean.Ebean;
import io.ebean.Model;
import javafx.application.Application;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/** Class which all models inherit from */
@MappedSuperclass
public class BaseModel extends Model {
    @Override
    /* Override ebean save so the save occurs on the current database
    *  There is no save(String databaseName) method so we have to use getServer
    */
    public void save() {
        Ebean.getServer(ApplicationManager.getDatabaseName()).save(this);
    }

    @Override
    /* Override ebean update so the update occurs on the current database */
    public void update() {
        super.update(ApplicationManager.getDatabaseName());
    }

    @Override
    /* Override ebean delete so it occurs on the current database */
    public boolean delete() {
        return super.delete(ApplicationManager.getDatabaseName());
    }


}
