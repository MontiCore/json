/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.prettyprint;

/**
 * This class is used to configure the PlantUML pretty printer.
 */
public class PlantUMLConfig {
  protected Boolean useStyling = false;
  protected String nodeBackgroundColor = "#2b2b2b";
  protected String propKeyColor = "#8872b0";
  protected String propValueNullColor = "#6798c1";
  protected String propValueNumberColor = "#6798c1";
  protected String propValueStringColor = "#688153";
  protected String propValueBooleanColor = "#cb742f";
  
  public PlantUMLConfig() {
  }
  
  public PlantUMLConfig(
      Boolean useStyling,
      String nodeBackgroundColor,
      String propKeyColor,
      String propValueNullColor,
      String propValueNumberColor,
      String propValueStringColor,
      String propValueBooleanColor) {
    this.useStyling = useStyling;
    this.nodeBackgroundColor = nodeBackgroundColor;
    this.propKeyColor = propKeyColor;
    this.propValueNullColor = propValueNullColor;
    this.propValueNumberColor = propValueNumberColor;
    this.propValueStringColor = propValueStringColor;
    this.propValueBooleanColor = propValueBooleanColor;
  }
  
  public PlantUMLConfig(Boolean useStyling) {
    this.useStyling = useStyling;
  }
  
  @Override
  public String toString() {
    return "PlantUMLConfig{"
        + "useStyling="
        + this.useStyling
        + ", nodeBackgroundColor="
        + this.nodeBackgroundColor
        + ", propKeyColor="
        + this.propKeyColor
        + ", propValueNullColor="
        + this.propValueNullColor
        + ", propValueNumberColor="
        + this.propValueNumberColor
        + ", propValueStringColor="
        + this.propValueStringColor
        + ", propValueBooleanColor="
        + this.propValueBooleanColor
        + '}';
  }
}
