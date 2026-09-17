package bmv.trimble.core;

public class ApiCallCommand extends CommandWithParamsExecutor {

  private final OutApiCallPort outApiCallPort;

  protected ApiCallCommand(OutApiCallPort outApiCallPort, CommandInputParameters params) {
    super(Command.API_CALL, params);
    this.outApiCallPort = outApiCallPort;
  }


  @Override
  public String execute() {
    return outApiCallPort.call(params);
  }
}
