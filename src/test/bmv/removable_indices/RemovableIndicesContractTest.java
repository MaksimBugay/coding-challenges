package bmv.removable_indices;

class RemovableIndicesContractTest extends RemovableIndicesContract {

  @Override
  protected Operation operationFor(String scenarioId) {
    return Result::getRemovableIndices;
  }
}
