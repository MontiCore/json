package de.monticore.lang.json.semdiff.exceptions;

public class SemanticJSONDiffException extends RuntimeException {

    private final SemanticJSONDiffError error;

    public SemanticJSONDiffException(final SemanticJSONDiffError error) {
        this(error, null);
    }

    public SemanticJSONDiffException(final SemanticJSONDiffError error, Exception e) {
        super(error.name(), e);
        this.error = error;
    }
}
