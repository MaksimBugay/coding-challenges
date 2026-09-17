package bmv;

import bmv.revolut.service.RandomIntService;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomIntServiceTampered extends RandomIntService {

  private final AtomicInteger current = new AtomicInteger();

  @Override
  public int getRandomIndex(int max) {
    return current.updateAndGet(v -> {
      v = v + 1;
      if (v > max) {
        v = 1;
      }
      return v;
    });
  }

  public void reset() {
    current.set(0);
  }
}
