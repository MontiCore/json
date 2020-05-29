/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff.messages;

import de.monticore.lang.json._ast.ASTJSONNode;

public class MissingPropertyMessage extends JSONSemDiffBaseMessage implements JSONSemDiffMessage {
  
  private final String missingProperty;
  
  // private final String valueOfMissingProperty;
  
  public MissingPropertyMessage(ASTJSONNode ast, String missingProperty) {
    super(ast);
    this.missingProperty = missingProperty;
    // this.valueOfMissingProperty = valueOfMissingProperty;
  }
  
  @Override
  public String getSimpleErrorMessage() {
    return String.format("Object in document has property '%s' missing in other document.", this.missingProperty);
  }
  
  @Override
  public String toString() {
    return this.getSimpleErrorMessage() +
        System.lineSeparator() +
        //"Value of missing property:" +
        //System.lineSeparator() +
        //this.valueOfMissingProperty +
        //System.lineSeparator() +
        String.format("Source position start: %s", getSourcePositionStart().toString()) +
        System.lineSeparator() +
        String.format("Source position end: %s", getSourcePositionEnd().toString());
  }
}
