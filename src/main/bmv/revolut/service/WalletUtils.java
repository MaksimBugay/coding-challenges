package bmv.revolut.service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class WalletUtils {

  private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

  private WalletUtils() {
  }

  public static BigDecimal validateAndConvert(String value) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("The balance cannot be null or empty.");
    }

    if (!NUMERIC_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("The balance must be a valid numerical value.");
    }

    try {
      return new BigDecimal(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("The balance is not a valid BigDecimal format.", e);
    }
  }
}
