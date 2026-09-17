package bmv.revolut.service;

import bmv.revolut.model.Account;
import java.util.List;

public class BalanceTransferService {

  public void transfer(MyWallet w1, MyWallet w2, String amount) {
    DistributedTransaction.execute(List.of(w1, w2), transaction -> {
      transaction.executeInTransaction(w1, () -> w1.withdraw(amount));
      transaction.executeInTransaction(w2, () -> w2.deposit(amount));
    });
  }

  public void transfer(Account from, Account to, double amount) {
    if (amount == 0.0) {
      return;
    }
    if (amount < 0.0) {
      throw new IllegalArgumentException("Negative amount");
    }
    synchronized (from) {
      synchronized (to) {
        if (from.getBalance() < amount) {
          throw new IllegalArgumentException("Not enough funds");
        }
        from.updateBalance(-amount);
        to.updateBalance(amount);
      }
    }
  }

}
