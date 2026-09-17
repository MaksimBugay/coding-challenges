package bmv.twilio;

public class PackageItem implements IPackage {

  private int id;

  private String name;

  private int weight;

  private int length;

  private int width;

  private int height;

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int getWeight() {
    return weight;
  }

  @Override
  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public int getLength() {
    return length;
  }

  @Override
  public void setLength(int length) {
    this.length = length;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void setWidth(int width) {
    this.width = width;
  }
}
