package factories;

import io.ebean.Transaction;
import models.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TripFactory {
    private static Logger logger = LoggerFactory.getLogger("application");

    public TripFactory(){//Just used to instanciate

    }


    /**
     * Updates the visit orders of
     * @param list
     * @param
     * @return
     */
    public boolean swapVisitsList(ArrayList<String> list){
        try (Transaction transaction = Visit.db().beginTransaction()) {
            int size = list.size();
            for(int i = 0; i < size; i++){
                Integer currentVisitId = Integer.parseInt(list.get(i));
                Visit visit = Visit.find.byId(currentVisitId);
                if(i < size - 1){
                    Visit visit2 = Visit.find.byId(Integer.parseInt(list.get(i+1)));
                    if(visit.getVisitName().equalsIgnoreCase(visit2.getVisitName())){
                        return false;
                    }
                }
                visit.setVisitorder(i);
                visit.update();
            }
            transaction.commit();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

}

//var prevPagesOrder = [];
//start: function(event, ui) {
//prevPagesOrder = $(this).sortable('toArray');
//},
// var currentOrder = $(this).sortable('toArray');
// var first = ui.item[0].id;
// var second = currentOrder[prevPagesOrder.indexOf(first)];