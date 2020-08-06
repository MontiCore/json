package de.monticore.lang.json;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class JSONCLIConfiguration {
  
  public static final String HELP = "h";
  public static final String HELP_LONG = "help";
  public static final String INPUT = "i";
  public static final String INPUT_LONG = "input";
  public static final String PRINT = "pp";
  public static final String PRINT_LONG = "prettyprint";
  public static final String REPORT = "r";
  public static final String REPORT_LONG = "report";
  
  Options options;
  
  /**
   * Gets the available CLI options for the JSON tool.
   */
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
    options.addOption(Option.builder(HELP)
        .longOpt(HELP_LONG)
        .desc("Prints this help dialog")
        .build());
    
    // parse input file
    options.addOption(Option.builder(INPUT)
        .longOpt(INPUT_LONG)
        .argName("file")
        .hasArg()
        .desc("Reads the source file (mandatory) and parses the contents as JSON")
        .build());
  
    // pretty print JSON
    options.addOption(Option.builder(PRINT)
        .longOpt(PRINT_LONG)
        .argName("file")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Prints the JSON-AST to stdout or the specified file (optional)")
        .build());
    
    // pretty print JSON
    options.addOption(Option.builder(REPORT)
        .longOpt(REPORT_LONG)
        .argName("dir")
        .hasArg(true)
        .desc("Prints reports of the JSON artifact to stdout or the specified directory (optional). Available reports: "
            + "(1) Prints a list of all properties, "
            + "(2) Prints a set of all properties with the number of occurrences, "
            + "(3) Prints a list of all top level properties")
        .build());
  }
}
