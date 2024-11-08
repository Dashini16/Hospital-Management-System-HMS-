package lookups;

import java.util.List;
import java.util.function.Predicate;

public class UserLookup {

    // Generic lookup method using a predicate
    public <T> T findByID(String id, List<T> dataList, Predicate<T> matchCondition) {
        for (T item : dataList) {
            if (matchCondition.test(item)) {
                return item;
            }
        }
        return null;
    }
}
