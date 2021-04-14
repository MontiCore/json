/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._visitor;

import java.util.ArrayList;
import java.util.List;

import de.monticore.lang.json._ast.ASTJSONNode;
import de.monticore.lang.json._ast.ASTJSONProperty;

/**
 * Traverses the JSON-AST shallowly and finds all top-level JSONProperties.
 * Stores these as list containing the corresponding property names.
 */
public class TopLevelPropertyCalculator implements JSONVisitor2, JSONHandler {
  private List<String> properties = new ArrayList<String>();
  protected JSONTraverser traverser;
  
  @Override
  public JSONTraverser getTraverser () {
    return traverser;
  }
  
  @Override
  public void setTraverser (JSONTraverser traverser) {
    this.traverser = traverser;
  }
  
  @Override
  public void visit(ASTJSONProperty node) {
    String key = node.getKey();
    properties.add(key);
  }
  
  @Override
  public void traverse(de.monticore.lang.json._ast.ASTJSONProperty node) {
    // Do nothing, as the we only traverse the AST shallowly. Thus, when finding
    // a JSONProperty, we are not looking deeper, but still in the breadth.
  }
  
  /**
   * Calculates all top-level JSONProperty names and returns these as a list.
   * 
   * @return The list of property names in the JSON AST
   */
  public List<String> getTopLevelPropertyNames() {
    return properties;
  }
  
}
