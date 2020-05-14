package de.monticore.lang.json.semdiff.messages;

import de.monticore.ast.ASTNode;

public class MissingProperty implements JSONSemDiffMessage {

  private ASTNode astNode;
  private String missingProperty;
  private String valueOfMissingProperty;

  public MissingProperty(ASTNode astNode, String missingProperty, String valueOfMissingProperty) {
    this.astNode = astNode;
    this.missingProperty = missingProperty;
    this.valueOfMissingProperty = valueOfMissingProperty;
  }

  @Override
  public ASTNode getASTNode() {
    return this.astNode;
  }

  @Override
  public String getSimpleErrorMessage() {
    return String.format("Object in document has property '%s' missing in other document.", this.missingProperty);
  }

  @Override
  public String toString() {
    String res = "";
    res += this.getSimpleErrorMessage();
    res += System.lineSeparator();
    res += "Value of missing property:";
    res += System.lineSeparator();
    res += this.valueOfMissingProperty;
    res += System.lineSeparator();
    res += String.format("Source position start: %s", getSourcePositionStart().toString());
    res += String.format("Source position end: %s", getSourcePositionEnd().toString());
    return res;
  }
}
