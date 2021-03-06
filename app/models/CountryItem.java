package models;

import io.ebean.Model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


/** Parent class for Nationality and Passport giving them isValidContry property */
@MappedSuperclass
public abstract class CountryItem  extends BaseModel {

    @Column
    private Boolean countryValid;


    public CountryItem() {
        this.countryValid = true;
    }

    public Boolean getCountryValid() {
        return countryValid;
    }

    public void setCountryValid(Boolean countryValid) {
        this.countryValid = countryValid;
    }
}
