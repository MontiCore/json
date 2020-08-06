/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.lang.json._visitor.FullPropertyCalculator;
import de.monticore.lang.json._visitor.TopLevelPropertyCalculator;
import de.monticore.lang.json.prettyprint.JSONPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Command line interface for the JSON language and corresponding tooling.
 */
public class JSONTool {
  
  // TODO: diese drei Attribute werden besser als lokale Variable gespeichert und als Argumente zwischen Funktionen übergeben
  // (teilweise ist das schon erledigt)
  private Optional<ASTJSONDocument> jsonDoc;   // muss dann kein Optional mehr sein ...
  private FullPropertyCalculator fpc;
  private TopLevelPropertyCalculator tlpc;
  
  /**
   * Main method that is called from command line and runs the JSON tool.
   * 
   * @param args The input parameters for configuring the JSON tool.
   */
  public static void main(String[] args) {
    JSONTool cli = new JSONTool();
    // Initialize Logging with standard logging
    Log.init();
    cli.handleArgs(args);
  }
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   * 
   * @param args The input parameters for configuring the JSON tool.
   */
  public void handleArgs(String[] args) {
    
    try {
      // Create CLI parser and parse input options from command line
      CommandLineParser cliparser = new DefaultParser();
      JSONCLIConfiguration config = new JSONCLIConfiguration();
      CommandLine cmd = cliparser.parse(config.getOptions(), args);
      
      // help
      if (cmd.hasOption(JSONCLIConfiguration.HELP)
              || !cmd.hasOption(JSONCLIConfiguration.INPUT)) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("JSONTool", config.getOptions());
        // print help, but continue ...
      }
      
      // parse input file
      // (only returns if successfull)
      ASTJSONDocument jsonDoc = parseFile(cmd.getOptionValue(JSONCLIConfiguration.INPUT));
      
      // -option pretty print
      if (cmd.hasOption(JSONCLIConfiguration.PRINT)) {
        String path = cmd.getOptionValue(JSONCLIConfiguration.PRINT, StringUtils.EMPTY);
        prettyPrint(jsonDoc,path);
      }
      
      // -option reports
      if (cmd.hasOption(JSONCLIConfiguration.REPORT)) {
        String path = cmd.getOptionValue(JSONCLIConfiguration.REPORT, StringUtils.EMPTY);
        report(jsonDoc,path);
      }
      
    } catch (ParseException e) {
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }
    
  }
  
  /**
   * Parses the contents of a given file as JSON.
   * 
   * @param path The path to the JSON-file as String
   */
  public ASTJSONDocument parseFile(String path) {
    Path model = Paths.get(path);
    JSONParser parser = new JSONParser();
    Optional<ASTJSONDocument> jsonDoc;
    try {
      jsonDoc = parser.parse(model.toString());
    }
    catch (IOException e) {
      Log.error("0xA7102 File " + path + " not found.");
      jsonDoc = null; // will not be reached
    }
    return jsonDoc.get();
  }
  
  /**
   * Prints the contents of the JSON-AST to stdout or a specified file.
   * 
   * @param file The target file name for printing the JSON artifact. If empty,
   *          the content is printed to stdout instead
   */
  public void prettyPrint(ASTJSONDocument jsonDoc, String file) {
    // pretty print AST
    JSONPrettyPrinter pp = new JSONPrettyPrinter();
    String json = pp.printJSONDocument(jsonDoc);
    print(json, file);
  }
  
  /**
   * Creates reports for the JSON-AST to stdout or a specified file.
   * 
   * @param path The target path of the directory for the report artifacts. If
   *          empty, the contents are printed to stdout instead
   */
  public void report(ASTJSONDocument jsonDoc, String path) {
    // calculate and print reports
    String aProps = allPropertyNames(jsonDoc);
    print(aProps, path + REPORT_ALL_PROPS);

    String cProps = countedPropertyNames(jsonDoc);
    print(cProps, path + REPORT_COUNTED_PROPS);

    String tlProps = topLevelPropertyNames(jsonDoc);
    print(tlProps, path + REPORT_TOPLEVEL_PROPS);
  }

  // Names of the reports:
  public static final String REPORT_ALL_PROPS = "/allProperties.txt";
  public static final String REPORT_COUNTED_PROPS = "/countedProperties.txt";
  public static final String REPORT_TOPLEVEL_PROPS = "/topLevelProperties.txt";
  
  /**
   * Calculates all property names in the JSON-AST as ordered list.
   * 
   * @return A String containing all property names
   */
  private String allPropertyNames(ASTJSONDocument jsonDoc) {
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    List<String> properties = fpc.getAllPropertyNames(jsonDoc);
    String content = "";
    for (int i = 0; i < properties.size(); i++) {
      content += properties.get(i);
      if (i < properties.size() - 1) {
        content += ", ";
      }
    }
    return content;
  }
  
  /**
   * Calculates all property names in the JSON-AST as a set with additional
   * number of their respective occurrence.
   * 
   * @return A String containing all property names with the number of
   *         occurrence
   */
  public String countedPropertyNames(ASTJSONDocument jsonDoc) {
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    Map<String, Integer> properties = fpc.getAllPropertyNamesCounted(jsonDoc);
    Set<Entry<String, Integer>> entries = properties.entrySet();
    Iterator<Entry<String, Integer>> it = entries.iterator();
    String content = "";
    while (it.hasNext()) {
      Entry<String, Integer> entry = it.next();
      content += entry.getKey();
      content += " (" + entry.getValue() + ")";
      if (it.hasNext()) {
        content += ", ";
      }
    }
    return content;
  }
  
  /**
   * Calculates all top-level property names in the JSON-AST as ordered list.
   * Thus, only traverses the AST shallowly.
   * 
   * @return A String containing all top-level property names
   */
  public String topLevelPropertyNames(ASTJSONDocument jsonDoc) {
    TopLevelPropertyCalculator tlpc = new TopLevelPropertyCalculator();
    List<String> properties = tlpc.getTopLevelPropertyNames(jsonDoc);
    String content = "";
    for (int i = 0; i < properties.size(); i++) {
      content += properties.get(i);
      if (i < properties.size() - 1) {
        content += ", ";
      }
    }
    return content;
  }
  
  /**
   * Prints the given content to a target file (if specified) or to stdout (if
   * the file is Optional.empty()).
   * 
   * @param content The String to be printed
   * @param path The target path to the file for printing the content. If empty,
   *          the content is printed to stdout instead
   */
  public void print(String content, String path) {
    // print to stdout or file
    if (path.isEmpty()) {
      System.out.println(content);
    } else {
      File f = new File(path);
      // create directories
      f.getAbsoluteFile().getParentFile().mkdirs();
      
      FileWriter writer;
      try {
        writer = new FileWriter(f);
        writer.write(content);
        writer.close();
      } catch (IOException e) {
        Log.error("0xA7105 Could not write to file " + f.getAbsolutePath());
      }
    }
  }
}
