/*
 * Copyright (c) 2017, MontiCore. All rights reserved. http://www.se-rwth.de/
 */
package json4graph.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import json._ast.ASTJSONDocument;
import json4graph._parser.JSON4GraphParser;

public class JSON4GraphParserTest {
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json4graph/parser/od.json");
    JSON4GraphParser parser = new JSON4GraphParser();
    
    // test if model is parsed
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // test graph access
  }
}
