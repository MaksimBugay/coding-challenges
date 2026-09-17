package bmv.longest_increasing_subsequence;

class LongestIncreasingSubsequenceContractTest extends LongestIncreasingSubsequenceContract {
    @Override
    protected Operation operationFor(String scenarioId) {
        return Result::longestIncreasingSubsequence;
    }
}
