package bmv.trimble.rdm.model;

public class StringValueValidatorAndConvertor implements ValueValidatorAndConvertor<String> {

  @Override
  public String convert(String value) {
    return value;
  }

  @Override
  public String print(String value) {
    return value;
  }
}
