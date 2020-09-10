/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.json.JSONMill;
import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._od.JSON2OD;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.lang.json._symboltable.JSONArtifactScope;
import de.monticore.lang.json._symboltable.JSONGlobalScope;
import de.monticore.lang.json._symboltable.JSONSymbolTableCreatorDelegator;
import de.monticore.lang.json._visitor.FullPropertyCalculator;
import de.monticore.lang.json._visitor.TopLevelPropertyCalculator;
import de.monticore.lang.json.prettyprint.JSONPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * Command line interface for the JSON language and corresponding tooling.
 * Defines, handles, and executes the corresponding command line options and
 * arguments, such as --help
 */
public class JSONCLI {
  
  /*=================================================================*/
  /* Part 1: Handling the arguments and options
  /*=================================================================*/
  
  /**
   * Main method that is called from command line and runs the JSON tool.
   * 
   * @param args The input parameters for configuring the JSON tool.
   */
  public static void main(String[] args) {
    JSONCLI cli = new JSONCLI();
    // TODONJ: wäre es nicht für User sinnvoller mit Log.init(); zu arbeiten?
    // Was ist der Unterschied?

    // initialize logging with slf4j logging variant
    Slf4jLog.init();
    cli.run(args);
  }
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   * 
   * @param args The input parameters for configuring the JSON tool.
   */
  public void run(String[] args) {
  
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
      
      // -option developer logging
      if (cmd.hasOption("d")) {
        useDeveloperLogbackConfiguration();
      }
      //TODONJ: Anstatt initialer LogBack-Config und Überschreiben hier:
      // wie wäre es einen Else-Fall aufzubauen und da dann zu Konfigurieren
      // Und ich sehe immer noch Default: Log.init();
      
      // parse input file, which is now available
      // (only returns if successful)
      ASTJSONDocument jsonDoc = parseFile(cmd.getOptionValue("i"));
      
      // even though JSON defines no symbols, we create the symbol table as
      // MontiCore always expects the symbol table to exist and further tooling
      // such as json2od requires it to compute
      createSymbolTable(jsonDoc);
      
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
      
      // -option syntax objects
      if (cmd.hasOption("so")) {
        String path = cmd.getOptionValue("so", StringUtils.EMPTY);
        json2od(jsonDoc, getModelNameFromFile(cmd.getOptionValue("i")), path);
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
    formatter.setWidth(80);
    formatter.printHelp("JSONCLI", options);
  }
  
  /*=================================================================*/
  /* Part 2: Executing arguments
  /*=================================================================*/
  
  /**
   * Enables detailed developer logging. Loads and configures logback with the
   * developer logback XML input stream.
   */
  public void useDeveloperLogbackConfiguration() {
    String devConfig = "developer.logging.xml";
    InputStream config = JSONCLI.class.getClassLoader().getResourceAsStream(devConfig);
    ILoggerFactory lf = LoggerFactory.getILoggerFactory();
    if(lf instanceof LoggerContext) {
      LoggerContext context = (LoggerContext) lf;
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      context.reset();
      try {
        configurator.doConfigure(config);
      } catch (JoranException e) {
        Log.error("0xA7103 Could not configure logging (-d), with file " + devConfig + ".");
      }
    }
  }
  
  /**
   * Parses the contents of a given file as JSON.
   * 
   * @param path The path to the JSON-file as String
   */
  public ASTJSONDocument parseFile(String path) {
    Optional<ASTJSONDocument> jsonDoc = Optional.empty();
    
    // disable fail-quick to find all parsing errors
    Log.enableFailQuick(false);
    try {
      Path model = Paths.get(path);
      JSONParser parser = new JSONParser();
      jsonDoc = parser.parse(model.toString());
    }
    catch (IOException | NullPointerException e) {
      Log.error("0xA7102 Input file '" + path + "' not found.");
    }
    // TODONJ: Wenn die Datei gefunden wird, aber nicht geparst werden kann. Entsteht dann auch 0xA7102?
    // Bitte prüfen.
    
    // re-enable fail-quick to print potential errors
    Log.enableFailQuick(true);
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
   * Creates the symbol table from the parsed AST.
   *
   * @param ast The top JSON model element.
   * @return The artifact scope derived from the parsed AST
   */
  public JSONArtifactScope createSymbolTable(ASTJSONDocument ast) {
    JSONGlobalScope globalScope = JSONMill.jSONGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .setModelFileExtension(".json")
        .build();

    JSONSymbolTableCreatorDelegator symbolTable = JSONMill.jSONSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();
    
    return symbolTable.createFromAST(ast);
  }

  /**
   * Extracts the model name from a given file name. The model name corresponds
   * to the unqualified file name without file extension.
   * 
   * @param file The path to the input file
   * @return The extracted model name
   */
  public String getModelNameFromFile(String file) {
    String modelName = new File(file).getName();
    // cut file extension if present
    if (modelName.length() > 0) {
      int lastIndex = modelName.lastIndexOf(".");
      if (lastIndex != -1) {
        modelName = modelName.substring(0, lastIndex);
      }
    }
    return modelName;
  }

  /**
   * Creates an object diagram for the JSON-AST to stdout or a specified file.
   * 
   * @param jsonDoc The JSON-AST for which the object diagram is created
   * @param modelName The derived model name for the JSON-AST
   * @param file The target file name for printing the object diagram. If empty,
   *          the content is printed to stdout instead
   */
  public void json2od(ASTJSONDocument jsonDoc, String modelName, String file) {
    // initialize json2od printer
    IndentPrinter printer = new IndentPrinter();
    MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
    ReportingRepository repository = new ReportingRepository(identifierHelper);
    JSON2OD json2od = new JSON2OD(printer, repository);
    
    // print object diagram
    String od = json2od.printObjectDiagram((new File(modelName)).getName(), jsonDoc);
    print(od, file);
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
      // create directories (logs error otherwise)
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
    
    // developer level logging
    options.addOption(Option.builder("d")
        .longOpt("dev")
        .desc("Specifies whether developer level logging should be used (default is false)")
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
            + System.lineSeparator() + REPORT_ALL_PROPS + ": a list of all properties, " 
            + System.lineSeparator() + REPORT_COUNTED_PROPS + ": a set of all properties with the number of occurrences, " 
            + System.lineSeparator() + REPORT_TOPLEVEL_PROPS + ": a list of all top level properties")
        .build());
    
    // print object diagram
    options.addOption(Option.builder("so")
        .longOpt("syntaxobjects")
        .argName("file")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Prints an object diagram of the JSON-AST to stdout or the specified file (optional)")
        .build());
    
    return options;
  }
}
