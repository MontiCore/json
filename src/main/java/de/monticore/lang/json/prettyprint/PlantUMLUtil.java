package de.monticore.lang.json.prettyprint;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.se_rwth.commons.logging.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * Provides utilities to translate JSONs to PlantUML DSL code as well as rendered diagrams in SVG
 * format.
 */
public class PlantUMLUtil {
  public static final String PLANTUML_EMPTY = "@startuml\n@enduml";
  
  /**
   * Renders the JSON of the given AST as PlantUML SVG and saves it at the given target location.
   *
   * @param astJSON        AST of the JSON to render
   * @param outputPathSVG  path to SVG file where to save the rendered model
   * @param plantUMLConfig config containing options for pretty printing
   */
  public static Path writeJsonToPlantUmlSvg(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTJSONDocument> astJSON,
      Path outputPathSVG,
      PlantUMLConfig plantUMLConfig)
      throws IOException {
    
    final String plantUMLString = toPlantUmlModelString(astJSON, plantUMLConfig);
    final SourceStringReader reader = new SourceStringReader(plantUMLString);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    // Write the first image to "os"
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();
    
    // The XML is stored into svg
    final String svg = new String(os.toByteArray(), StandardCharsets.UTF_8);
    try (PrintWriter out = new PrintWriter(outputPathSVG.toString())) {
      out.println(svg);
    }
    
    return outputPathSVG;
  }
  
  /**
   * Loads a JSON object from a given file, renders a corresponding PlantUML SVG and saves it at the
   * given target location.
   *
   * @param pathJSON       path to JSON file from which to load the model
   * @param outputPathSVG  path to SVG file where to save the rendered model
   * @param plantUMLConfig config containing options for pretty printing
   */
  public static void writeJsonToPlantUmlSvg(
      String pathJSON, Path outputPathSVG, PlantUMLConfig plantUMLConfig) throws IOException {
    
    final String jsonString = new String(Files.readAllBytes(Paths.get(pathJSON)));
    
    final String plantUMLString = toPlantUmlModelString(jsonString, plantUMLConfig);
    final SourceStringReader reader = new SourceStringReader(plantUMLString);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    // Write the first image to "os"
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();
    
    // The XML is stored into svg
    final String svg = new String(os.toByteArray(), StandardCharsets.UTF_8);
    try (PrintWriter out = new PrintWriter(outputPathSVG.toString())) {
      out.println(svg);
    }
  }
  
  /**
   * Generates PlantUML DSL code for the JSON of the given AST and saves it at the given target
   * location.
   *
   * @param astJSON        AST of the JSON to generate DSL code for
   * @param outputPath     path to the file where to save the generated code
   * @param plantUMLConfig config containing options for pretty printing
   */
  public static Path writeJsonToPlantUmlModelFile(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTJSONDocument> astJSON,
      Path outputPath,
      PlantUMLConfig plantUMLConfig)
      throws IOException {
    final String plantUMLString = toPlantUmlModelString(astJSON, plantUMLConfig);
    
    try (PrintWriter out = new PrintWriter(outputPath.toString())) {
      out.println(plantUMLString);
    }
    
    return outputPath;
  }
  
  /**
   * Loads a JSON object from a given file, generates corresponding PlantUML DSL code and saves it
   * at the given target location.
   *
   * @param pathJSON       path to JSON file from which to load the model
   * @param outputPath     path to the file where to save the generated code
   * @param plantUMLConfig config containing options for pretty printing
   */
  public static void writeJsonToPlantUmlModelFile(
      String pathJSON, Path outputPath, PlantUMLConfig plantUMLConfig) throws IOException {
    final String jsonString = new String(Files.readAllBytes(Paths.get(pathJSON)));
    
    final String plantUMLString = toPlantUmlModelString(jsonString, plantUMLConfig);
    
    try (PrintWriter out = new PrintWriter(outputPath.toString())) {
      out.println(plantUMLString);
    }
  }
  
  /**
   * Generates PlantUML DSL code for the JSON of the given AST and returns it.
   *
   * @param astJSON        AST of the JSON to generate DSL code for
   * @param plantUMLConfig config containing options for pretty printing
   * @return generated PlantUML DSL code
   */
  public static String toPlantUmlModelString(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTJSONDocument> astJSON,
      PlantUMLConfig plantUMLConfig) {
    JSONToPlantUML prettyPrinter = new JSONToPlantUML(plantUMLConfig);
    
    if (astJSON.isPresent()) {
      return prettyPrinter.printJSONDocument(astJSON.get());
    }
    
    return PLANTUML_EMPTY;
  }
  
  /**
   * Generates PlantUML DSL code for the given JSON string and returns it.
   *
   * @param jsonString     JSON to translate to PlantUML DSL code
   * @param plantUMLConfig config containing options for pretty printing
   * @return generated PlantUML DSL code
   */
  public static String toPlantUmlModelString(String jsonString, PlantUMLConfig plantUMLConfig) {
    JSONParser parser = new JSONParser();
    
    try {
      Optional<ASTJSONDocument> astJSON = parser.parse_String(jsonString);
      return toPlantUmlModelString(astJSON, plantUMLConfig);
    }
    catch (IOException e) {
      Log.error("Cannot display JSON since it contains errors!");
    }
    
    return PLANTUML_EMPTY;
  }
  
  /**
   * Loads a JSON object from a given file, renders a corresponding PlantUML PNG and saves it at the
   * given target location.
   *
   * @param astJSON        AST of the JSON to generate DSL code for
   * @param outputPathPNG  path to PNG file where to save the rendered model
   * @param plantUMLConfig config containing options for pretty printing
   */
  public static void writeJsonToPlantUmlPng(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTJSONDocument> astJSON,
      Path outputPathPNG,
      PlantUMLConfig plantUMLConfig)
      throws IOException {
    
    final String plantUMLString = toPlantUmlModelString(astJSON, plantUMLConfig);
    final SourceStringReader reader = new SourceStringReader(plantUMLString);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    // Write the first image to "os"
    reader.outputImage(os, new FileFormatOption(FileFormat.PNG));
    os.close();
    
    final File png = new File(outputPathPNG.toString());
    try (FileOutputStream outputStream = new FileOutputStream(png)) {
      outputStream.write(os.toByteArray());
    }
  }
  
  /**
   * Loads a JSON object from a given file, renders a corresponding PlantUML PNG and saves it at the
   * given target location.
   *
   * @param pathJSON       path to JSON file from which to load the model
   * @param outputPathPNG  path to PNG file where to save the rendered model
   * @param plantUMLConfig config containing options for pretty printing
   */
  public static void writeJsonToPlantUmlPng(
      String pathJSON, Path outputPathPNG, PlantUMLConfig plantUMLConfig) throws IOException {
    
    final String jsonString = new String(Files.readAllBytes(Paths.get(pathJSON)));
    
    final String plantUMLString = toPlantUmlModelString(jsonString, plantUMLConfig);
    final SourceStringReader reader = new SourceStringReader(plantUMLString);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    // Write the first image to "os"
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();
    
    final File png = new File(outputPathPNG.toString());
    try (FileOutputStream outputStream = new FileOutputStream(png)) {
      outputStream.write(os.toByteArray());
    }
  }
  
}

