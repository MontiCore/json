/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;

public class JSONCLITest {
  
  private static final String INPUT = "src/test/resources/json/parser/bookstore.json";
  private static final String PRINT = "target/generated-test-sources/bookstore.txt";
  
  @Test
  public void testParseAndPrint() throws IOException {
    String[] args = { "-i", INPUT, "-pp", PRINT };
    JSONCLI.main(args);
    
    // check if printed JSON is valid
    JSONParser parser = new JSONParser();
    Path model = Paths.get(PRINT);
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }

  @Test
  public void testSyntaxObjects() throws IOException {
    String[] args = { "-i", INPUT, "-so" };
    JSONCLI.main(args);
  }
  
}
