/* (c) https://github.com/MontiCore/monticore */
package json4graph._visitor;

import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import json4graph._ast.ASTJSONObject;

public class JSON4GraphObjectVisitor implements JSON4GraphVisitor {
  
  private static final JSON4GraphObjectVisitor INSTANCE = new JSON4GraphObjectVisitor();
  
  /**
   * Private default constructor.
   */
  private JSON4GraphObjectVisitor() {
    
  }
  
  /**
   * Getter for the singleton instance.
   * 
   * @return The singleton instance of {@link JSON4GraphObjectVisitor}
   */
  public static JSON4GraphObjectVisitor getInstance() {
    return INSTANCE;
  }
  
  /**
   * Retrieves the actual value referenced via a String literal.
   * 
   * @param astStringLiteral The reference ID
   * @return The corresponding JSON object owning the reference ID
   */
  public ASTJSONObject getReferenceValue(ASTStringLiteral astStringLiteral) {
    
    return null;
  }
}
