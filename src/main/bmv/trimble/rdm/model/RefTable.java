package bmv.trimble.rdm.model;

import java.util.Set;
import java.util.UUID;

public record RefTable(
    UUID id,
    int version,
    String name,
    String displayName,
    String description,
    Set<RefColumnDefinition> columnsMetadata,
    SraState sraState,
    boolean deleted,
    boolean deprecated
) {

}
