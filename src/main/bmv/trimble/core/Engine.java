package bmv.trimble.core;

import java.util.List;
import java.util.stream.Collectors;

public class Engine {

  public List<String> execute(List<CommandWithParamsExecutor> commands) {
    return commands.stream()
        .map(CommandWithParamsExecutor::execute)
        .collect(Collectors.toList());
  }

}
