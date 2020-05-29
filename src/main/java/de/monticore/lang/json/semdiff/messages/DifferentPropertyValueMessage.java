/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff.messages;

import de.monticore.lang.json._ast.ASTJSONNode;

public class DifferentPropertyValueMessage extends JSONSemDiffBaseMessage implements JSONSemDiffMessage {
  
  private final String property;
  
  private final String valueOfProperty;
  
  public DifferentPropertyValueMessage(ASTJSONNode ast, String property, String valueOfProperty) {
    super(ast);
    this.property = property;
    this.valueOfProperty = valueOfProperty;
  }
  
  @Override
  public String getSimpleErrorMessage() {
    return String.format("Object in document has property '%s', which is different on other document.", this.property);
  }
  
  @Override
  public String toString() {
    return this.getSimpleErrorMessage() +
        System.lineSeparator() +
        "Value of different property:" +
        System.lineSeparator() +
        this.valueOfProperty +
        System.lineSeparator() +
        String.format("Source position start: %s", getSourcePositionStart().toString()) +
        System.lineSeparator() +
        String.format("Source position end: %s", getSourcePositionEnd().toString());
  }
}
