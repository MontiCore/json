package de.monticore.lang.json.prettyprint;

import de.monticore.lang.json.JSONMill;
import de.monticore.lang.json._ast.*;
import de.monticore.lang.json._visitor.JSONHandler;
import de.monticore.lang.json._visitor.JSONTraverser;
import de.monticore.lang.json._visitor.JSONVisitor2;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Parser translating JSON documents to PlantUML DSL code.
 */
public class JSONToPlantUML implements JSONVisitor2, MCCommonLiteralsVisitor2, JSONHandler {
  
  final private PlantUMLConfig config;
  private IndentPrinter printer;
  private JSONTraverser traverser;
  
  /**
   * Constructor supporting custom PlantUML configs.
   *
   * @param config defines pretty print options
   */
  public JSONToPlantUML(PlantUMLConfig config) {
    this.config = config;
    this.printer = new IndentPrinter();
    this.traverser = JSONMill.traverser();
    this.traverser.add4JSON(this);
    this.traverser.add4MCCommonLiterals(this);
    this.traverser.setJSONHandler(this);
  }
  
  /**
   * Default constructor using the default PlantUML config.
   */
  public JSONToPlantUML() {
    this(new PlantUMLConfig());
  }
  
  /**
   * Translates the given JSON document to PlantUML DSL code.
   *
   * @param jsonDocument JSON document to translate
   * @return generated PlantUML DSL code
   */
  public String printJSONDocument(ASTJSONDocument jsonDocument) {
    this.printer.clearBuffer();
    this.getTraverser().handle(jsonDocument);
    return printer.getContent();
  }
  
  ///////////////////////////////////////////////////////////////////////////
  // Handler overrides
  ///////////////////////////////////////////////////////////////////////////
  
  public JSONTraverser getTraverser() {
    return this.traverser;
  }
  
  public void setTraverser(JSONTraverser traverser) {
    this.traverser = traverser;
  }
  
  /**
   * Handles the traversal over a JSON object. It basically handles placing commas behind each
   * property except the last one.
   *
   * @param node JSON object
   */
  @Override
  public void handle(ASTJSONObject node) {
    this.getTraverser().visit(node);
    int length = node.getPropList().size();
    for (int i = 0; i < length; i++) {
      node.getProp(i).accept(this.getTraverser());
      if (i < length - 1) {
        this.printer.println(",");
      }
      else {
        this.printer.println();
      }
    }
    this.getTraverser().endVisit(node);
  }
  
  /**
   * Handles the traversal over a JSON array. It basically handles placing commas behind each
   * element except the last one.
   *
   * @param node JSON array
   */
  @Override
  public void handle(ASTJSONArray node) {
    this.getTraverser().visit(node);
    int length = node.getJSONValueList().size();
    for (int i = 0; i < length; i++) {
      node.getJSONValue(i).accept(this.getTraverser());
      if (i < length - 1) {
        this.printer.println(",");
      }
      else {
        this.printer.println();
      }
    }
    this.getTraverser().endVisit(node);
  }
  
  ///////////////////////////////////////////////////////////////////////////
  // Visitor overrides
  ///////////////////////////////////////////////////////////////////////////
  
  @Override
  public void visit(ASTJSONDocument node) {
    this.printer.println("@startjson");
    if (this.config.useStyling) {
      this.printer.println("<style>");
      this.printer.println("jsonDiagram {");
      this.printer.indent();
      this.printer.println("node {");
      this.printer.indent();
      this.printer.println("BackGroundColor " + this.config.nodeBackgroundColor);
      this.printer.println("RoundCorner 0");
      this.printer.unindent();
      this.printer.println("}");
      this.printer.unindent();
      this.printer.println("}");
      this.printer.println("</style>");
    }
  }
  
  @Override
  public void endVisit(ASTJSONDocument node) {
    this.printer.println();
    this.printer.print("@endjson");
  }
  
  @Override
  public void visit(ASTJSONObject node) {
    this.printer.println("{");
    this.printer.indent();
  }
  
  @Override
  public void endVisit(ASTJSONObject node) {
    this.printer.unindent();
    this.printer.print("}");
  }
  
  @Override
  public void visit(ASTJSONArray node) {
    this.printer.println("[");
    this.printer.indent();
  }
  
  @Override
  public void endVisit(ASTJSONArray node) {
    this.printer.unindent();
    this.printer.print("]");
  }
  
  @Override
  public void visit(ASTJSONBoolean node) {
    if (this.config.useStyling) {
      this.printer.print(this.colored(Boolean.toString(node.getBooleanLiteral().getValue()),
          this.config.propValueBooleanColor));
    }
    else {
      this.printer.print(node.getBooleanLiteral().getValue());
    }
  }
  
  @Override
  public void visit(ASTJSONNull node) {
    if (this.config.useStyling) {
      this.printer.print(this.colored("null", this.config.propValueNullColor));
    }
    else {
      this.printer.print("null");
      
    }
  }
  
  @Override
  public void visit(ASTJSONProperty node) {
    if (this.config.useStyling) {
      this.printer.print(this.colored(node.getKey(), this.config.propKeyColor) + ": ");
    }
    else {
      this.printer.print("\"" + node.getKey() + "\": ");
    }
  }
  
  @Override
  public void visit(ASTSignedBasicDoubleLiteral node) {
    this.printNumber(node.getSource());
  }
  
  @Override
  public void visit(ASTSignedBasicFloatLiteral node) {
    this.printNumber(node.getSource());
  }
  
  @Override
  public void visit(ASTSignedBasicLongLiteral node) {
    this.printNumber(node.getSource());
  }
  
  @Override
  public void visit(ASTSignedNatLiteral node) {
    this.printNumber(node.getSource());
  }
  
  @Override
  public void visit(ASTStringLiteral node) {
    if (this.config.useStyling) {
      this.printer.print(this.colored(node.getSource(), this.config.propValueStringColor));
    }
    else {
      this.printer.print("\"" + node.getSource() + "\"");
    }
  }
  
  ///////////////////////////////////////////////////////////////////////////
  // Auxiliary methods
  ///////////////////////////////////////////////////////////////////////////
  
  /**
   * Auxiliary method used by all visit callbacks handling numbers.
   *
   * @param number number source string
   */
  private void printNumber(String number) {
    if (this.config.useStyling) {
      this.printer.print(this.colored(number, this.config.propValueNumberColor));
    }
    else {
      this.printer.print(number);
    }
  }
  
  /**
   * Auxiliary method used to print JSON colored literals with PlantUML syntax.
   *
   * @param literal literal to color
   * @param color   PlantUML color string
   * @return colored literal
   */
  private String colored(String literal, String color) {
    return "\"<color:" + color + ">" + literal + "\"";
  }
}



