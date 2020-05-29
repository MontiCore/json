/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff.messages;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.SourcePosition;

public interface JSONSemDiffMessage {
  
  ASTNode getASTNode();
  
  String getSimpleErrorMessage();
  
  default SourcePosition getSourcePositionStart() {
    return getASTNode().get_SourcePositionStart();
  }
  
  default SourcePosition getSourcePositionEnd() {
    return getASTNode().get_SourcePositionEnd();
  }
  
}
