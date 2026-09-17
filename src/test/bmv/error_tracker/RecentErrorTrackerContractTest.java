package bmv.error_tracker;

class RecentErrorTrackerContractTest extends RecentErrorTrackerContract {

  @Override
  protected Operation operationFor(String scenarioId) {
    return Result::getErrorCodes;
  }
}
