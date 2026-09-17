package bmv.trimble.rdm.model;

import java.util.UUID;

public record GlobalFilter(
    UUID id,
    GlobalFilterType type,
    String name
) {

}
