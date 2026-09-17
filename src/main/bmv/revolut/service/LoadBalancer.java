package bmv.revolut.service;

import bmv.revolut.model.Node;
import java.util.Collection;

public class LoadBalancer {

  public static final int REGISTRY_CAPACITY = 10;

  private final IndexIterator indexIterator;

  private final InMemoryStorage<Node> registry = new InMemoryStorage<>(REGISTRY_CAPACITY);

  public LoadBalancer(IndexIterator indexIterator) {
    this.indexIterator = indexIterator;
  }

  public LoadBalancer() {
    this.indexIterator = new RoundRobinIndexIterator(REGISTRY_CAPACITY);
  }

  public void register(Node newNode) {
    registry.add(newNode);
  }

  public Node getNode() {
    return registry.get(indexIterator.getNext());
  }

  public Collection<Node> getRegistry() {
    return registry.getAll();
  }

  public int getCurrentCapacity() {
    return registry.getCurrentCapacity();
  }
}
