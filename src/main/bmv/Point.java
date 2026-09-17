package bmv;

import java.util.Objects;

public class Point {

    public final Integer row;
    public final Integer column;

    public Point(Integer row, Integer column) {
      this.row = row;
      this.column = column;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Point)) {
        return false;
      }
     Point point = (Point) o;
      return row.equals(point.row) && column.equals(point.column);
    }

    @Override
    public int hashCode() {
      return Objects.hash(row, column);
    }
  }