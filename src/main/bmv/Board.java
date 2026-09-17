package bmv;

import static java.lang.Math.abs;

import java.util.List;
import java.util.stream.Collectors;

public class Board {

  private final int boardSize;
  private final List<Point> obstacles;

  public Board(int boardSize, List<List<Integer>> obstacles, Point checkPoint) {
    this.boardSize = boardSize;
    this.obstacles = obstacles.stream()
        .filter(list -> checkPoint.row.equals(list.get(0))
            || checkPoint.column.equals(list.get(1))
            || abs(checkPoint.row - list.get(0)) == abs(checkPoint.column - list.get(1))
        )
        .map(list -> new Point(list.get(0), list.get(1)))
        .collect(Collectors.toList());
  }

  public boolean isPositionAllowed(Point position) {
    boolean result;
    if (position.row == 0 || position.row > boardSize) {
      result = false;
    } else if (position.column == 0 || position.column > boardSize) {
      result = false;
    } else {
      result = obstacles.stream()
          .noneMatch(
              ob -> position.row.equals(ob.row) && position.column.equals(ob.column));
    }
    return result;
  }
}
