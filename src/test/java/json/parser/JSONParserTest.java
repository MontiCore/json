/*
 * Copyright (c) 2017, MontiCore. All rights reserved. http://www.se-rwth.de/
 */
package json.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import json._ast.ASTJSONDocument;
import json._parser.JSONParser;

public class JSONParserTest {
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/bookstore.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
  
  @Test
  public void testGenerated() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/generated.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
}
