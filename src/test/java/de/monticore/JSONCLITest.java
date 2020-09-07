/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;

public class JSONCLITest {
  
  private static final String INPUT = "src/test/resources/json/parser/bookstore.json";
  private static final String PRINT = "target/generated-test-sources/bookstore.txt";
  private static final String D_LOG = "dev-log/";
  
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
  public void testDevLog() throws IOException {
    // clean up dev-log directory
    File devLog = new File(D_LOG);
    if (devLog.exists() && devLog.isDirectory()) {
      FileUtils.deleteDirectory(devLog);
    }
    assertFalse(devLog.exists());
    
    String[] args = { "-i", INPUT, "-d" };
    JSONCLI.main(args);
    
    assertTrue(devLog.exists());
    File[] files = devLog.listFiles();
    assertEquals(1, files.length);
    File log = files[0];
    assertEquals(0, log.length());
  }
  
}
