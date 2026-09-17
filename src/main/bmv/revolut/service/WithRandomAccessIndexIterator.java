package bmv.revolut.service;

import java.util.SplittableRandom;

public class WithRandomAccessIndexIterator extends BaseIndexIterator implements IndexIterator {

  private final SplittableRandom random = new SplittableRandom();

  public WithRandomAccessIndexIterator(int max) {
    super(max);
  }

  @Override
  public int getNext() {
    return random.nextInt(getMax());
  }
}
