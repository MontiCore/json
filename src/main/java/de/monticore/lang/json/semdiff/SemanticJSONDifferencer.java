/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff;

import static de.monticore.lang.json.semdiff.exceptions.SemanticJSONDiffError.AMBIGUOUS_PROPERTY;
import static de.monticore.lang.json.semdiff.exceptions.SemanticJSONDiffError.FAILED_TO_PARSE_MODEL;
import static de.monticore.lang.json.semdiff.exceptions.SemanticJSONDiffError.INVALID_JSON_SEM_DIFF_TYPE;
import static de.monticore.lang.json.semdiff.exceptions.SemanticJSONDiffError.UNKNOWN_JSON_VALUE_TYPE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.monticore.lang.json._ast.ASTJSONArray;
import de.monticore.lang.json._ast.ASTJSONBoolean;
import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._ast.ASTJSONNull;
import de.monticore.lang.json._ast.ASTJSONNumber;
import de.monticore.lang.json._ast.ASTJSONObject;
import de.monticore.lang.json._ast.ASTJSONProperty;
import de.monticore.lang.json._ast.ASTJSONString;
import de.monticore.lang.json._ast.ASTJSONValue;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.lang.json.prettyprint.JSONPrettyPrinter;
import de.monticore.lang.json.semdiff.exceptions.SemanticJSONDiffException;
import de.monticore.lang.json.semdiff.messages.DifferentPropertyTypeMessage;
import de.monticore.lang.json.semdiff.messages.DifferentPropertyValueMessage;
import de.monticore.lang.json.semdiff.messages.EquivalentModelsMessage;
import de.monticore.lang.json.semdiff.messages.JSONSemDiffMessage;
import de.monticore.lang.json.semdiff.messages.MissingPropertyMessage;

public class SemanticJSONDifferencer {
  
  private final JSONParser parser = new JSONParser();
  
  private final JSONPrettyPrinter prettyPrinter = new JSONPrettyPrinter();
  
  public List<JSONSemDiffMessage> semDiffJSONArtifacts(String artifactName1, String artifactName2) throws SemanticJSONDiffException {
    ASTJSONDocument d1 = parse(artifactName1), d2 = parse(artifactName2);
    return semDiffASTs(d1, d2);
  }
  
  private ASTJSONDocument parse(String model) throws SemanticJSONDiffException {
    try {
      return parser.parse(model).orElseThrow(() -> new SemanticJSONDiffException(FAILED_TO_PARSE_MODEL, model));
    }
    catch (IOException e) {
      throw new SemanticJSONDiffException(FAILED_TO_PARSE_MODEL, model, e);
    }
  }
  
  public List<JSONSemDiffMessage> semDiffASTs(ASTJSONDocument d1, ASTJSONDocument d2) throws SemanticJSONDiffException {
    return toMessages(semDiffASTs(d1.getJSONValue(), d2.getJSONValue()));
  }
  
  private List<JSONSemDiffMessage> toMessages(List<SemanticJSONDifference> semDiffs) throws SemanticJSONDiffException {
    if (semDiffs.isEmpty()) {
      return Collections.singletonList(new EquivalentModelsMessage(null));
    }
    List<JSONSemDiffMessage> result = new ArrayList<>();
    for (SemanticJSONDifference semDiff : semDiffs) {
      result.add(toMessage(semDiff));
    }
    return result;
  }
  
  private JSONSemDiffMessage toMessage(SemanticJSONDifference diff) throws SemanticJSONDiffException {
    switch (diff.kind) {
      case MISSING_PROPERTY:
        return new MissingPropertyMessage(diff.node, getPropertyName(diff));
      case DIFFERENT_VALUE:
        return new DifferentPropertyValueMessage(diff.node, getPropertyName(diff), diff.value);
      case DIFFERENT_TYPE:
        return new DifferentPropertyTypeMessage(diff.node, getPropertyName(diff), diff.type);
      default:
        throw new SemanticJSONDiffException(INVALID_JSON_SEM_DIFF_TYPE, diff.kind.name());
    }
  }
  
  private static String getPropertyName(SemanticJSONDifference diff) {
    String result = diff.property.getKey();
    if (diff.index != null) {
      result += "[" + diff.index + "]";
    }
    return result;
  }
  
  private List<SemanticJSONDifference> semDiffASTs(ASTJSONValue n1, ASTJSONValue n2) throws SemanticJSONDiffException {
    if (!equalJSONType(n1, n2)) {
      return Collections.singletonList(SemanticJSONDifference.differentType(n1, jsonTypeToString(n1)));
    }
    if (isJSONObject(n1)) {
      return semDiffASTs(toJSONObject(n1), toJSONObject(n2));
    }
    else if (isJSONArray(n1)) {
      return semDiffASTs(toJSONArray(n1), toJSONArray(n2));
    }
    else if (isJSONBoolean(n1)) {
      return semDiffASTs(toJSONBoolean(n1), toJSONBoolean(n2));
    }
    else if (isJSONString(n1)) {
      return semDiffASTs(toJSONString(n1), toJSONString(n2));
    }
    else if (isJSONNumber(n1)) {
      return semDiffASTs(toJSONNumber(n1), toJSONNumber(n2));
    }
    else if (isJSONNull(n1)) {
      // nothing to do here
    }
    throw new SemanticJSONDiffException(UNKNOWN_JSON_VALUE_TYPE, n1.toString());
  }
  
  private List<SemanticJSONDifference> semDiffASTs(ASTJSONObject o1, ASTJSONObject o2) throws SemanticJSONDiffException {
    List<SemanticJSONDifference> result = new ArrayList<>();
    for (ASTJSONProperty p1 : o1.getPropList()) {
      List<ASTJSONProperty> props2 = o2.getProps(p1.getKey());
      if (props2.isEmpty()) {
        result.add(SemanticJSONDifference.missingProperty(o1, p1));
      }
      else if (props2.size() > 1) {
        throw new SemanticJSONDiffException(AMBIGUOUS_PROPERTY, p1.getKey());
      }
      else {
        ASTJSONProperty p2 = props2.get(0);
        List<SemanticJSONDifference> semDiffs = semDiffASTs(p1.getValue(), p2.getValue());
        semDiffs.forEach(e -> {
          if (e.property == null)
            e.property = p1;
        });
        result.addAll(semDiffs);
      }
    }
    return result;
  }
  
  private List<SemanticJSONDifference> semDiffASTs(ASTJSONArray a1, ASTJSONArray a2) throws SemanticJSONDiffException {
    List<SemanticJSONDifference> result = new ArrayList<>();
    for (int i = 0; i < a1.getJSONValuesList().size(); i++) {
      if (i >= a2.getJSONValuesList().size()) {
        result.add(SemanticJSONDifference.missingProperty(a1, i));
      }
      else {
        ASTJSONValue v1 = a1.getJSONValues(i);
        ASTJSONValue v2 = a2.getJSONValues(i);
        
        List<SemanticJSONDifference> semDiffs = semDiffASTs(v1, v2);
        for (SemanticJSONDifference diff : semDiffs) {
          if (diff.index == null && diff.property == null) {
            diff.index = i;
          }
        }
        result.addAll(semDiffs);
      }
    }
    return result;
  }
  
  private List<SemanticJSONDifference> semDiffASTs(ASTJSONBoolean n1, ASTJSONBoolean n2) {
    if (n1.deepEquals(n2)) {
      return Collections.emptyList();
    }
    return Collections.singletonList(SemanticJSONDifference.differentValue(n1, Boolean.toString(n1.getBooleanLiteral().getValue())));
  }
  
  private List<SemanticJSONDifference> semDiffASTs(ASTJSONString n1, ASTJSONString n2) {
    if (n1.deepEquals(n2)) {
      return Collections.emptyList();
    }
    return Collections.singletonList(SemanticJSONDifference.differentValue(n1, n1.getStringLiteral().getValue()));
  }
  
  private List<SemanticJSONDifference> semDiffASTs(ASTJSONNumber n1, ASTJSONNumber n2) {
    if (n1.deepEquals(n2)) {
      return Collections.emptyList();
    }
    return Collections.singletonList(SemanticJSONDifference.differentValue(n1, prettyPrinter.printJSONNumber(n2)));
  }
  
  private static boolean equalJSONType(ASTJSONValue n1, ASTJSONValue n2) {
    return (isJSONObject(n1) && isJSONObject(n2)) || (isJSONArray(n1) && isJSONArray(n2)) || (isJSONBoolean(n1) && isJSONBoolean(n2)) || (isJSONString(n1) && isJSONString(n2)) || (isJSONNumber(n1) && isJSONNumber(n2)) || isJSONNull(n1) && isJSONNull(n2);
  }
  
  private static String jsonTypeToString(ASTJSONValue value) throws SemanticJSONDiffException {
    if (isJSONObject(value)) {
      return "object";
    }
    else if (isJSONArray(value)) {
      return "array";
    }
    else if (isJSONBoolean(value)) {
      return "boolean";
    }
    else if (isJSONString(value)) {
      return "string";
    }
    else if (isJSONNumber(value)) {
      return "number";
    }
    else if (isJSONNull(value)) {
      return "null";
    }
    throw new SemanticJSONDiffException(UNKNOWN_JSON_VALUE_TYPE, value.toString());
  }
  
  private static boolean isJSONObject(ASTJSONValue value) {
    return value instanceof ASTJSONObject;
  }
  
  private static ASTJSONObject toJSONObject(ASTJSONValue value) {
    return (ASTJSONObject) value;
  }
  
  private static boolean isJSONArray(ASTJSONValue value) {
    return value instanceof ASTJSONArray;
  }
  
  private static ASTJSONArray toJSONArray(ASTJSONValue value) {
    return (ASTJSONArray) value;
  }
  
  private static boolean isJSONBoolean(ASTJSONValue value) {
    return value instanceof ASTJSONBoolean;
  }
  
  private static ASTJSONBoolean toJSONBoolean(ASTJSONValue value) {
    return (ASTJSONBoolean) value;
  }
  
  private static boolean isJSONString(ASTJSONValue value) {
    return value instanceof ASTJSONString;
  }
  
  private static ASTJSONString toJSONString(ASTJSONValue value) {
    return (ASTJSONString) value;
  }
  
  private static boolean isJSONNumber(ASTJSONValue value) {
    return value instanceof ASTJSONNumber;
  }
  
  private static ASTJSONNumber toJSONNumber(ASTJSONValue value) {
    return (ASTJSONNumber) value;
  }
  
  private static boolean isJSONNull(ASTJSONValue value) {
    return value instanceof ASTJSONNull;
  }
}
