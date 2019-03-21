package factories;

import formdata.DestinationFormData;
import models.Destination;
import play.data.Form;
import play.data.FormFactory;

import javax.inject.Inject;

public class DestinationFactory {

//    @Inject
//    FormFactory formFactory;

    public DestinationFormData getFormData(int destId) {
        Destination destination = Destination.find.query().where().eq("destid", destId).findOne();
        if (destination == null){
            return null;
        }
        System.out.println(destination);
        System.out.println(destination.destName);
        System.out.println(destination.destType);
        System.out.println(destination.country);
        System.out.println(destination.longitude);
        System.out.println(destination.latitude);
        System.out.println(destination.district);


        DestinationFormData destinationFormData = new DestinationFormData(destination);
        return destinationFormData;
    }

    public Form<DestinationFormData> getForm(FormFactory formFactory, DestinationFormData formData) {
        if (formData == null) return null;
//        System.out.println("form factory =" + formFactory);
        Form<DestinationFormData> formDataForm = formFactory.form(DestinationFormData.class).fill(formData);
        return formDataForm;
    }
}

