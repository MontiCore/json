///* (c) https://github.com/MontiCore/monticore */
//package de.monticore.lang.json.prettyprint;
//
//import com.sun.org.apache.xerces.internal.parsers.XMLParser;
//import de.monticore.lang.json._ast.ASTJSONDocument;
//import de.monticore.lang.json._parser.JSONParser;
//import de.monticore.lang.xmllight.XMLLightMill;
//import de.monticore.lang.xmllight._ast.ASTXMLDocument;
//import de.monticore.lang.xmllight._parser.XMLLightParser;
//import de.se_rwth.commons.logging.Log;
//import org.antlr.v4.runtime.RecognitionException;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Optional;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//public class JSONToXMLTest {
//
//	@Test
//	public void testJSONToXML() throws RecognitionException, IOException {
//		Path model = Paths.get("src/test/resources/json/prettyprint/bookstore.json");
//		JSONParser parser = new JSONParser();
//
//		// parse model
//		Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
//
//		// pretty print AST
//		JSONToXML pp = new JSONToXML();
//		String printedModel = pp.printJSONDocument(jsonDoc.get());
//		XMLLightParser parserXML = XMLLightMill.parser();
//		Optional<ASTXMLDocument> doc = parserXML.parse_StringXMLDocument(printedModel);
//
//		assertFalse(parserXML.hasErrors());
//		assertTrue(doc.isPresent());
//	}
//}
//
