package bmv.worcato;

import java.util.Comparator;

public class SlowProcessCollector extends BoundedPriorityBlockingQueueWithEviction<LogRecord> {

  public SlowProcessCollector(int capacity) {
    super(capacity, Comparator.comparingLong(LogRecord::executionTime));
  }

  @Override
  protected LogRecord[] getConversionArray() {
    return new LogRecord[0];
  }
}
