package bmv.trimble.core;

public abstract class CommandWithParamsExecutor {

  protected final  Command command;

  protected final CommandInputParameters params;

  protected CommandWithParamsExecutor(Command command, CommandInputParameters params) {
    this.command = command;
    this.params = params;
  }

  public abstract String execute();
}
