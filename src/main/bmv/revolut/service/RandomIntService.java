package bmv.revolut.service;

import java.util.Random;

public class RandomIntService {

  private static final Random rn = new Random();

  public int getRandomIndex(int max) {
    return rn.nextInt(max) + 1;
  }
}
