package bmv.revolut.service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DistributedTransaction {

  private final Map<TransactionalObject<?>, String> mapping;

  public DistributedTransaction(Map<TransactionalObject<?>, String> mapping) {
    this.mapping = mapping;
  }

  public void executeInTransaction(TransactionalObject<?> object, Runnable operation) {
    object.executeInTransaction(operation, mapping.get(object));
  }

  public static void execute(List<TransactionalObject<?>> objects,
      Consumer<DistributedTransaction> operation) {
    Map<TransactionalObject<?>, String> mapping = null;
    try {
      mapping = objects.stream()
          .collect(Collectors.toMap(tr -> tr, TransactionalObject::startTransaction));
      operation.accept(new DistributedTransaction(mapping));
      mapping.forEach(TransactionalObject::commit);
    } catch (Exception ex) {
      if (mapping != null) {
        mapping.forEach(TransactionalObject::rollbackSilently);
      }
    }
  }
}
