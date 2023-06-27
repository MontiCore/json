package de.monticore.lang.json.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.lang.json.JSONMill;
import de.monticore.lang.json._ast.*;
import de.monticore.lang.json._prettyprint.JSONFullPrettyPrinter;
import de.monticore.lang.json._visitor.JSONHandler;
import de.monticore.lang.json._visitor.JSONTraverser;
import de.monticore.lang.json._visitor.JSONVisitor2;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

public class JSONToPlantUML implements JSONVisitor2, MCCommonLiteralsVisitor2, JSONHandler {
    private IndentPrinter printer;
    private JSONTraverser traverser;

    public JSONToPlantUML() {
        this.printer = new IndentPrinter();
        this.traverser = JSONMill.traverser();
        this.traverser.add4JSON(this);
        this.traverser.add4MCCommonLiterals(this);
        this.traverser.setJSONHandler(this);
    }

    public JSONTraverser getTraverser() {
        return this.traverser;
    }

    public void setTraverser(JSONTraverser traverser) {
        this.traverser = traverser;
    }

    /**
     * Serializes and pretty-prints the JSON-AST.
     *
     * @param jsonDocument The root node of the input AST
     * @return The pretty-printed JSON-AST as String
     */
    public String printJSONDocument(ASTJSONDocument jsonDocument) {
        this.printer.clearBuffer();
        this.getTraverser().handle(jsonDocument);
        String puml = printer.getContent();
        return puml;
    }

    @Override
    public void handle(ASTJSONObject node) {
        this.getTraverser().visit(node);
        int length = node.getPropList().size();
        for (int i = 0; i < length; i++) {
            node.getProp(i).accept(this.getTraverser());
            if (i < length - 1) {
                this.printer.println(",");
            } else {
                this.printer.println();
            }
        }
        this.getTraverser().endVisit(node);
    }

    @Override
    public void handle(ASTJSONArray node) {
        this.getTraverser().visit(node);
        int length = node.getJSONValueList().size();
        for (int i = 0; i < length; i++) {
            node.getJSONValue(i).accept(this.getTraverser());
            if (i < length - 1) {
                this.printer.println(",");
            } else {
                this.printer.println();
            }
        }
        this.getTraverser().endVisit(node);
    }

    @Override
    public void visit(ASTJSONDocument node) {
        this.printer.println("@startjson");
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
        this.printer.print(node.getBooleanLiteral().getValue());
    }

    @Override
    public void visit(ASTJSONNull node) {
        this.printer.print("null");
    }

    @Override
    public void visit(ASTJSONProperty node) {
        this.printer.print("\"" + node.getKey() + "\": ");
    }

    @Override
    public void endVisit(ASTJSONProperty node) {

    }

    @Override
    public void visit(ASTSignedBasicDoubleLiteral node) {
        this.printer.print(node.getSource());
    }

    @Override
    public void visit(ASTSignedBasicFloatLiteral node) {
        this.printer.print(node.getSource());
    }

    @Override
    public void visit(ASTSignedBasicLongLiteral node) {
        this.printer.print(node.getSource());
    }

    @Override
    public void visit(ASTSignedNatLiteral node) {
        this.printer.print(node.getSource());
    }

    @Override
    public void visit(ASTStringLiteral node) {
        this.printer.print("\"" + node.getSource() + "\"");
    }

}



