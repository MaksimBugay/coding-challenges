package bmv.solve_me_first;

import java.util.function.IntBinaryOperator;

class SolveMeFirstContractTest extends SolveMeFirstContract {

  @Override
  protected IntBinaryOperator subjectFor(String scenarioId) {
    return SolveMeFirst::solveMeFirst;
  }
}
