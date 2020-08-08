/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Configuration for the command line interface for the JSON language.
 */
@Deprecated
public class JSONCLIConfiguration {
  
  Options options;
  
  /**
   * Gets the available CLI options for the JSON tool.
   */
  @Deprecated
  public Options getOptions() {
    if (options == null) {
      initOptions();
    }
    return options;
  }
  
  /**
   * Initializes the available CLI options for the JSON tool.
   */
  protected void initOptions() {
    options = new Options();
    
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
        .desc("Prints reports of the JSON artifact to stdout or the specified directory (optional). Available reports: "
            + "(1) Prints a list of all properties, "
            + "(2) Prints a set of all properties with the number of occurrences, "
            + "(3) Prints a list of all top level properties")
        .build());
  }
}
