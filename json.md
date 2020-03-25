# JSON

MontiCore language for parsing JSON artifacts.


# JSON
The MontiCore language for parsing JSON artifacts contains the grammar:
- **JSON**: basic json language with symbol table definition 

The main pupose of this language is parsing general artifacts in JSON format
that adhere to the common standard.

The grammar file is [`JSON.mc4`][JSONGrammar].

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
