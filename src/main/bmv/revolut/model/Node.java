package bmv.revolut.model;

import java.util.Objects;

public class Node {
  public final String address;

  public Node(String address) {
    if (address == null){
      throw new IllegalArgumentException("No address provided");
    }
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Node)) {
      return false;
    }
    Node node = (Node) o;
    return address.equals(node.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address);
  }
}
