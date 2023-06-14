package de.monticore.lang.json.prettyprint;

import de.monticore.lang.json.JSONMill;
import de.monticore.lang.json._visitor.JSONHandler;
import de.monticore.lang.json._visitor.JSONTraverser;
import de.monticore.lang.json._visitor.JSONVisitor2;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class JSONToPlantUMLPrettyPrinter extends IndentPrinter implements JSONVisitor2, MCCommonLiteralsVisitor2, JSONHandler {



    private JSONTraverser traverser;

    public JSONToPlantUMLPrettyPrinter() {
        this.traverser = JSONMill.traverser();
        this.traverser.add4JSON(this);
        //this.traverser.add4MCCommonLiterals(this);
        this.traverser.setJSONHandler(this);
    }

    @Override
    public JSONTraverser getTraverser() {
        return traverser;
    }

    @Override
    public void setTraverser(JSONTraverser traverser) {
        this.traverser = traverser;
    }
}



