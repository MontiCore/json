/*
 * Copyright (c) 2017, MontiCore. All rights reserved. http://www.se-rwth.de/
 */
package de.monticore.lang.json._symboltable;

public class JSONLanguage extends JSONLanguageTOP {
  
  public static final String FILE_ENDING = "json";
  
  public JSONLanguage() {
    super("JSON Language", FILE_ENDING);
  }
  
  protected JSONModelLoader provideModelLoader() {
    return new JSONModelLoader(this);
  }
  
}
