<!-- (c) https://github.com/MontiCore/monticore -->
# JSON Language Description

* The MontiCore language JSON contains the grammar 
  and symbol management infrastructure for parsing and processing 
  JSON artifacts
```json
    {
        "Alice": {
            "name": "Alice Anderson",
            "address": {
                "postal_code": 10459,
                "street": "Beck Street",
                "number": 56
            }
        },
        "Bob": {
            "name": "Bob Barkley",
            "address": {
                "postal_code": 10459,
                "street": "Freeman Street",
                "number": 73
            }
        }
    }
```
* The main purpose of this language is parsing general artifacts in JSON format
  that adhere to the common [standard](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf).
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

* Main grammar [`de.monticore.lang.JSON.mc4`](src/main/grammars/de/monticore/lang/JSON.mc4).


## Handwritten Extensions

### Symboltable
* The JSON artifacts provide symbols of type JSONPropertySymbol. 
* The JSON symbols of artifact `A.json` are stored in `A.jsonsym`.
* Symbol management:
  * JSON artifacts provide a hierarchy of scopes along the objects they define.
  * Each *"attribute name"* (i.e., each property key) acts as a symbol.
  * Symbols are by definition *externally visible* and *exported*. 
    All of them, even deeply nested ones!
  * Therefore, symbol resolving is implemented in a way that even deeply nested 
    names are found by `resolve.DownMany` delivering a set of found symbols.
  * Example: Considering the presented JSON snippet, the default resolving 
    algorithm requires the qualified name `Alice.address.street` to resolve the 
    corresponding symbol.
  * The modified `resolve.DownMany` approach allows resolving for `street` 
    directly, returning a list of all property symbols named "street".

## Further Links 

* [JSON grammar](src/main/grammars/de/monticore/lang/JSON.mc4)
* [Functions for JSON available](./Readme.mc4)
* [CD4Analysis](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis)

