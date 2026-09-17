package bmv.trimble.rdm.model;

import java.util.UUID;

public record RefObject(
    UUID id,
    int version,
    RefObjectType type,
    String createdBy,
    long createdAt
) {

}
