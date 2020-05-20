package de.monticore.lang.json.semdiff;

import de.monticore.lang.json._ast.ASTJSONNode;
import de.monticore.lang.json._ast.ASTJSONProperty;

class SemanticJSONDifference {

    enum KIND {
        MISSING_PROPERTY,
        DIFFERENT_VALUE,
        DIFFERENT_TYPE
    }

    final KIND kind;

    ASTJSONNode node;

    ASTJSONProperty property;

    Integer index;

    String value;

    String type;

    private SemanticJSONDifference(KIND kind, ASTJSONNode node, ASTJSONProperty property, Integer index, String value, String type) {
        this.kind = kind;
        this.node = node;
        this.property = property;
        this.index = index;
        this.value = value;
        this.type = type;
    }

    public static SemanticJSONDifference missingProperty(ASTJSONNode node, ASTJSONProperty property) {
        return new SemanticJSONDifference(KIND.MISSING_PROPERTY, node, property, null, null, null);
    }

    public static SemanticJSONDifference missingProperty(ASTJSONNode node, Integer index) {
        return new SemanticJSONDifference(KIND.MISSING_PROPERTY, node, null, index, null, null);
    }

    public static SemanticJSONDifference differentValue(ASTJSONNode node, String value) {
        return new SemanticJSONDifference(KIND.DIFFERENT_VALUE, node, null, null, value, null);
    }

    public static SemanticJSONDifference differentType(ASTJSONNode node, String type) {
        return new SemanticJSONDifference(KIND.DIFFERENT_VALUE, node, null, null, null, type);
    }
}
