package bmv.revolut.service;

public abstract class BaseIndexIterator {

  protected final int max;

  protected BaseIndexIterator(int max) {
    this.max = max;
  }

  public int getMax() {
    return max;
  }
}
