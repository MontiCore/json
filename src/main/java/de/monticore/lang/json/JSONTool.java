/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json;

import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._od.JSON2OD;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.lang.json._prettyprint.JSONFullPrettyPrinter;
import de.monticore.lang.json._symboltable.IJSONArtifactScope;
import de.monticore.lang.json._symboltable.IJSONGlobalScope;
import de.monticore.lang.json._symboltable.JSONScopesGenitorDelegator;
import de.monticore.lang.json._visitor.FullPropertyCalculator;
import de.monticore.lang.json._visitor.JSONTraverser;
import de.monticore.lang.json._visitor.TopLevelPropertyCalculator;
import de.monticore.lang.json.prettyprint.PlantUMLConfig;
import de.monticore.lang.json.prettyprint.PlantUMLUtil;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCSimpleGenericTypesNodeIdentHelper;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JSONTool extends JSONToolTOP {
  
  /*=================================================================*/
  /* Part 1: Handling the arguments and options
  /*=================================================================*/
  
  /**
   * Processes user input from command line and delegates to the corresponding tools.
   *
   * @param args The input parameters for configuring the JSON tool.
   */
  @Override
  public void run(String[] args) {
    
    Options options = initOptions();
    
    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);
      
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
        Log.initDEBUG();
      }
      else {
        Log.init();
      }
      
      // parse input file, which is now available
      // (only returns if successful)
      ASTJSONDocument jsonDoc = parse(cmd.getOptionValue("i"));
      
      // even though JSON defines no symbols, we create the symbol table as
      // MontiCore always expects the symbol table to exist and further tooling
      // such as json2od requires it to compute
      createSymbolTable(jsonDoc);
      
      // -option pretty print "-pp (json [file] | puml (plain | styled) (txt [file] | svg file | png file))"
      if (cmd.hasOption("pp")) {
        String[] params = cmd.getOptionValues("pp");
        if (params.length == 0) {
          printHelp(options);
          return;
        }
        
        if (params[0].equals("json")) {
          String path = params.length == 2 ? params[1] : "";
          prettyPrint(jsonDoc, path);
        }
        else if (params[0].equals("puml")) {
          if (params.length < 3) {
            printHelp(options);
            return;
          }
          boolean styled = false;
          final String style = params[1];
          if (style.equals("plain")) {
            styled = false;
          }
          else if (style.equals("styled")) {
            styled = true;
          }
          else {
            printHelp(options);
            return;
          }
          String format = params[2];
          String file = params.length < 4 ? StringUtils.EMPTY : params[3];
          
          if (format.equals("txt")) {
            if (file.equals("")) {
              prettyPrintPlantUmlCli(jsonDoc, styled);
            }
            else {
              prettyPrintPlantUmlTxt(jsonDoc, file, styled);
            }
          }
          else if (format.equals("svg")) {
            prettyPrintPlantUmlSvg(jsonDoc, file, styled);
          }
          else if (format.equals("png")) {
            prettyPrintPlantUmlPng(jsonDoc, file, styled);
          }
          else {
            printHelp(options);
            return;
          }
        }
        else {
          printHelp(options);
          return;
        }
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
      
    }
    catch (ParseException e) {
      // an unexpected error from the apache CLI parser:
      Log.error("0xA7104 Could not process parameters: " + e.getMessage());
    }
    
  }
  
  
  /*=================================================================*/
  /* Part 2: Executing arguments
  /*=================================================================*/
  
  /**
   * Prints the contents of the JSON-AST to stdout or a specified file.
   *
   * @param jsonDoc The JSON-AST to be pretty printed
   * @param file    The target file name for printing the JSON artifact. If empty, the content is
   *                printed to stdout instead
   */
  @Override
  public void prettyPrint(ASTJSONDocument jsonDoc, String file) {
    // pretty print AST
    JSONFullPrettyPrinter pp = new JSONFullPrettyPrinter(new IndentPrinter());
    String json = pp.prettyprint(jsonDoc);
    print(json, file);
  }
  
  /**
   * Auxiliary method translating the given JSON document to PlantUML DSL code and printing them to
   * the CLI.
   *
   * @param jsonDoc JSON document to generate code for
   * @param styled  whether to include "darkula" style code in the DSL code
   */
  public void prettyPrintPlantUmlCli(ASTJSONDocument jsonDoc, boolean styled) {
    PlantUMLConfig config = new PlantUMLConfig(styled);
    String modelString = PlantUMLUtil.toPlantUmlModelString(Optional.of(jsonDoc), config);
    print(modelString, StringUtils.EMPTY);
  }
  
  /**
   * Auxiliary method translating the given JSON document to PlantUML DSL code and writing it to the
   * given file.
   *
   * @param jsonDoc JSON document to generate code for
   * @param file    file to write the generated code to
   * @param styled  whether to include "darkula" style code in the DSL code
   */
  public void prettyPrintPlantUmlTxt(ASTJSONDocument jsonDoc, String file, boolean styled) {
    Path outputPath = Paths.get(file);
    PlantUMLConfig config = new PlantUMLConfig(styled);
    
    try {
      PlantUMLUtil.writeJsonToPlantUmlModelFile(Optional.of(jsonDoc), outputPath, config);
    }
    catch (IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(1);
    }
  }
  
  /**
   * Auxiliary method rendering the given JSON document as PlantUML JSON diagram in SVG format and
   * saving it to the given file.
   *
   * @param jsonDoc JSON document to render a diagram for
   * @param file    file to save the diagram to
   * @param styled  whether to apply "darkula" styling to the generated JSON image
   */
  public void prettyPrintPlantUmlSvg(ASTJSONDocument jsonDoc, String file, boolean styled) {
    Path outputPath = Paths.get(file);
    PlantUMLConfig config = new PlantUMLConfig(styled);
    
    try {
      PlantUMLUtil.writeJsonToPlantUmlSvg(Optional.of(jsonDoc), outputPath, config);
    }
    catch (IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(1);
    }
  }
  
  /**
   * Auxiliary method rendering the given JSON document as PlantUML JSON diagram in PNG format and
   * saving it to the given file.
   *
   * @param jsonDoc JSON document to render a diagram for
   * @param file    file to save the diagram to
   * @param styled  whether to apply "darkula" styling to the generated JSON image
   */
  public void prettyPrintPlantUmlPng(ASTJSONDocument jsonDoc, String file, boolean styled) {
    Path outputPath = Paths.get(file);
    PlantUMLConfig config = new PlantUMLConfig(styled);
    
    try {
      PlantUMLUtil.writeJsonToPlantUmlPng(Optional.of(jsonDoc), outputPath, config);
    }
    catch (IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(1);
    }
  }
  
  /**
   * Creates reports for the JSON-AST to stdout or a specified file.
   *
   * @param ast  The JSON-AST for which the reports are created
   * @param path The target path of the directory for the report artifacts. If empty, the contents
   *             are printed to stdout instead
   */
  @Override
  public void report(ASTJSONDocument ast, String path) {
    // calculate and print reports
    String aProps = allPropertyNames(ast);
    print(aProps, path + "/" + REPORT_ALL_PROPS);
    
    String cProps = countedPropertyNames(ast);
    print(cProps, path + "/" + REPORT_COUNTED_PROPS);
    
    String tlProps = topLevelPropertyNames(ast);
    print(tlProps, path + "/" + REPORT_TOPLEVEL_PROPS);
  }
  
  // names of the reports:
  public static final String REPORT_ALL_PROPS = "allProperties.txt";
  public static final String REPORT_COUNTED_PROPS = "countedProperties.txt";
  public static final String REPORT_TOPLEVEL_PROPS = "topLevelProperties.txt";
  
  /**
   * Extracts the model name from a given file name. The model name corresponds to the unqualified
   * file name without file extension.
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
   * Parses the contents of a given file as JSON.
   *
   * @param path The path to the JSON-file as String
   */
  @Override
  public ASTJSONDocument parse(String path) {
    Optional<ASTJSONDocument> jsonDoc = Optional.empty();
    
    // disable fail-quick to find all parsing errors
    Log.enableFailQuick(false);
    try {
      Path model = Paths.get(path);
      JSONParser parser = new JSONParser();
      jsonDoc = parser.parse(model.toString());
    }
    catch (IOException | NullPointerException e) {
      Log.error("0xA7109 Input file '" + path + "' not found.");
    }
    
    // re-enable fail-quick to print potential errors
    Log.enableFailQuick(true);
    return jsonDoc.get();
  }
  
  /**
   * Calculates all property names in the JSON-AST as ordered list.
   *
   * @param jsonDoc The JSON-AST to traverse
   * @return A String containing all property names
   */
  private String allPropertyNames(ASTJSONDocument jsonDoc) {
    JSONTraverser traverser = JSONMill.traverser();
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    traverser.add4JSON(fpc);
    jsonDoc.accept(traverser);
    List<String> properties = fpc.getAllPropertyNames();
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
   * Calculates all property names in the JSON-AST as a set with additional number of their
   * respective occurrence.
   *
   * @param jsonDoc The JSON-AST to traverse
   * @return A String containing all property names with the number of occurrence
   */
  public String countedPropertyNames(ASTJSONDocument jsonDoc) {
    JSONTraverser traverser = JSONMill.traverser();
    FullPropertyCalculator fpc = new FullPropertyCalculator();
    traverser.add4JSON(fpc);
    jsonDoc.accept(traverser);
    Map<String, Integer> properties = fpc.getAllPropertyNamesCounted();
    Iterator<Map.Entry<String, Integer>> it = properties.entrySet().iterator();
    String content = "";
    while (it.hasNext()) {
      Map.Entry<String, Integer> entry = it.next();
      content += entry.getKey();
      content += " (" + entry.getValue() + ")";
      if (it.hasNext()) {
        content += ", ";
      }
    }
    return content;
  }
  
  /**
   * Calculates all top-level property names in the JSON-AST as ordered list. Thus, only traverses
   * the AST shallowly.
   *
   * @param jsonDoc The JSON-AST to traverse
   * @return A String containing all top-level property names
   */
  public String topLevelPropertyNames(ASTJSONDocument jsonDoc) {
    JSONTraverser traverser = JSONMill.traverser();
    TopLevelPropertyCalculator tlpc = new TopLevelPropertyCalculator();
    traverser.add4JSON(tlpc);
    traverser.setJSONHandler(tlpc);
    jsonDoc.accept(traverser);
    List<String> properties = tlpc.getTopLevelPropertyNames();
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
  @Override
  public IJSONArtifactScope createSymbolTable(ASTJSONDocument ast) {
    IJSONGlobalScope globalScope = JSONMill.globalScope();
    globalScope.setFileExt(".json");
    
    JSONScopesGenitorDelegator symbolTable = JSONMill.scopesGenitorDelegator();
    
    return symbolTable.createFromAST(ast);
  }
  
  /**
   * Creates an object diagram for the JSON-AST to stdout or a specified file.
   *
   * @param jsonDoc   The JSON-AST for which the object diagram is created
   * @param modelName The derived model name for the JSON-AST
   * @param file      The target file name for printing the object diagram. If empty, the content is
   *                  printed to stdout instead
   */
  public void json2od(ASTJSONDocument jsonDoc, String modelName, String file) {
    // initialize json2od printer
    IndentPrinter printer = new IndentPrinter();
    MCSimpleGenericTypesNodeIdentHelper identifierHelper =
        new MCSimpleGenericTypesNodeIdentHelper();
    ReportingRepository repository = new ReportingRepository(identifierHelper);
    JSONTraverser traverser = JSONMill.traverser();
    JSON2OD json2od = new JSON2OD(printer, repository);
    traverser.add4JSON(json2od);
    traverser.setJSONHandler(json2od);
    
    // print object diagram
    String od = json2od.printObjectDiagram((new File(modelName)).getName(), jsonDoc);
    print(od, file);
  }
  
  /*=================================================================*/
  /* Part 3: Defining the options incl. help-texts
  /*=================================================================*/
  
  /**
   * Initializes the Standard CLI options for the JSON tool.
   *
   * @return The CLI options with arguments.
   */
  
  @Override
  public Options addStandardOptions(Options options) {
    
    //help
    options.addOption(Option.builder("h")
        .longOpt("help")
        .desc("Prints this help dialog")
        .build());
    
    //version
    options.addOption(org.apache.commons.cli.Option.builder("v")
        .longOpt("version")
        .desc("Prints version information")
        .build());
    
    //parse input file
    options.addOption(Option.builder("i")
        .longOpt("input")
        .argName("file")
        .hasArg()
        .desc("Reads the source file (mandatory) and parses the contents as JSON")
        .build());
    
    //pretty print JSON
    options.addOption(Option.builder("pp")
        .longOpt("prettyprint")
        .argName("(json [file] | puml (plain | styled) (txt [file] | svg file | png file))")
        .optionalArg(true).numberOfArgs(4)
        .desc("Prints the JSON either as pretty printed JSON (json), PlantUML DSL code (puml" +
            " txt), or PlantUML SVG/PNG diagram (puml svg|png), and writes it to the given " +
            "file (mandatory for SVG/PNG). When using PlantUML it must be specified whether " +
            "to use default styling (plain) or the \"Darkula\"-themed style (styled).")
        .build());
    
    //reports
    options.addOption(Option.builder("r")
        .longOpt("report")
        .argName("dir")
        .hasArg(true).desc(
        "Prints reports of the JSON artifact to the specified directory (optional). Available reports:" +
            System.lineSeparator() + REPORT_ALL_PROPS + ": a list of all properties, " +
            System.lineSeparator() + REPORT_COUNTED_PROPS +
            ": a set of all properties with the number of occurrences, " + System.lineSeparator() +
            REPORT_TOPLEVEL_PROPS + ": a list of all top level properties").build());
    
    return options;
  }
  
  /**
   * Initializes the Additional CLI options for the JSON tool.
   *
   * @return The CLI options with arguments.
   */
  @Override
  public Options addAdditionalOptions(Options options) {
    
    // developer level logging
    options.addOption(Option.builder("d").longOpt("dev")
        .desc("Specifies whether developer level logging should be used (default is false)")
        .build());
    
    // print object diagram
    options.addOption(
        Option.builder("so").longOpt("syntaxobjects").argName("file").optionalArg(true)
            .numberOfArgs(1).desc(
                "Prints an object diagram of the JSON-AST to stdout or the specified file (optional)")
            .build());
    return options;
    
  }
}
