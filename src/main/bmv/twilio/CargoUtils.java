package bmv.twilio;

public final class CargoUtils {

  private CargoUtils() {
  }

  public static int calculateShippingCost(int length, int width, int height) {
    final long d = (long) length * width * height;
    if (d < 100_000) {
      return 10;
    }
    if (d > 500_000) {
      return 30;
    }
    return 20;
  }
}
