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
   * @param args The input parameters
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    JSONCLI cli = new JSONCLI();
    cli.run(args);
  }
  
  private void run(String[] args) throws IOException {
    init();
    printHelp();
    handleArgs(args);
  }
  
  private void init() {
    System.out.println("##### JSON command line tool #####");
    Log.init();
    Log.enableFailQuick(false);
    reader = new BufferedReader(new InputStreamReader(System.in));
    fpc = new FullPropertyCalculator();
    tlpc = new TopLevelPropertyCalculator();
    jsonDoc = Optional.empty();
  }
  
  private void refresh() {
    fpc = new FullPropertyCalculator();
    tlpc = new TopLevelPropertyCalculator();
    jsonDoc = Optional.empty();
  }
  
  private String input() throws IOException {
    System.out.print("> ");
    String input = reader.readLine();
    return input;
  }
  
  private void handleArgs(String[] args) throws IOException {
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
      else if (hasFormat(input, print)) {
        print("");
      }
      else if (hasFormat(input, propertiesAllCount)) {
        countedPropertyNames();
      }
      else if (hasFormat(input, propertiesAll)) {
        allPropertyNames();
      }
      else if (hasFormat(input, propertiesTL)) {
        topLevelPropertyNames();
      }
      else if (hasFormat(input, quit)) {
        exit = true;
      }
      else {
        printHelp();
      }
      
    }
  }
  
  private boolean hasFormat(String input, String[] format) {
    String[] tmp = input.split(separator);
    for (int i = 0; i < format.length; i++) {
      if (tmp.length <= i || !format[i].equals(tmp[i])) {
        return false;
      }
    }
    return true;
  }
  
  private void printHelp() {
    System.out.println("-h                    Opens this help dialoge");
    System.out.println("parse *input*         Parses the given input as JSON");
    System.out.println("parse -f *source*     Reads the given source file and parses the contents as JSON");
    System.out.println("print                 Prints the JSON-AST");
    System.out.println("print -f *target*     Prints the JSON-AST to the specified file");
    System.out.println("properties -a         Returns a list of all properties in the chached JSON artifact");
    System.out.println("properties -a -c      Returns a set of all properties and the number of ocurrence in the chached JSON artifact");
    System.out.println("properties -tl        Returns a list of all top level properties in the chached JSON artifact");
    System.out.println("-q                    Quit");
  }
  
  private void parseString(String json) throws IOException {
    refresh();
    JSONParser parser = new JSONParser();
    jsonDoc = parser.parse_StringJSONDocument(json);
  }
  
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
  
  private void print(String file) throws IOException {
    // check if AST is available
    if (!jsonDoc.isPresent()) {
      System.out.println("Error: No JSON artifact available. First parse a valid JSON artifact.");
      return;
    }
    
    // pretty print AST
    JSONPrettyPrinter pp = new JSONPrettyPrinter();
    String json = pp.printJSONDocument(jsonDoc.get());
    
    // print to concole or file
    if (file.isEmpty()) {
      System.out.println(json);
    } else {
      FileWriter writer = new FileWriter(file);
      writer.write(json);
      writer.close();
    }
  }
  
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
