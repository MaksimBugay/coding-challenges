package bmv.prefix;

class LongestCommonPrefixContractTest extends LongestCommonPrefixContract {

  @Override
  protected LongestCommonPrefix subjectFor(String scenarioId) {
    return new LongestCommonPrefix();
  }
}
