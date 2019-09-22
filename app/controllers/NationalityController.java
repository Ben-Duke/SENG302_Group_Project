package controllers;

import accessors.NationalityAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.Nationality;
import models.User;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.ok;
import static play.mvc.Results.unauthorized;

public class NationalityController {

    /**
     * Returns a list of all nationalities in the database
     * @param request the http request
     * @return a list of all nationalities in the database
     */
    public Result getAllNationalities (Http.Request request) {
        User currentUser = User.getCurrentUser(request);
        if(currentUser == null){
            return unauthorized("You need to be logged in to use this api");
        }

        List<Nationality> nationalities = NationalityAccessor.getAll();

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode nationalityNodes = objectMapper.createArrayNode();

        for (Nationality nationality : nationalities) {
            nationalityNodes.add(nationality.getNationalityName());
        }
        return ok(nationalityNodes);
    }
}
