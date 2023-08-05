/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.prettyprint;

/**
 * This class is used to configure the PlantUML pretty printer. Currently, it provides parameters to
 * configure some styling for rendering PlantUML JSON diagrams.
 */
public class PlantUMLConfig {
  protected Boolean useStyling = false;
  protected String nodeBackgroundColor = "#2b2b2b";
  protected String propKeyColor = "#8872b0";
  protected String propValueNullColor = "#6798c1";
  protected String propValueNumberColor = "#6798c1";
  protected String propValueStringColor = "#688153";
  protected String propValueBooleanColor = "#cb742f";
  
  /**
   * Default constructor applying no styling.
   */
  public PlantUMLConfig() {
  }
  
  /**
   * Constructor to override the default styling parameters.
   *
   * @param useStyling            whether to apply styling
   * @param nodeBackgroundColor   background color of JSON diagram nodes
   * @param propKeyColor          text color of property keys
   * @param propValueNullColor    text color of null values
   * @param propValueNumberColor  text color of number values
   * @param propValueStringColor  text color of string values
   * @param propValueBooleanColor text color of boolean values
   */
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
  
  /**
   * Constructor to apply default styling.
   *
   * @param useStyling whether to apply styling
   */
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
