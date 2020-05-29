/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff.exceptions;

public class SemanticJSONDiffException extends Exception {
  
  /**
   * Generated serial ID.
   */
  private static final long serialVersionUID = 7751512136853989170L;
  private final SemanticJSONDiffError error;
  
  public SemanticJSONDiffException(final SemanticJSONDiffError error, String cause) {
    this(error, cause, null);
  }
  
  public SemanticJSONDiffException(final SemanticJSONDiffError error, String cause, Exception e) {
    super(String.format(error.getErrorMessage(), cause), e);
    this.error = error;
  }
  
  public SemanticJSONDiffError getError() {
    return error;
  }
}
