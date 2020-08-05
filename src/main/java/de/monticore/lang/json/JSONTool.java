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
  
  private static final String REPORT_ALL_PROPS = "/AllProperties.txt";
  private static final String REPORT_COUNTED_PROPS = "/CountedProperties.txt";
  private static final String REPORT_TOPLEVEL_PROPS = "/TopLevelProperties.txt";
  
  
  private Optional<ASTJSONDocument> jsonDoc;
  private FullPropertyCalculator fpc;
  private TopLevelPropertyCalculator tlpc;
  
  /**
   * Main method that is called from command line and runs the JSON tool.
   * 
   * @param args The input parameters for configuring the JSON tool.
   */
  public static void main(String[] args) {
    JSONTool cli = new JSONTool();
    cli.run(args);
  }
  
  /**
   * Main run method of the CLI instance. Initializes the tool and passes
   * arguments to main program loop.
   * 
   * @param args The input parameters for configuring the JSON tool.
   */
  private void run(String[] args) {
    init();
    handleArgs(args);
  }
  
  /**
   * Initializes the CLI tool. Sets up the logger as well as available tooling.
   */
  private void init() {
    Log.init();
    jsonDoc = Optional.empty();
    fpc = new FullPropertyCalculator();
    tlpc = new TopLevelPropertyCalculator();
  }
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   * 
   * @param args The input parameters for configuring the JSON tool.
   */
  private void handleArgs(String[] args) {
    try {
      // Create CLI parser and parse input options from command line
      CommandLineParser parser = new DefaultParser();
      JSONCLIConfiguration config = new JSONCLIConfiguration();
      CommandLine cmd = parser.parse(config.getOptions(), args);
      
      // help
      if (cmd.hasOption(JSONCLIConfiguration.HELP)) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("JSONTool", config.getOptions());
      }
      
      // parse input file
      if (cmd.hasOption(JSONCLIConfiguration.INPUT)) {
        parseFile(cmd.getOptionValue(JSONCLIConfiguration.INPUT));
      }
      
      // pretty print
      if (cmd.hasOption(JSONCLIConfiguration.PRINT)) {
        String path = cmd.getOptionValue(JSONCLIConfiguration.PRINT, StringUtils.EMPTY);
        prettyPrint(path);
      }
      
      // reports
      if (cmd.hasOption(JSONCLIConfiguration.REPORT)) {
        String path = cmd.getOptionValue(JSONCLIConfiguration.REPORT, StringUtils.EMPTY);
        report(path);
      }
      
    }
    catch (ParseException e) {
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }
    
  }
  
  /**
   * Parses the contents of a given file as JSON.
   * 
   * @param path The path to the JSON-file as String
   */
  private void parseFile(String path) {
    Path model = Paths.get(path);
    JSONParser parser = new JSONParser();
    try {
      jsonDoc = parser.parse(model.toString());
    }
    catch (IOException e) {
      Log.error("0xA7102 File " + path + " not found.");
    }
  }
  
  /**
   * Prints the contents of the JSON-AST to stdout or a specified file.
   * 
   * @param file The target file name for printing the JSON artifact. If empty,
   *          the content is printed to stdout instead
   */
  private void prettyPrint(String file) {
    // check if AST is available
    if (!jsonDoc.isPresent()) {
      Log.error("0xA7103 No JSON artifact available to pretty print. First specify a valid JSON artifact as input.");
      return;
    }
    
    // pretty print AST
    JSONPrettyPrinter pp = new JSONPrettyPrinter();
    String json = pp.printJSONDocument(jsonDoc.get());
    print(json, file);
  }
  
  /**
   * Creates reports for the JSON-AST to stdout or a specified file.
   * 
   * @param path The target path of the directory for the report artifacts. If
   *          empty, the contents are printed to stdout instead
   */
  private void report(String path) {
    // check if AST is available
    if (!jsonDoc.isPresent()) {
      Log.error("0xA7104 No JSON artifact available for reporting. First specify a valid JSON artifact as input.");
      return;
    }
    
    // calculate reports
    String aProps = allPropertyNames();
    String cProps = countedPropertyNames();
    String tlProps = topLevelPropertyNames();
    
    // print reports
    print(aProps, path + REPORT_ALL_PROPS);
    print(cProps, path + REPORT_COUNTED_PROPS);
    print(tlProps, path + REPORT_TOPLEVEL_PROPS);
  }
  
  /**
   * Calculates all property names in the JSON-AST as ordered list.
   * 
   * @return A String containing all property names
   */
  private String allPropertyNames() {
    List<String> properties = fpc.getAllPropertyNames(jsonDoc.get());
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
  private String countedPropertyNames() {
    Map<String, Integer> properties = fpc.getAllPropertyNamesCounted(jsonDoc.get());
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
  private String topLevelPropertyNames() {
    List<String> properties = tlpc.getTopLevelPropertyNames(jsonDoc.get());
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
  private void print(String content, String path) {
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
