package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Model;

import javax.inject.Inject;
import javax.persistence.*;
import java.nio.file.Paths;
import java.util.List;


interface Media {

    Integer getMediaId();

}
