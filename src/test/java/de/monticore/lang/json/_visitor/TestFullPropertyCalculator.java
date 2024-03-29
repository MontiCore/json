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

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import de.monticore.lang.json.JSONMill;
import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;

public class TestFullPropertyCalculator {

  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAllProperties() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/visitor/persons.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    JSONTraverser traverser = JSONMill.traverser();
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    traverser.add4JSON(fpc);
    jsonDoc.get().accept(traverser);
    List<String> checksum = Arrays.asList(new String[] { 
        "Alice", "name", "Bob", "name", "friends", 
        "Alice", "Bob", "Alice", "Charlie" });
    
    List<String> propList = fpc.getAllPropertyNames();
    assertTrue(propList.equals(checksum));
  }
  
  @Test
  public void testCountAllProperties() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/visitor/persons.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    JSONTraverser traverser = JSONMill.traverser();
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    traverser.add4JSON(fpc);
    jsonDoc.get().accept(traverser);
    Map<String, Integer> checksum = new HashMap<String, Integer>();
    checksum.put("Alice", 3);
    checksum.put("name", 2);
    checksum.put("Bob", 2);
    checksum.put("friends", 1);
    checksum.put("Charlie", 1);
    
    Map<String, Integer> propMap = fpc.getAllPropertyNamesCounted();
    propMap.equals(checksum);
  }
  
}
