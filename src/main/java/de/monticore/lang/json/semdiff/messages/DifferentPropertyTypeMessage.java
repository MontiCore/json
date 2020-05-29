/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff.messages;

import de.monticore.lang.json._ast.ASTJSONNode;

public class DifferentPropertyTypeMessage extends JSONSemDiffBaseMessage implements JSONSemDiffMessage {
  
  private final String property;
  
  private final String typeOfProperty;
  
  public DifferentPropertyTypeMessage(ASTJSONNode ast, String property, String typeOfProperty) {
    super(ast);
    this.property = property;
    this.typeOfProperty = typeOfProperty;
  }
  
  @Override
  public String getSimpleErrorMessage() {
    return String.format("Object in document has property '%s', which is another type on other document.", this.property);
  }
  
  @Override
  public String toString() {
    return this.getSimpleErrorMessage() +
        System.lineSeparator() +
        "Type of different property:" +
        System.lineSeparator() +
        this.typeOfProperty +
        System.lineSeparator() +
        String.format("Source position start: %s", getSourcePositionStart().toString()) +
        System.lineSeparator() +
        String.format("Source position end: %s", getSourcePositionEnd().toString());
  }
}
