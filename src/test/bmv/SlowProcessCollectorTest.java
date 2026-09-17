package bmv;

import bmv.worcato.LogRecord;
import bmv.worcato.SlowProcessCollector;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

public class SlowProcessCollectorTest {

  @Test
  void slowProcessCollectorTest() {
    long start = Instant.now().toEpochMilli();
    SlowProcessCollector slowProcessCollector = new SlowProcessCollector(10);

    List<LogRecord> logs1 = populateLogs(10_000_000);
    List<LogRecord> logs2 = populateLogs(10_000_000);
    /*logs1.stream().parallel().forEach(slowProcessCollector::offer);
    logs2.stream().parallel().forEach(slowProcessCollector::offer);*/
    /*try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
      logs1.forEach(el -> executor.submit(() -> slowProcessCollector.offer(el)));
      logs2.forEach(el -> executor.submit(() -> slowProcessCollector.offer(el)));
    }*/
    logs1.forEach(slowProcessCollector::offer);
    logs2.forEach(slowProcessCollector::offer);
    System.out.println("The slowest processes");
    List<LogRecord> slowProcesses = slowProcessCollector.peekAll();
    slowProcesses.forEach(System.out::println);
    System.out.println("Execution time = " + (Instant.now().toEpochMilli() - start));
  }

  public static List<LogRecord> populateLogs(int n) {
    List<LogRecord> logs = new ArrayList<>();
    for (int i = 0; i < 10_000_000; i++) {
      LogRecord newLogRecord = new LogRecord(
          "Process" + i,
          getRandomLongInRange(1, 100),
          Instant.now().toEpochMilli()
      );
      //System.out.println(newLogRecord);
      logs.add(newLogRecord);
    }
    return logs;
  }

  public static long getRandomLongInRange(long min, long max) {
    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }
    return min + (long) (ThreadLocalRandom.current().nextDouble() * (max - min));
  }
}
