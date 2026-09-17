package bmv;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GridSearchTest {

  @Test
  void gridSearchTest() {
    Assertions.assertEquals("YES",
        GridSearch.gridSearch(
            List.of(
                "111111111111111"
                , "111111111111111"
                , "111111011111111"
                , "111111111111111"
                , "111111111111111"),
            List.of(
                "11111"
                , "11111"
                , "11110"))
    );

    Assertions.assertEquals("YES",
        GridSearch
            .gridSearch(List.of("123412", "561212", "123634", "781288"), List.of("12", "34"))
    );

  }

}
