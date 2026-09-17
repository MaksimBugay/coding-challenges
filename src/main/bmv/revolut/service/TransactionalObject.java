package bmv.revolut.service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public abstract class TransactionalObject<T> {

  private final AtomicReference<TransactionParams<T>> transactionHolder =
      new AtomicReference<>(null);

  protected abstract T getCurrentValue();

  protected abstract void setCurrentValue(T value);

  public String startTransaction() {
    TransactionParams<T> transaction = transactionHolder.get();
    if (transaction != null) {
      throw new IllegalStateException("Busy with other transaction");
    }
    transaction = new TransactionParams<>(getCurrentValue());
    if (!transactionHolder.compareAndSet(null, transaction)) {
      throw new IllegalStateException("Busy with other transaction");
    }
    return transaction.id();
  }

  public void commit(String transactionId) {
    validateIdAndGetTransaction(transactionId);
    transactionHolder.set(null);
  }

  public void rollback(String transactionId) {
    if (transactionHolder.get() == null) {
      return;
    }
    TransactionParams<T> transaction = validateIdAndGetTransaction(transactionId);
    setCurrentValue(transaction.rollbackValue());
    transactionHolder.set(null);
  }

  public void rollbackSilently(String transactionId) {
    try {
      rollback(transactionId);
    } catch (Exception ex) {
      //put some logging here
    }
  }

  public void executeInTransaction(Runnable operation, String transactionId) {
    validateIdAndGetTransaction(transactionId);
    operation.run();
  }

  public <V> V callInTransaction(Supplier<V> operation, String transactionId) {
    validateIdAndGetTransaction(transactionId);
    return operation.get();
  }

  protected TransactionParams<T> validateIdAndGetTransaction(String id) {
    TransactionParams<T> transaction = transactionHolder.get();
    if (transaction == null) {
      throw new IllegalStateException("Transaction is not open");
    }
    if (!transaction.id().equals(id)) {
      throw new IllegalArgumentException("Wrong transaction id");
    }
    return transaction;
  }

  public record TransactionParams<T>(String id, T rollbackValue) {

    public TransactionParams(T rollbackValue) {
      this(UUID.randomUUID().toString(), rollbackValue);
    }
  }
}
