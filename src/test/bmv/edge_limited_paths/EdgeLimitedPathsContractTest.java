package bmv.edge_limited_paths;

class EdgeLimitedPathsContractTest extends EdgeLimitedPathsContract {

  @Override
  protected Operation operationFor(String scenarioId) {
    return new EdgeLimitedPaths()::distanceLimitedPathsExist;
  }
}
