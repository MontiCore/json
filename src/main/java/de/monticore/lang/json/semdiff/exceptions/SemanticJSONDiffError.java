/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff.exceptions;

public enum SemanticJSONDiffError {
  FAILED_TO_PARSE_MODEL("Failed to parse model '%s'"),
  UNKNOWN_JSON_VALUE_TYPE("Unknown json value type '%s'"),
  AMBIGUOUS_PROPERTY("Ambiguous property '%s' in other document"),
  INVALID_JSON_SEM_DIFF_TYPE("Invalid semantic difference type '%s'")
  ;
  
  private final String errorMessage;
  
  SemanticJSONDiffError(final String errorMessage) {
    this.errorMessage = errorMessage;
  }
  
  public String getErrorMessage() {
    return this.errorMessage;
  }
}
