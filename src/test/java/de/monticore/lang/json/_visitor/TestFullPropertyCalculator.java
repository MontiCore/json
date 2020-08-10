/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._visitor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;

public class TestFullPropertyCalculator {
  
  @Test
  public void testAllProperties() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/visitor/persons.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    List<String> checksum = Arrays.asList(new String[] { 
        "Alice", "name", "Bob", "name", "friends", 
        "Alice", "Bob", "Alice", "Charlie" });
    
    List<String> propList = fpc.getAllPropertyNames(jsonDoc.get());
    assertTrue(propList.equals(checksum));
  }
  
  @Test
  public void testCountAllProperties() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/visitor/persons.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    Map<String, Integer> checksum = new HashMap<String, Integer>();
    checksum.put("Alice", 3);
    checksum.put("name", 2);
    checksum.put("Bob", 2);
    checksum.put("friends", 1);
    checksum.put("Charlie", 1);
    
    Map<String, Integer> propMap = fpc.getAllPropertyNamesCounted(jsonDoc.get());
    propMap.equals(checksum);
  }
  
}
