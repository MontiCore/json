<!-- (c) https://github.com/MontiCore/monticore -->
# JSON

* The MontiCore language for parsing JSON artifacts contains the grammar:
  * **JSON**: json language with symbol table definition 

* The main purpose of this language is parsing general artifacts in JSON format
  that adhere to the common standard.

* The JSON grammar adheres to the common **standard** and allows parsing 
  arbitrary JSON artifacts for further processing.
* Actually the grammar represents a slight superset to the official JSON standard. 
  It is intended for parsing JSON-compliant artifacts. Further well-formedness
  checks are not included, because we assume to parse correctly produced JSON 
  documents only.

* Please note that JSON (like XML or ASCII) is just a carrier language.
  The conrete JSON dialect and the question, how to recreate the
  real objects / data structures, etc. behind the JSON tree structure
  is beyond this grammar, but can be applied to the AST defined here.

* Main grammar [`de.monticore.lang.JSON.mc4`][JSONGrammar].

## Handwritten Extensions

### Symboltable
- The [`de.monticore.lang.json._symboltable.JSONLanguage`][JSONLanguage]
 defines the language name and its file ending. Additionally, it sets the 
 default model loader.

## Functionality
### Structure Extraction
(Under construction) 
Automatically extracts the structure of a set of JSON artifacts and stores it 
as a class diagram adhering to [`CD4Analysis`][CD4Analysis].
  

[JSONGrammar]: https://git.rwth-aachen.de/monticore/languages/json/-/blob/master/src/main/grammars/de/monticore/lang/JSON.mc4
[JSONLanguage]: https://git.rwth-aachen.de/monticore/languages/json/-/blob/master/src/main/java/de/monticore/lang/json/_symboltable/JSONLanguage.java
[CD4Analysis]: https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis



