package de.monticore.lang.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

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
  
  private Optional<ASTJSONDocument> jsonDoc;
  private FullPropertyCalculator fpc;
  private TopLevelPropertyCalculator tlpc;
  
  private static final String help = "-h";
  private static final String input = "-i";
  private static final String print = "-pp";
  private static final String properties = "-prop";
  private static final String all = "-a";
  private static final String counted = "-c";
  private static final String top_level = "-tl";
  
  private static final List<String> allArgs = Arrays.asList(help, input, print, properties, all, counted, top_level);
  
  
  /**
   * Main method that is called from command line and runs the JSON tool.
   * 
   * @param args The input parameters for configuring the JSON tool.
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    JSONTool cli = new JSONTool();
    cli.run(args);
  }
  
  /**
   * Main run method of the CLI instance. Initializes the tool and passes
   * arguments to main program loop.
   * 
   * @throws IOException
   */
  private void run(String[] args) throws IOException {
    init();
    handleArgs(args);
  }
  
  /**
   * Initializes the CLI tool. Sets up the logger as well as available tooling.
   */
  private void init() {
    Log.init();
    Log.enableFailQuick(false);
    jsonDoc = Optional.empty();
    fpc = new FullPropertyCalculator();
    tlpc = new TopLevelPropertyCalculator();
  }
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   * 
   * @param args The input parameters for configuring the JSON tool.
   * @throws IOException
   */
  private void handleArgs(String[] args) throws IOException {
    // setup argument list and remove empty Strings
    List<String> formArgs = new ArrayList<String>(Arrays.asList(args));
    formArgs.removeAll(Arrays.asList(StringUtils.EMPTY));
    
    // print help if requested or arguments cannot be processed
    if (formArgs.contains(help) || !processCommands(formArgs)) {
      printHelp();
    }
  }
  
  /**
   * Executes the JSON tool based on the given commands.
   * 
   * @param args The formatted input arguments (without empty entries)
   * @return true, if the execution was successful, false otherwise
   */
  private boolean processCommands(List<String> args) {
    // ensure exactly one input model is defined
    if (Collections.frequency(args, input) != 1) {
      System.out.println("Error: No unique JSON artifact defined. Make sure to define an input file exactly once.");
      return false;
    }
    
    // retrieve input model if available
    int pathPos = args.indexOf(input) + 1;
    if (pathPos == -1 || args.size() < pathPos) {
      System.out.println("Error: No JSON artifact defined.");
      return false;
    }
    
    parseFile(args.get(pathPos));
    if (!jsonDoc.isPresent()) {
      // error message already printed if we reach this position
      return false;
    }
    
    // remove input command from argument list
    args.remove(pathPos);
    args.remove(pathPos - 1);
    
    // process remaining commands sequentially and stop if any error occurs
    for (int i = 0; i < args.size(); i++) {
      String arg = args.get(i);
      if (arg.equals(print)) {
        Optional<String> path = fetchTargetPath(args, i + 1);
        print(path.get());
        
        // increment counter due to additional arguments
        if (path.isPresent()) {
          i++;
        }
      } else if (arg.equals(properties) && i + 1 < args.size()) {
        String propArg = args.get(i+1);
        Optional<String> path = fetchTargetPath(args, i + 2);
        
        // delegate to correct behavior based on the property argument
        if (propArg.equals(all)) {
          allPropertyNames();
        } else if (propArg.equals(counted)) {
          countedPropertyNames();
        } else if (propArg.equals(top_level)) {
          topLevelPropertyNames();
        } else {
          System.out.println("Error: " + propArg + " is not a valid property arguement.");
          return false;
        }
        
        // increment counter due to additional arguments
        if (path.isPresent()) {
          i += 2;
        } else {
          i++;
        }
      } else {
        System.out.println("Error: Unknown or incomplete command " + arg);
        return false;
      }
    }
    
    return true;
  }

  /**
   * Returns a file in the argument list based on the specified position. Checks
   * whether the position contains a valid target path and creates the required
   * parent directories if possible.
   * 
   * @param args The argument list
   * @param pos The possible position of the path to check
   * @return An optional with the target path, Optional.empty() otherwise
   */
  private Optional<String> fetchTargetPath(List<String> args, int pos) {
    String path = "";
    if (pos < args.size()) {
      path = args.get(pos);
      if (allArgs.contains(path)) {
        return Optional.empty();
      } else {
        File f = new File(path);
        if (!f.getParentFile().mkdirs()) {
          return Optional.empty();
        }
      }
    }
    return Optional.of(path);
  }
  
  /**
   * Prints the usage of the CLI. Contains available commands with possible
   * parameters an their explanations.
   */
  private void printHelp() {
    System.out.println("-h                Opens this help dialoge");
    System.out.println("-i *source*       Reads the given source file and parses the contents as JSON");
    System.out.println("-pp               Prints the JSON-AST");
    System.out.println("-pp *target*      Prints the JSON-AST to the specified file");
    System.out.println("-prop -a          Returns a list of all properties in the cached JSON artifact");
    System.out.println("-prop -c          Returns a set of all properties and the number of ocurrence in the cached JSON artifact");
    System.out.println("-prop -tl         Returns a list of all top level properties in the cached JSON artifact");
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
      System.out.println("Error: File not found.");
    }
  }
  
  /**
   * Prints the contents of the JSON-AST to command line or a specified file.
   * 
   * @param file The target file for printing the JSON artifact. If empty, the
   *          artifact is printed to the command line instead
   */
  private void print(String file) {
    // check if AST is available
    if (!jsonDoc.isPresent()) {
      System.out.println("Error: No JSON artifact available. First parse a valid JSON artifact.");
      return;
    }
    
    // pretty print AST
    JSONPrettyPrinter pp = new JSONPrettyPrinter();
    String json = pp.printJSONDocument(jsonDoc.get());
    
    // print to console or file
    if (file.isEmpty()) {
      System.out.println(json);
    } else {
      FileWriter writer;
      try {
        writer = new FileWriter(file);
        writer.write(json);
        writer.close();
      } catch (IOException e) {
        System.out.println("Error: Could not write to file " + file);
      }
    }
  }
  
  /**
   * Prints all property names in the JSON-AST as ordered list.
   */
  private void allPropertyNames() {
    // check if AST is available
    if (!jsonDoc.isPresent()) {
      System.out.println("Error: No JSON artifact available. First parse a valid JSON artifact.");
      return;
    }
    
    // print property names
    List<String> properties = fpc.getAllPropertyNames(jsonDoc.get());
    for (int i = 0; i < properties.size(); i++) {
      System.out.print(properties.get(i));
      if (i < properties.size() - 1) {
        System.out.print(", ");
      }
    }
    System.out.println();
  }
  
  /**
   * Prints all property names in the JSON-AST as a set with additional number
   * of their respective occurrence.
   */
  private void countedPropertyNames() {
    // check if AST is available
    if (!jsonDoc.isPresent()) {
      System.out.println("Error: No JSON artifact available. First parse a valid JSON artifact.");
      return;
    }
    
    // print property names with number of occurrence
    Map<String, Integer> properties = fpc.getAllPropertyNamesCounted(jsonDoc.get());
    Set<Entry<String, Integer>> entries = properties.entrySet();
    Iterator<Entry<String, Integer>> it = entries.iterator();
    while (it.hasNext()) {
      Entry<String, Integer> entry = it.next();
      System.out.print(entry.getKey());
      System.out.print(" (" + entry.getValue() + ")");
      if (it.hasNext()) {
        System.out.print(",");
      }
    }
    System.out.println();
  }
  
  /**
   * Prints all top-level property names in the JSON-AST as ordered list. Thus,
   * only traverses the AST shallowly.
   */
  private void topLevelPropertyNames() {
    // check if AST is available
    if (!jsonDoc.isPresent()) {
      System.out.println("Error: No JSON artifact available. First parse a valid JSON artifact.");
      return;
    }
    
    // print property names
    List<String> properties = tlpc.getTopLevelPropertyNames(jsonDoc.get());
    for (int i = 0; i < properties.size(); i++) {
      System.out.print(properties.get(i));
      if (i < properties.size() - 1) {
        System.out.print(", ");
      }
    }
    System.out.println();
  }
}
