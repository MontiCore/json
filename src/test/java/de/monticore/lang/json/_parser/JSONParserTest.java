/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._parser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;

import de.monticore.lang.json._ast.ASTJSONDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONParserTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/bookstore.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
  
  @Test
  public void testGeneratedArray() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/generatedArray.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
  
  @Test
  public void testGeneratedObjects() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/generatedObjects.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
  
  @Test
  public void testNumbers() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/Numbers.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
}
