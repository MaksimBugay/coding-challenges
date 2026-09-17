package bmv.trimble.adapters;

import bmv.trimble.core.CommandInputParameters;
import bmv.trimble.core.OutPersistStoragePort;

public class PersistStorageAdapter implements OutPersistStoragePort {

  @Override
  public boolean store(CommandInputParameters params) {
    return false;
  }
}
