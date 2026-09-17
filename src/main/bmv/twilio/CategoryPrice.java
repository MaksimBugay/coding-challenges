package bmv.twilio;

public enum CategoryPrice {

  SMALL, MEDIUM, LARGE;

  public static CategoryPrice fromPackage(IPackage pack) {

    if (pack.getWeight() <= 1 && pack.getLength() <= 30 && pack.getWidth() <= 30
        && pack.getHeight() <= 30) {
      return SMALL;
    }
    if (pack.getWeight() <= 3 && pack.getLength() <= 60 && pack.getWidth() <= 60
        && pack.getHeight() <= 60) {
      return MEDIUM;
    }
    return LARGE;
  }

}
