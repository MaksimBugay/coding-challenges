package bmv.worcato;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BoundedPriorityBlockingQueueWithEviction<E> {

  private final PriorityQueue<E> queue;
  private final ReentrantLock lock = new ReentrantLock();
  private final int capacity;

  public BoundedPriorityBlockingQueueWithEviction(int capacity, Comparator<E> comparator) {
    this.queue = new PriorityQueue<>(capacity, comparator);
    this.capacity = capacity;
  }

  public boolean offer(E e) {
    E lowestPriorityElement = queue.peek();
    if (lowestPriorityElement != null
        && queue.size() >= capacity
        && queue.comparator().compare(lowestPriorityElement, e) > 0) {
      return false; // Reject the new element as it has lower or equal priority
    }
    lock.lock();
    try {
      lowestPriorityElement = queue.peek();
      if (lowestPriorityElement != null && queue.size() >= capacity) {
        if (queue.comparator().compare(lowestPriorityElement, e) > 0) {
          return false; // Reject the new element as it has lower or equal priority
        }
        queue.poll(); // Remove the lowest priority element
      }
      return queue.offer(e);
    } finally {
      lock.unlock();
    }
  }

  public E poll() {
    lock.lock();
    try {
      return queue.poll();
    } finally {
      lock.unlock();
    }
  }

  public E peek() {
    lock.lock();
    try {
      return queue.peek();
    } finally {
      lock.unlock();
    }
  }

  public int size() {
    lock.lock();
    try {
      return queue.size();
    } finally {
      lock.unlock();
    }
  }

  public boolean isEmpty() {
    lock.lock();
    try {
      return queue.isEmpty();
    } finally {
      lock.unlock();
    }
  }

  public boolean remove(Object o) {
    lock.lock();
    try {
      return queue.remove(o);
    } finally {
      lock.unlock();
    }
  }

  public void clear() {
    lock.lock();
    try {
      queue.clear();
    } finally {
      lock.unlock();
    }
  }

  protected abstract E[] getConversionArray();

  public List<E> peekAll() {
    lock.lock();
    try {
      return List.of(queue.toArray(getConversionArray()));
    } finally {
      lock.unlock();
    }
  }

}
