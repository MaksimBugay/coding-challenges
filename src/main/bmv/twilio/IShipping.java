package bmv.twilio;

import java.util.Map;

public interface IShipping {
    void addPackage(IPackage pack);
    void removePackage(int id);
    int calculateTotalCost();
    Map<String, Integer> categoryPrices();
    Map<String, Integer> packageList();
}
