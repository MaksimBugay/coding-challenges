package bmv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import bmv.sort.quick.QuickSort;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class MockitoTest {

  @Test
  void staticMethodMockTest() {
    try (MockedStatic<GridSearch> classMock = mockStatic(GridSearch.class)) {

      classMock.when(() -> GridSearch.gridSearch(anyList(), anyList())).thenReturn("YES");

      assertEquals("YES", GridSearch.gridSearch(List.of(), List.of()));
    }
    assertEquals("NO", GridSearch.gridSearch(List.of(), List.of()));
  }

  @Test
  void quickSortTest() {
    long[] input = new long[] {10, 80, 30, 90, 40, 60, 50, 11, 3};
    QuickSort quickSortMock = Mockito.mock(QuickSort.class, withSettings().useConstructor(input));
    when(quickSortMock.sort()).thenReturn(new long[] {10, 80});
    System.out.println(Arrays.toString(quickSortMock.sort()));
  }
}
