package utilities;

import javafx.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventFindaUtilitiesTest  {

    /**
     * Filter by (cycling and running and swimming) or triathlon
     */
    @Test
    public void addFreeTextFilterToQuery(){
//        [Pair<[Pair<"cycling", "and">, Pair<"running", "and">, Pair<"swimming, "">], "or">, Pair<[Pair<"triathlon", ""], "">>]
//        List<Pair<List<Pair<String, String>>,String>>
        Pair pair1 = new Pair("cycling", "and");
        Pair pair2 = new Pair("running", "and");
        Pair pair3 = new Pair("swimming", "");
        List<Pair> pairCombination = new ArrayList();
        pairCombination.add(pair1);
        pairCombination.add(pair2);
        pairCombination.add(pair3);

    }
}
