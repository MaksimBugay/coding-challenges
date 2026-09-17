package bmv.log_analysis;

class LogAnalysisContractTest extends LogAnalysisContract {

  @Override
  protected Operation operationFor(String scenarioId) {
    return Result::getStaleServerCount;
  }
}
