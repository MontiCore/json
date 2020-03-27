/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._symboltable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.json._symboltable.JSONLanguage;
import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;

public class JSONSymboltableTest {
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/symboltable/bookstore.json");
    JSONParser parser = new JSONParser();
    
    // parse model
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // build symbol table
    final JSONLanguage lang = JSONSymTabMill.jSONLanguageBuilder().build();
    JSONArtifactScope scope = createSymbolTable(lang, jsonDoc.get());
    
    // test resolving
    assertTrue(scope.resolveJSONProperty("bookstore").isPresent());
    assertTrue(scope.resolveJSONProperty("bookstore.title").isPresent());
    assertTrue(scope.resolveJSONProperty("bookstore.order.more Books.SpecifiedBook.someNumber").isPresent());
    
    Optional<JSONPropertySymbol> bookOpt = scope.resolveJSONProperty("bookstore.order.more Books.SpecifiedBook");
    assertTrue(bookOpt.isPresent());
    assertTrue(bookOpt.get().getSpannedScope().resolveJSONProperty("Definition").isPresent());
    assertTrue(bookOpt.get().getEnclosingScope().resolveJSONPropertyDown("SpecifiedBook.Definition").isPresent());
  }
  
  
  /**
   * Creates the symbol table from the parsed AST.
   *
   * @param lang The JSON language.
   * @param ast The top JSON model element.
   * @return The artifact scope derived from the parsed AST
   */
  private static JSONArtifactScope createSymbolTable(JSONLanguage lang, ASTJSONDocument ast) {
    JSONGlobalScope globalScope = JSONSymTabMill.jSONGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .setJSONLanguage(lang)
        .build();
    JSONSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }
  
}
