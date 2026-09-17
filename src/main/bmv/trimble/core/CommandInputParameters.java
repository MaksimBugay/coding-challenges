package bmv.trimble.core;

import java.util.HashMap;
import java.util.Map;

public class CommandInputParameters {

  private final Map<String, Object> params;


  public CommandInputParameters(Map<String, Object> params) {
    this.params = params == null ? new HashMap<>() : params;
  }

  public Object getParameterValue(String paramName) {
    return params.get(paramName);
  }
}
