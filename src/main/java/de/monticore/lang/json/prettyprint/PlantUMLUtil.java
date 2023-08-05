package de.monticore.lang.json.prettyprint;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.se_rwth.commons.logging.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLUtil {
    public static final String PLANTUML_EMPTY = "@startuml\n@enduml";

    /**
     * this needs GraphViz/JDOT installed on your PC
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
     * this needs GraphViz/JDOT installed on your PC
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

    public static void writeJsonToPlantUmlModelFile(
            String pathJSON, Path outputPath, PlantUMLConfig plantUMLConfig) throws IOException {
        final String jsonString = new String(Files.readAllBytes(Paths.get(pathJSON)));

        final String plantUMLString = toPlantUmlModelString(jsonString, plantUMLConfig);

        try (PrintWriter out = new PrintWriter(outputPath.toString())) {
            out.println(plantUMLString);
        }
    }

    public static String toPlantUmlModelString(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTJSONDocument> astJSON,
            PlantUMLConfig config) {
        JSONToPlantUML prettyPrinter = new JSONToPlantUML(config);

        if (astJSON.isPresent()) {
            return prettyPrinter.printJSONDocument(astJSON.get());
        }

        return PLANTUML_EMPTY;
    }

    public static String toPlantUmlModelString(String jsonString, PlantUMLConfig config) {
        JSONParser parser = new JSONParser();

        try {
            Optional<ASTJSONDocument> astJSON = parser.parse_String(jsonString);
            return toPlantUmlModelString(astJSON, config);
        } catch (IOException e) {
            Log.error("Cannot display JSON since it contains errors!");
        }

        return PLANTUML_EMPTY;
    }
}

