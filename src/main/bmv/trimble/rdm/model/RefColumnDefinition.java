package bmv.trimble.rdm.model;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public record RefColumnDefinition(
    String name,
    RefColumnType type,
    Set<String> allowedValues, //for enumeration only
    List<Function<String, Boolean>> restrictions   //custom serialization/deserialization required
) {

}
