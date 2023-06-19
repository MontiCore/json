package de.monticore.lang.json.prettyprint;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._prettyprint.JSONFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class JSONToPlantUML {

    JSONFullPrettyPrinter prettyPrinter;

    public JSONToPlantUML() {
        this.prettyPrinter = new JSONFullPrettyPrinter(new IndentPrinter(), false);
    }

    /**
     * Serializes and pretty-prints the JSON-AST.
     *
     * @param jsonDocument The root node of the input AST
     * @return The pretty-printed JSON-AST as String
     */
    public String printJSONDocument(ASTJSONDocument jsonDocument) {
        String printedModel = "@startjson\n" + this.prettyPrinter.prettyprint(jsonDocument) + "\n@endjson";
        return printedModel;
    }
}



