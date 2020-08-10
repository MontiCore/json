/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.monticore.lang.json._ast.ASTJSONNode;
import de.monticore.lang.json._ast.ASTJSONProperty;

/**
 * Traverses the JSON-AST and finds all JSONProperties. Stores these as list
 * containing the property names and as map containing the names as well as
 * their number of occurrence.
 */
public class FullPropertyCalculator implements JSONVisitor {
  private List<String> properties = new ArrayList<String>();
  private Map<String, Integer> propertyMap = new HashMap<String, Integer>();
  
  @Override
  public void visit(ASTJSONProperty node) {
    String key = node.getKey();
    properties.add(key);
    
    // Add to map or increment counter
    if (propertyMap.containsKey(key)) {
      propertyMap.replace(key, propertyMap.get(key) + 1);
    } else {
      propertyMap.put(key, 1);
    }
  }
  
  /**
   * Calculates all JSONProperty names and returns these as a list.
   * 
   * @param node An arbitrary node of the JSON-AST
   * @return The list of property names in the JSON-AST
   */
  public List<String> getAllPropertyNames(ASTJSONNode node) {
    if (properties.isEmpty()) {
      node.accept(getRealThis());
    }
    return properties;
  }
  
  /**
   * Calculates all JSONProperty names and returns these as a map with their
   * number of occurrence.
   * 
   * @param node An arbitrary node of the JSON-AST
   * @return The list of property names in the JSON-AST
   */
  public Map<String, Integer> getAllPropertyNamesCounted(ASTJSONNode node) {
    if (propertyMap.isEmpty()) {
      node.accept(getRealThis());
    }
    return propertyMap;
  }
  
}
