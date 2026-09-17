package bmv.insertion_sort_advanced_analysis;

class InsertionSortContractTest extends InsertionSortContract {
    @Override
    protected Operation operationFor(String scenarioId) {
        return Result::insertionSort;
    }
}
