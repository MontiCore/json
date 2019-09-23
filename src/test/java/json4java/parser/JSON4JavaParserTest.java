/*
 * Copyright (c) 2017, MontiCore. All rights reserved. http://www.se-rwth.de/
 */
package json4java.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.io.paths.ModelPath;
import json._ast.ASTJSONDocument;
import json4java._parser.JSON4JavaParser;
import json4java._symboltable.IJSON4JavaScope;
import json4java._symboltable.JSON4JavaArtifactScope;
import json4java._symboltable.JSON4JavaGlobalScope;
import json4java._symboltable.JSON4JavaLanguage;
import json4java._symboltable.JSON4JavaSymbolTableCreatorDelegator;

public class JSON4JavaParserTest {
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json4java/parser/automaton.json");
    JSON4JavaParser parser = new JSON4JavaParser();
    
    // test if model is parsed
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // test symbol table access
    JSON4JavaLanguage lang = new JSON4JavaLanguage();
    JSON4JavaArtifactScope artifactScope = createSymbolTable(lang, jsonDoc.get());
    IJSON4JavaScope s = artifactScope.getSubScopes().stream().findAny().get();
    
    assertTrue(s.resolveJSONProperty("MyAutomaton").isPresent());
    assertTrue(s.resolveJSONProperty("states").isPresent());
    assertTrue(s.resolveJSONProperty("transitions").isPresent());
    assertTrue(s.resolveJSONProperty("MyAutomaton.states.Ping").isPresent());
    assertTrue(artifactScope.resolveJSONProperty("MyAutomaton.transitions.t1").isPresent());
  }
  
  /**
   * Creates the symbol table concerning the JSON4Java language for a given AST.
   * 
   * @param lang The JSON4Java language
   * @param ast The root AST element, for which the symbol table is created
   * @return The artifact scope with respect to the AST element
   */
  private JSON4JavaArtifactScope createSymbolTable(JSON4JavaLanguage lang, ASTJSONDocument ast) {
    JSON4JavaGlobalScope globalScope = new JSON4JavaGlobalScope(new ModelPath(), lang);
    JSON4JavaSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }
}
