/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff.messages;

import de.monticore.ast.ASTNode;
import de.monticore.lang.json._ast.ASTJSONNode;

abstract class JSONSemDiffBaseMessage implements JSONSemDiffMessage {
  
  private final ASTJSONNode ast;
  
  JSONSemDiffBaseMessage(final ASTJSONNode ast) {
    this.ast = ast;
  }
  
  @Override
  public ASTNode getASTNode() {
    return this.ast;
  }
}
