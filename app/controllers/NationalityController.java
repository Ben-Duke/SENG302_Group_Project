package controllers;

import accessors.NationalityAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.Nationality;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.ok;

public class NationalityController {

    public Result getAllNationalities (Http.Request request) {
        List<Nationality> nationalities = NationalityAccessor.getAll();

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode nationalityNodes = objectMapper.createArrayNode();

        for (Nationality nationality : nationalities) {
            nationalityNodes.add(nationality.getNationalityName());
        }
        return ok(nationalityNodes);
    }
}
