package bmv.revolut.service;

import static bmv.revolut.service.WalletUtils.validateAndConvert;

import java.math.BigDecimal;

public class MyWallet extends TransactionalObject<BigDecimal> {

  private BigDecimal balance;

  public MyWallet(String initialBalance) {
    this.balance = validateAndConvert(initialBalance);
  }

  public void deposit(String amount) {
    BigDecimal depositAmount = validateAndConvert(amount);
    balance = balance.add(depositAmount);
  }

  public void withdraw(String amount) {
    BigDecimal withdrawalAmount = validateAndConvert(amount);
    if (balance.compareTo(withdrawalAmount) < 0) {
      throw new IllegalArgumentException("No enough funds");
    }
    balance = balance.subtract(withdrawalAmount);
  }

  public String getBalance() {
    return balance.toString();
  }

  @Override
  protected BigDecimal getCurrentValue() {
    return balance;
  }

  @Override
  protected void setCurrentValue(BigDecimal value) {
    this.balance = value;
  }
}
