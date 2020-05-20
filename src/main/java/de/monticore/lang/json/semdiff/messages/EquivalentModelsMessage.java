package de.monticore.lang.json.semdiff.messages;

import de.monticore.lang.json._ast.ASTJSONNode;

public class EquivalentModelsMessage extends JSONSemDiffBaseMessage implements JSONSemDiffMessage {

    public EquivalentModelsMessage(ASTJSONNode ast) {
        super(ast);
    }

    @Override
    public String getSimpleErrorMessage() {
        return "Models are equivalent.";
    }

    @Override
    public String toString() {
        return getSimpleErrorMessage();
    }
}
