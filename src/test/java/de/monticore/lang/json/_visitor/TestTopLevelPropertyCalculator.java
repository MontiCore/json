/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._visitor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.lang.json.JSONMill;
import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;

public class TestTopLevelPropertyCalculator {
  
  @Test
  public void testTopLevelProperties() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/visitor/persons.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    JSONTraverser traverser = JSONMill.traverser();
    TopLevelPropertyCalculator tlpc = new TopLevelPropertyCalculator();
    traverser.add4JSON(tlpc);
    traverser.setJSONHandler(tlpc);
    jsonDoc.get().accept(traverser);
    List<String> checksum = Arrays.asList(new String[] { "Alice", "Bob" });
    
    List<String> propList = tlpc.getTopLevelPropertyNames();
    assertTrue(propList.equals(checksum));
  }
  
}
