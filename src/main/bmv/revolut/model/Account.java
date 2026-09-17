package bmv.revolut.model;

import java.util.UUID;

public class Account {

  private final UUID id;

  private double balance;

  public Account(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }

  public double getBalance() {
    return balance;
  }

  public synchronized void updateBalance(double increment) {
    this.balance = balance + increment;
  }
}
