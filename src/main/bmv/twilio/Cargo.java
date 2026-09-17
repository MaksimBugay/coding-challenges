package bmv.twilio;

import static bmv.twilio.CargoUtils.calculateShippingCost;
import static java.util.stream.Collectors.groupingBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Cargo implements IShipping {

  private final Map<Integer, IPackage> packages = new HashMap<>();

  @Override
  public void addPackage(IPackage pack) {
    packages.put(pack.getId(), pack);
  }

  @Override
  public void removePackage(int id) {
    packages.remove(id);
  }

  @Override
  public int calculateTotalCost() {
    return packages.values().stream()
        .map(item -> calculateShippingCost(item.getLength(), item.getWidth(), item.getHeight()))
        .reduce(Integer::sum).orElse(0);
  }

  @Override
  public Map<String, Integer> categoryPrices() {
    Map<CategoryPrice, List<IPackage>> packagesPerCategory = packages.values().stream()
        .collect(groupingBy(CategoryPrice::fromPackage));

    return packagesPerCategory.entrySet().stream()
        .collect(Collectors.toMap(
            entry -> entry.getKey().name(),
            entry -> entry.getValue().stream()
                .map(item -> calculateShippingCost(item.getLength(), item.getWidth(),
                    item.getHeight()))
                .reduce(Integer::sum).orElse(0))
        );
  }

  @Override
  public Map<String, Integer> packageList() {
    Map<String, List<IPackage>> packagesPerName = packages.values().stream()
        .collect(groupingBy(IPackage::getName));

    return packagesPerName.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().size()));
  }
}
