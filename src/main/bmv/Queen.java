package bmv;

import java.util.function.BiFunction;

public class Queen {

  private Point currentPosition;

  public Point getCurrentPosition() {
    return currentPosition;
  }

  public void setCurrentPosition(Point currentPosition) {
    this.currentPosition = currentPosition;
  }

  public Point doStep(QueenStep queenStep) {
    this.currentPosition = queenStep.getStep().apply(currentPosition.row, currentPosition.column);
    return currentPosition;
  }

  public enum QueenStep {
    UP((row, column) -> new Point(row + 1, column)),
    DOWN((row, column) -> new Point(row - 1, column)),
    LEFT((row, column) -> new Point(row, column - 1)),
    RIGHT((row, column) -> new Point(row, column + 1)),
    UP_LEFT((row, column) -> new Point(row + 1, column - 1)),
    UP_RIGHT((row, column) -> new Point(row + 1, column + 1)),
    DOWN_LEFT((row, column) -> new Point(row - 1, column - 1)),
    DOWN_RIGHT((row, column) -> new Point(row - 1, column + 1));

    private final BiFunction<Integer, Integer, Point> step;

    QueenStep(
        BiFunction<Integer, Integer, Point> step) {
      this.step = step;
    }

    public BiFunction<Integer, Integer, Point> getStep() {
      return step;
    }
  }
}
