package json4java._symboltable;

public class JSON4JavaLanguage extends JSON4JavaLanguageTOP {
  public static final String FILE_ENDING = "json";
  
  public JSON4JavaLanguage() {
    super("JSON4Java Language", FILE_ENDING);
  }
  
  @Override
  protected JSON4JavaModelLoader provideModelLoader() {
    return new JSON4JavaModelLoader(this);
  }
}
