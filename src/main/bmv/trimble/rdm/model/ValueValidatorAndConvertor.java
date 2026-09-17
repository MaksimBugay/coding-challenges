package bmv.trimble.rdm.model;

import java.util.Collection;
import java.util.Optional;

public interface ValueValidatorAndConvertor<V> {

  V convert(String value);

  default String print(V value) {
    return value.toString();
  }

  default V validateAndExtract(RefColumnDefinition columnDefinition, String value) {
    boolean allowed = Optional.ofNullable(columnDefinition.allowedValues())
        .filter(set -> set.contains(value)).isPresent();

    if (!allowed) {
      throw new IllegalArgumentException();
    }

    boolean restricted = Optional.ofNullable(columnDefinition.restrictions())
        .stream()
        .flatMap(Collection::stream)
        .map(restriction -> restriction.apply(value))
        .anyMatch(Boolean.FALSE::equals);

    if (restricted) {
      throw new IllegalArgumentException();
    }

    return convert(value);
  }
}
