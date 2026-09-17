package bmv.trimble.rdm.model;

public class StringRefColumnDefinition extends TypedRefColumnDefinition<String> {

  public StringRefColumnDefinition(RefColumnDefinition refColumnDefinition) {
    super(refColumnDefinition, new StringValueValidatorAndConvertor());
  }

  @Override
  public RefColumnType getType() {
    return RefColumnType.STRING;
  }
}
