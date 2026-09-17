package bmv.revolut.service;

import java.util.List;

public interface SimpleStorage<T> {

  T get(int index);

  void add(T element);

  void remove(T element);

  void removeByIndex(int index);

  List<T> getAll();
}
