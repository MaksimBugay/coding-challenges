package bmv.trimble.rdm.model;

import java.util.Map;
import java.util.UUID;

public record RefRecord(

    UUID id,
    int version,
    String refTableId,
    Map<String, String> data, //column name + value pairs
    Boolean deleted,
    Boolean deprecated
) {

}
