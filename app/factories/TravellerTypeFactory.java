package factories;

import models.TravellerType;

import java.util.HashSet;
import java.util.Set;

public class TravellerTypeFactory {


    /**
     * A work-around due to a bug introduced working with Play. The bug was unexplained
     * but essentially a String object was being jammed into the place of a Set and so
     * this method unpacks the String and rebuilds the Set
     * @param travellerTypes The possibly malformed Set produced by PlayFramework
     * @return A well-formed Set of TravellerTypes
     */
    public static Set<TravellerType> formNewTravellerTypes(Set<TravellerType> travellerTypes) {

        Set<TravellerType> travellerTypesSet = new HashSet<>();

        String typesString = travellerTypes.toString();
        if (typesString.equals("[]") || typesString.equals("BeanSet size[0] set[]")) {
            return travellerTypesSet;
        }
        typesString = typesString.replaceAll("\\[|]", ""); //Trim off the set square brackets
        String[] types = typesString.split("\\s*,\\s"); // Split into array by the comma/whitespace delim
        for (String type: types) {
            TravellerType travellerType = TravellerType.find.query()
                    .where().eq("travellerTypeName", type).findOne();
            travellerTypesSet.add(travellerType);
        }
        return travellerTypesSet;
    }

}
