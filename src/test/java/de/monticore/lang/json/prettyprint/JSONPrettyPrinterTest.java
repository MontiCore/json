/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.lang.json._prettyprint.JSONFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;

public class JSONPrettyPrinterTest {

  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/prettyprint/bookstore.json");
    JSONParser parser = new JSONParser();
    
    // parse model
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // pretty print AST
    JSONFullPrettyPrinter pp = new JSONFullPrettyPrinter(new IndentPrinter());
    String printedModel = pp.prettyprint(jsonDoc.get());
    
    // parse printed model
    Optional<ASTJSONDocument> printedJsonDoc = parser.parse_StringJSONDocument(printedModel);
    assertFalse(parser.hasErrors());
    assertTrue(printedJsonDoc.isPresent());
    
    // original model and printed model should be the same
    assertTrue(jsonDoc.get().deepEquals(printedJsonDoc.get(), true));
  }
}
