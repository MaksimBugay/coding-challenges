package bmv.bead_ornaments;

import java.util.List;

final class ResultBeadOrnamentsContractTest extends BeadOrnamentsContractTest {
    @Override
    protected int beadOrnaments(List<Integer> beadCounts) {
        return Result.beadOrnaments(beadCounts);
    }
}
