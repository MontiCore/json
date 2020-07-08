package de.monticore.lang.json;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.lang.json._visitor.FullPropertyCalculator;
import de.monticore.lang.json._visitor.TopLevelPropertyCalculator;
import de.monticore.lang.json.prettyprint.JSONPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Command line interface for the JSON language and corresponding tooling.
 */
public class JSONCLI {
  
  private BufferedReader reader;
  private FullPropertyCalculator fpc;
  private TopLevelPropertyCalculator tlpc;
  private Optional<ASTJSONDocument> jsonDoc;
  
  private static final String separator = " ";
  private static final String[] help = { "-h" };
  private static final String[] parse = { "parse" };
  private static final String[] parseFile = { "parse", "-f" };
  private static final String[] print = { "print" };
  private static final String[] printToFile = { "print", "-f" };
  private static final String[] propertiesAll = { "properties", "-a" };
  private static final String[] propertiesAllCount = { "properties", "-a", "-c" };
  private static final String[] propertiesTL = { "properties", "-tl" };
  private static final String[] quit = { "-q" };
  
  /**
   * Main method that is called from command line and runs the JSON tool.
   * 
   * @param args The input parameters. Not required nor handled.
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    JSONCLI cli = new JSONCLI();
    cli.run();
  }
  
  /**
   * Main run method of the CLI instance. Initializes the tool and passes
   * arguments to main program loop.
   * 
   * @throws IOException
   */
  private void run() throws IOException {
    init();
    printHelp();
    handleArgs();
  }
  
  /**
   * Initializes the CLI tool. Sets up the logger as well as available tooling.
   */
  private void init() {
    System.out.println("##### JSON command line tool #####");
    Log.init();
    Log.enableFailQuick(false);
    reader = new BufferedReader(new InputStreamReader(System.in));
    fpc = new FullPropertyCalculator();
    tlpc = new TopLevelPropertyCalculator();
    jsonDoc = Optional.empty();
  }
  
  /**
   * Refreshes the CLI tool on model changes. Resets the stored JSON-AST and
   * instantiates available tooling to enable efficient computation.
   */
  private void refresh() {
    fpc = new FullPropertyCalculator();
    tlpc = new TopLevelPropertyCalculator();
    jsonDoc = Optional.empty();
  }
  
  /**
   * Requests command line input.
   * 
   * @return The given input line from command line
   * @throws IOException
   */
  private String input() throws IOException {
    System.out.print("> ");
    String input = reader.readLine();
    return input;
  }
  
  /**
   * Main program loop. Processes user input from command line and delegates to
   * the corresponding tools.
   * 
   * @throws IOException
   */
  private void handleArgs() throws IOException {
    boolean exit = false;
    
    while (!exit) {
      String input = input();
      
      if (input.isEmpty()) {
        // do nothing
      }
      else if (hasFormat(input, help)) {
        printHelp();
      }
      else if (hasFormat(input, parseFile)) {
        parseFile(input.split(separator, 3)[2]);
      }
      else if (hasFormat(input, parse)) {
        parseString(input.split(separator, 2)[1]);
      }
      else if (hasFormat(input, printToFile)) {
        print(input.split(separator, 3)[2]);
      }
      else if (hasFormat(input, print) && partsMatch(input, 1)) {
        print("");
      }
      else if (hasFormat(input, propertiesAllCount) && partsMatch(input, 3)) {
        countedPropertyNames();
      }
      else if (hasFormat(input, propertiesAll) && partsMatch(input, 2)) {
        allPropertyNames();
      }
      else if (hasFormat(input, propertiesTL) && partsMatch(input, 2)) {
        topLevelPropertyNames();
      }
      else if (hasFormat(input, quit) && partsMatch(input, 1)) {
        exit = true;
      }
      else {
        printHelp();
      }
      
    }
  }
  
  /**
   * Checks the format of a command line input to derive the correct behavior.
   * 
   * @param input The input String from command line.
   * @param format A predefined format of input
   * @return true, ifthe input matches the format, false otherwise
   */
  private boolean hasFormat(String input, String[] format) {
    String[] tmp = input.split(separator);
    for (int i = 0; i < format.length; i++) {
      if (tmp.length <= i || !format[i].equals(tmp[i])) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Checks whether the number of parts matches expected number concerning a
   * specific format.
   * 
   * @param input The input String
   * @param parts The expected number of parts
   * @return true if parts matches the expected number, false otherwise
   */
  private boolean partsMatch(String input, int parts) {
    return input.split(separator).length == parts;
  }
  
  /**
   * Prints the usage of the CLI. Contains available commands with possible
   * parameters an their explanations.
   */
  private void printHelp() {
    System.out.println("-h                    Opens this help dialoge");
    System.out.println("parse *input*         Parses the given input as JSON");
    System.out.println("parse -f *source*     Reads the given source file and parses the contents as JSON");
    System.out.println("print                 Prints the JSON-AST");
    System.out.println("print -f *target*     Prints the JSON-AST to the specified file");
    System.out.println("properties -a         Returns a list of all properties in the cached JSON artifact");
    System.out.println("properties -a -c      Returns a set of all properties and the number of ocurrence in the cached JSON artifact");
    System.out.println("properties -tl        Returns a list of all top level properties in the cached JSON artifact");
    System.out.println("-q                    Quit");
  }
  
  /**
   * Parses an input String as JSON.
   * 
   * @param json The input String
   * @throws IOException
   */
  private void parseString(String json) throws IOException {
    refresh();
    JSONParser parser = new JSONParser();
    jsonDoc = parser.parse_StringJSONDocument(json);
  }
  
  /**
   * Parses the contents of a given file as JSON.
   * 
   * @param path The path to the JSON-file as String
   */
  private void parseFile(String path) {
    refresh();
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
   * Prints all top-level property names in the JSON-AST as ordered list. THus,
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
