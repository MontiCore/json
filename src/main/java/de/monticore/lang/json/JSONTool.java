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

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.lang.json._visitor.FullPropertyCalculator;
import de.monticore.lang.json._visitor.TopLevelPropertyCalculator;
import de.monticore.lang.json.prettyprint.JSONPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Command line interface for the JSON language and corresponding tooling.
 * Defines, handles, and executes the corresponding command line options and
 * arguments, such as --help
 */
public class JSONTool {
  
  /*=================================================================*/
  /* Part 1: Handling the arguments and options
  /*=================================================================*/
  
  /**
   * Main method that is called from command line and runs the JSON tool.
   * 
   * @param args The input parameters for configuring the JSON tool.
   */
  public static void main(String[] args) {
    JSONTool cli = new JSONTool();
    // initialize logging with standard logging
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
  
    Options options = initOptions();
  
    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliparser = new DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);
      
      // help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        // do not continue, when help is printed
        return;
      }
  
      // if -i input is missing: also print help and stop
      if (!cmd.hasOption("i")) {
        printHelp(options);
        // do not continue, when help is printed
        return;
      }
  
      // parse input file, which is now available
      // (only returns if successful)
      ASTJSONDocument jsonDoc = parseFile(cmd.getOptionValue("i"));
      
      // -option pretty print
      if (cmd.hasOption("pp")) {
        String path = cmd.getOptionValue("pp", StringUtils.EMPTY);
        prettyPrint(jsonDoc, path);
      }
      
      // -option reports
      if (cmd.hasOption("r")) {
        String path = cmd.getOptionValue("r", StringUtils.EMPTY);
        report(jsonDoc, path);
      }
      
    } catch (ParseException e) {
      // ann unexpected error from the apache CLI parser:
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }
    
  }
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param options The input parameters and options.
   */
  public void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("JSONTool", options);
  }
  
  /*=================================================================*/
  /* Part 2: Executing arguments
  /*=================================================================*/
  
  /**
   * Parses the contents of a given file as JSON.
   * 
   * @param path The path to the JSON-file as String
   */
  public ASTJSONDocument parseFile(String path) {
    Optional<ASTJSONDocument> jsonDoc = Optional.empty();
    try {
      Path model = Paths.get(path);
      JSONParser parser = new JSONParser();
      jsonDoc = parser.parse(model.toString());
    }
    catch (IOException | NullPointerException e) {
      Log.error("0xA7102 Input file " + path + " not found.");
    }
    return jsonDoc.get();
  }
  
  /**
   * Prints the contents of the JSON-AST to stdout or a specified file.
   * 
   * @param jsonDoc The JSON-AST to be pretty printed
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
   * @param jsonDoc The JSON-AST for which the reports are created
   * @param path The target path of the directory for the report artifacts. If
   *          empty, the contents are printed to stdout instead
   */
  public void report(ASTJSONDocument jsonDoc, String path) {
    // calculate and print reports
    String aProps = allPropertyNames(jsonDoc);
    print(aProps, path + "/" + REPORT_ALL_PROPS);

    String cProps = countedPropertyNames(jsonDoc);
    print(cProps, path + "/" + REPORT_COUNTED_PROPS);

    String tlProps = topLevelPropertyNames(jsonDoc);
    print(tlProps, path + "/" + REPORT_TOPLEVEL_PROPS);
  }
  
  // names of the reports:
  public static final String REPORT_ALL_PROPS = "allProperties.txt";
  public static final String REPORT_COUNTED_PROPS = "countedProperties.txt";
  public static final String REPORT_TOPLEVEL_PROPS = "topLevelProperties.txt";
  
  /*=================================================================*/

  /**
   * Calculates all property names in the JSON-AST as ordered list.
   * 
   * @param jsonDoc The JSON-AST to traverse
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
   * @param jsonDoc The JSON-AST to traverse
   * @return A String containing all property names with the number of
   *         occurrence
   */
  public String countedPropertyNames(ASTJSONDocument jsonDoc) {
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    Map<String, Integer> properties = fpc.getAllPropertyNamesCounted(jsonDoc);
    Iterator<Entry<String, Integer>> it = properties.entrySet().iterator();
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
   * @param jsonDoc The JSON-AST to traverse
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
      // TODO: Error, when directory cannot be created?
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
  
  /*=================================================================*/
  /* Part 3: Defining the options incl. help-texts
  /*=================================================================*/

  /**
   * Initializes the available CLI options for the JSON tool.
   * 
   * @return The CLI options with arguments.
   */
  protected Options initOptions() {
    Options options = new Options();
    
    // help dialog
    options.addOption(Option.builder("h")
        .longOpt("help")
        .desc("Prints this help dialog")
        .build());
    
    // parse input file
    options.addOption(Option.builder("i")
        .longOpt("input")
        .argName("file")
        .hasArg()
        .desc("Reads the source file (mandatory) and parses the contents as JSON")
        .build());
    
    // pretty print JSON
    options.addOption(Option.builder("pp")
        .longOpt("prettyprint")
        .argName("file")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Prints the JSON-AST to stdout or the specified file (optional)")
        .build());
    
    // pretty print JSON
    options.addOption(Option.builder("r")
        .longOpt("report")
        .argName("dir")
        .hasArg(true)
        .desc("Prints reports of the JSON artifact to the specified directory (optional). Available reports:" 
            + "  " + REPORT_ALL_PROPS + "      a list of all properties, " 
            + "  " + REPORT_COUNTED_PROPS + "  a set of all properties with the number of occurrences, " 
            + "  " + REPORT_TOPLEVEL_PROPS + " a list of all top level properties")
        .build());
    
    return options;
  }
}
