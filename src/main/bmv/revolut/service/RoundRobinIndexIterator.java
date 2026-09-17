package bmv.revolut.service;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinIndexIterator extends BaseIndexIterator implements IndexIterator {

  private final AtomicInteger current = new AtomicInteger();

  public RoundRobinIndexIterator(int max) {
    super(max);
  }

  @Override
  public int getNext() {
    return current.updateAndGet(v -> (v < (getMax() - 1)) ? v + 1 : 0);
  }
}
