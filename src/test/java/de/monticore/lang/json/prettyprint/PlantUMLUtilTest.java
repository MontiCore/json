package de.monticore.lang.json.prettyprint;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PlantUMLUtilTest {
  
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  
  @Test
  public void testWriteCdToPlantUmlModelFile() {
    Path pathJSON = Paths.get("src/test/resources/json/prettyprint/bookstore.json");
    Path outputPath = Paths.get(folder.getRoot().getAbsolutePath().toString(), "bookstore.puml");
    PlantUMLConfig config = new PlantUMLConfig();
    
    try {
      PlantUMLUtil.writeJsonToPlantUmlModelFile(pathJSON.toFile().getAbsolutePath(), outputPath,
          config);
    }
    catch (IOException ex) {
      fail(ex.getMessage());
    }
    
    assertTrue(outputPath.toFile().exists());
    
    try {
      File file = new File(outputPath.toUri());
      // Read and strip empty lines and line ends
      String puml = FileUtils.readFileToString(file, "UTF-8").replaceAll("(?m)^[ \t]*\r?\n", "");
      puml = puml.replaceAll("\n", "");
      puml = puml.replaceAll("\r", "");
      assertNotNull(puml);
      assertNotEquals("", puml);
      assertTrue(puml.startsWith("@startjson"));
      assertTrue(puml.endsWith("@endjson"));
      // No double printing
      assertEquals(1, StringUtils.countMatches(puml, "@startjson"));
      assertEquals(1, StringUtils.countMatches(puml, "@endjson"));
    }
    catch (IOException ex) {
      fail(ex.getMessage());
    }
  }
  
  @Test
  public void testWriteCdToPlantUmlSvg() {
    Path pathJSON = Paths.get("src/test/resources/json/prettyprint/bookstore.json");
    Path outputPath = Paths.get(folder.getRoot().getAbsolutePath().toString(), "bookstore.svg");
    PlantUMLConfig config = new PlantUMLConfig();
    
    try {
      PlantUMLUtil.writeJsonToPlantUmlSvg(pathJSON.toFile().getAbsolutePath(), outputPath, config);
    }
    catch (IOException ex) {
      fail(ex.getMessage());
    }
    
    assertTrue(outputPath.toFile().exists());
    
    try {
      File file = new File(outputPath.toUri());
      // Read and strip empty lines
      String puml = FileUtils.readFileToString(file, "UTF-8").replaceAll("(?m)^[ \t]*\r?\n", "");
      assertNotNull(puml);
      assertNotEquals("", puml);
      assertEquals(0, StringUtils.countMatches(puml, "Syntax Error"));
      assertEquals(0, StringUtils.countMatches(puml, "Cannot find Graphviz"));
    }
    catch (IOException ex) {
      fail(ex.getMessage());
    }
  }
}
