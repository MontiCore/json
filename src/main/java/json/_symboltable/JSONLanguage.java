/*
 * Copyright (c) 2017, MontiCore. All rights reserved. http://www.se-rwth.de/
 */
package json._symboltable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import de.se_rwth.commons.logging.Log;
import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.se_rwth.commons.SourcePosition;
import de.monticore.symboltable.*;
import de.monticore.ast.ASTCNode;

public class JSONLanguage extends JSONLanguageTOP {
  
  public static final String FILE_ENDING = "json";
  
  public JSONLanguage() {
    super("JSON Language", FILE_ENDING);
  }
  
  protected JSONModelLoader provideModelLoader() {
    return new JSONModelLoader(this);
  }
  
}
