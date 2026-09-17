package bmv.trimble.core;

public class PersistStorageCommand extends CommandWithParamsExecutor {

  private final OutPersistStoragePort outPersistStoragePort;

  protected PersistStorageCommand(OutPersistStoragePort outPersistStoragePort, CommandInputParameters params) {
    super(Command.PERSIST_TO_STORAGE, params);
    this.outPersistStoragePort = outPersistStoragePort;
  }

  @Override
  public String execute() {
    return String.valueOf(outPersistStoragePort.store(params));
  }
}
