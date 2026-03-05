package de.monticore.lang.json.prettyprint;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class PlantUMLUtilTest {
  
  @Test
  public void testWriteCdToPlantUmlModelFile(@TempDir Path tempDir) {
    Path pathJSON = Paths.get("src/test/resources/json/prettyprint/bookstore.json");
    Path outputPath = tempDir.resolve("bookstore.puml");
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
  public void testWriteCdToPlantUmlSvg(@TempDir Path tempDir) {
    Path pathJSON = Paths.get("src/test/resources/json/prettyprint/bookstore.json");
    Path outputPath = tempDir.resolve("bookstore.svg");
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
  
  @Test
  public void testWriteCdToPlantUmlPng(@TempDir Path tempDir) {
    Path pathJSON = Paths.get("src/test/resources/json/prettyprint/bookstore.json");
    Path outputPath = tempDir.resolve("bookstore.png");
    PlantUMLConfig config = new PlantUMLConfig();
    
    try {
      PlantUMLUtil.writeJsonToPlantUmlPng(pathJSON.toFile().getAbsolutePath(), outputPath, config);
    }
    catch (IOException ex) {
      fail(ex.getMessage());
    }
    
    assertTrue(outputPath.toFile().exists());
    
    // No more proper ways to check the contents of the PNG.
  }
}
