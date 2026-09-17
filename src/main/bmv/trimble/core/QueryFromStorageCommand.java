package bmv.trimble.core;

public class QueryFromStorageCommand extends CommandWithParamsExecutor {

  private final OutQueryFromStoragePort outQueryFromStoragePort;

  protected QueryFromStorageCommand(OutQueryFromStoragePort outQueryFromStoragePort,
      CommandInputParameters params) {
    super(Command.QUERY_FROM_STORAGE, params);
    this.outQueryFromStoragePort = outQueryFromStoragePort;
  }

  @Override
  public String execute() {
    return outQueryFromStoragePort.query(params);
  }
}
