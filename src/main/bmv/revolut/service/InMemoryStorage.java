package bmv.revolut.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryStorage<T> implements SimpleStorage<T> {

  private final int capacity;

  private final AtomicInteger currentCapacity = new AtomicInteger();

  CopyOnWriteArrayList<T> storage = new CopyOnWriteArrayList<>();

  public InMemoryStorage(int capacity) {
    this.capacity = capacity;
  }

  @Override
  public T get(int index) {
    return storage.get(index);
  }

  @Override
  public void add(T element) {
    if (currentCapacity.incrementAndGet() > capacity) {
      throw new IllegalStateException("Registry is full");
    }
    boolean result = false;
    try {
      result = storage.addIfAbsent(element);
      if (!result) {
        throw new IllegalArgumentException("Element is already registered");
      }
    } finally {
      if (!result) {
        currentCapacity.decrementAndGet();
      }
    }
  }

  @Override
  public void remove(T element) {
    storage.remove(element);
  }

  @Override
  public void removeByIndex(int index) {
    storage.remove(index);
  }

  @Override
  public List<T> getAll() {
    return storage.subList(0, currentCapacity.get());
  }

  public int getCurrentCapacity() {
    return currentCapacity.get();
  }
}
