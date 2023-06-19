/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.prettyprint;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class JSONToPlantUMLTest {

    @Before
    public void setup() {
        LogStub.init();
        Log.enableFailQuick(false);
    }

    @Test
    public void testBookstore() throws RecognitionException, IOException {
        Path model = Paths.get("src/test/resources/json/prettyprint/bookstore.json");
        JSONParser parser = new JSONParser();

        // parse model
        Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
        assertFalse(parser.hasErrors());
        assertTrue(jsonDoc.isPresent());

        // print PlantUML JSON
        JSONToPlantUML pumlPrinter = new JSONToPlantUML();
        String printedModel = pumlPrinter.printJSONDocument(jsonDoc.get());

        // Assert that is has been printed correctly
        assertNotNull(printedModel);
        assertNotEquals("", printedModel);

        assertTrue(printedModel.startsWith("@startjson"));
        assertTrue(printedModel.endsWith("@endjson"));

        // remove surrounding PlantUML
        String strippedPrintedModel = printedModel.substring(10, printedModel.length() - 8);

        // parse printed model
        Optional<ASTJSONDocument> printedJsonDoc = parser.parse_StringJSONDocument(strippedPrintedModel);
        assertFalse(parser.hasErrors());
        assertTrue(printedJsonDoc.isPresent());

        // Note: original model and printed model is not necessarily the same because of stripped comments
    }
}
