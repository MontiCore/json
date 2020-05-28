package de.monticore.lang.json.semdiff.exceptions;

public class SemanticJSONDiffException extends Exception {

    private final SemanticJSONDiffError error;

    public SemanticJSONDiffException(final SemanticJSONDiffError error, String cause) {
        this(error, cause, null);
    }

    public SemanticJSONDiffException(final SemanticJSONDiffError error, String cause, Exception e) {
        super(String.format(error.getErrorMessage(), cause), e);
        this.error = error;
    }

    public SemanticJSONDiffError getError() {
        return error;
    }
}
