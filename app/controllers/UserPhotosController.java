package controllers;

import views.html.users.userPhotos;
import play.mvc.Result;
import static play.mvc.Results.ok;


public class UserPhotosController {

    public Result addUserPhotoPage() {
        Form<AddUserPhotoData> addUserPhotoFormData = formFactory.form(AddUserPhotoData.class);
        return ok(userPhotos.render(addUserPhotoFormData));
    }


}