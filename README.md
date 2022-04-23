<!-- (c) https://github.com/MontiCore/monticore -->
# JSON

The MontiCore language JSON defines the parsing and processing infrastructure 
for JSON artifacts. The language component (and full language) is part of the
MontiCore language library.

Please note that JSON (like XML or ASCII) is just a carrier language.
The concrete JSON dialect and the question, how to recreate the real
objects / data structures, etc., behind the JSON tree structure
is beyond this grammar but can be applied to the AST defined here.

* Main grammar
  [`de.monticore.lang.JSON.mc4`](src/main/grammars/de/monticore/lang/JSON.mc4).


## Functionality

### Parsing JSON artifacts and pretty printing.
* available 
  ([see language explanation](src/main/grammars/de/monticore/lang/json.md))
  

## Further Links

* [JSON grammar](src/main/grammars/de/monticore/lang/JSON.mc4)

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
