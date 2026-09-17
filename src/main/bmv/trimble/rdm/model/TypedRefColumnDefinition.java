package bmv.trimble.rdm.model;

public abstract class TypedRefColumnDefinition<T> {

  private final RefColumnDefinition refColumnDefinition;

  private final ValueValidatorAndConvertor<T> valueValidatorAndConvertor;

  public abstract RefColumnType getType();

  public TypedRefColumnDefinition(RefColumnDefinition refColumnDefinition,
      ValueValidatorAndConvertor<T> valueValidatorAndConvertor) {
    this.refColumnDefinition = refColumnDefinition;
    this.valueValidatorAndConvertor = valueValidatorAndConvertor;
  }
}
