/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.lang.json.JSONMill;
import de.monticore.lang.json._ast.*;
import de.monticore.lang.json._ast.ASTSignedBasicDoubleLiteral;
import de.monticore.lang.json._visitor.JSONHandler;
import de.monticore.lang.json._visitor.JSONTraverser;
import de.monticore.lang.json._visitor.JSONVisitor2;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

public class JSONPrettyPrinter extends IndentPrinter implements JSONVisitor2, MCCommonLiteralsVisitor2, JSONHandler {
  private JSONTraverser traverser;
  
  /** Default Constructor. */
  public JSONPrettyPrinter() {
    this.traverser= JSONMill.traverser();
    this.traverser.add4JSON(this);
    this.traverser.add4MCCommonLiterals(this);
    this.traverser.setJSONHandler(this);
  }

  public JSONTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(JSONTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * Serializes and pretty-prints the JSON-AST.
   * 
   * @param jsonDocument The root node of the input AST
   * @return The pretty-printed JSON-AST as String
   */
  public String printJSONDocument(ASTJSONDocument jsonDocument) {
    clearBuffer();
    getTraverser().handle(jsonDocument);
    return getContent();
  }

  /**
   * Serializes and pretty-prints a single number in a JSON artifact.
   * 
   * @param jsonNumber The input AST node that contains the number
   * @return The pretty-printed JSON number as String
   */
  public String printJSONNumber(ASTJSONNumber jsonNumber) {
    clearBuffer();
    getTraverser().handle(jsonNumber);
    return getContent();
  }
  
  @Override
  public void handle(ASTJSONArray node) {
    println("[");
    indent();
    for (int i = 0; i < node.getJSONValueList().size(); i++) {
      node.getJSONValue(i).accept(getTraverser());
      if(i+1<node.getJSONValueList().size()){
        println(", ");
      }else{
        println();
      }
    }
    unindent();
    print("]");
  }
  
  @Override
  public void visit(ASTJSONBoolean node) {
    print(node.getBooleanLiteral().getValue());
  }
  
  @Override
  public void visit(ASTJSONNull node) {
    print("null");
  }
  
  @Override
  public void handle(ASTJSONObject node) {
    println("{");
    indent();
    for (int i = 0; i < node.getPropList().size(); i++) {
      node.getProp(i).accept(getTraverser());
      if(i+1<node.getPropList().size()){
        println(", ");
      }else{
        println();
      }
    }
    unindent();
    print("}");
  }
  
  @Override
  public void visit(ASTJSONProperty node) {
    print("\"" + node.getKey() + "\": ");
  }
  
  @Override
  public void visit(ASTSignedBasicDoubleLiteral node) {
    print(node.getSource());
  }
  
  @Override
  public void visit(ASTSignedBasicFloatLiteral node) {
    print(node.getSource());
  }
  
  @Override
  public void visit(ASTSignedBasicLongLiteral node) {
    print(node.getSource());
  }
  
  @Override
  public void visit(ASTSignedNatLiteral node) {
    print(node.getSource());
  }
  
  @Override
  public void visit(ASTStringLiteral node) {
    print("\"" + node.getSource() + "\"");
  }

  @Override
  public void visit(ISymbol node) {
    //Nothing
  }

  @Override
  public void endVisit(ISymbol node) {
    //Nothing
  }

  @Override
  public void visit(IScope node) {
    //Nothing
  }

  @Override
  public void endVisit(IScope node) {
    //Nothing
  }

  @Override
  public void visit(ASTNode node) {
    //Nothing
  }

  @Override
  public void endVisit(ASTNode node) {
    //Nothing
  }
}
