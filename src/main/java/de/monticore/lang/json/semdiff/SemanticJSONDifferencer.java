package de.monticore.lang.json.semdiff;

import de.monticore.lang.json._ast.*;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.lang.json.prettyprint.JSONPrettyPrinter;
import de.monticore.lang.json.semdiff.exceptions.SemanticJSONDiffException;
import de.monticore.lang.json.semdiff.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.lang.json.semdiff.exceptions.SemanticJSONDiffError.FAILED_TO_PARSE_MODEL;

public class SemanticJSONDifferencer {

  private final JSONParser parser = new JSONParser();

  private final JSONPrettyPrinter prettyPrinter = new JSONPrettyPrinter();

  public List<JSONSemDiffMessage> semDiffJSONArtifacts(String artifactName1, String artifactName2) {
    ASTJSONDocument d1 = parse(artifactName1),
                    d2 = parse(artifactName2);
    return semDiffASTs(d1, d2);
  }

  private ASTJSONDocument parse(String model) {
    try {
      return parser.parse(model).orElseThrow(() -> new SemanticJSONDiffException(FAILED_TO_PARSE_MODEL));
    } catch (IOException e) {
      throw new SemanticJSONDiffException(FAILED_TO_PARSE_MODEL, e);
    }
  }

  public List<JSONSemDiffMessage> semDiffASTs(ASTJSONDocument d1, ASTJSONDocument d2) {
    return toMessages(semDiffASTs(d1.getJSONValue(), d2.getJSONValue()));
  }

  private List<JSONSemDiffMessage> toMessages(List<SemanticJSONDifference> semDiffs) {
    if (semDiffs.isEmpty()) {
      return Collections.singletonList(new EquivalentModelsMessage(null));
    }
    return semDiffs.stream().map(this::toMessage).collect(Collectors.toList());
  }

  private JSONSemDiffMessage toMessage(SemanticJSONDifference diff) {
    switch (diff.kind) {
      case MISSING_PROPERTY: return new MissingPropertyMessage(diff.node, getPropertyName(diff));
      case DIFFERENT_VALUE: return new DifferentPropertyValueMessage(diff.node, getPropertyName(diff), diff.value);
      case DIFFERENT_TYPE: return new DifferentPropertyTypeMessage(diff.node, getPropertyName(diff), diff.type);
      default: throw new IllegalStateException("Invalid semantic difference type");
    }
  }

  private static String getPropertyName(SemanticJSONDifference diff) {
    String result = diff.property.getKey();
    if (diff.index != null) {
      result += "[" + diff.index + "]";
    }
    return result;
  }

  private List<SemanticJSONDifference> semDiffASTs(ASTJSONValue n1, ASTJSONValue n2) {
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
    throw new IllegalArgumentException("Not valid JSON value");
  }

  private List<SemanticJSONDifference> semDiffASTs(ASTJSONObject o1, ASTJSONObject o2) {
    List<SemanticJSONDifference> result = new ArrayList<>();
    for (ASTJSONProperty p1 : o1.getPropList()) {
      List<ASTJSONProperty> props2 = o2.getProps(p1.getKey());
      if (props2.isEmpty()) {
        result.add(SemanticJSONDifference.missingProperty(o1, p1));
      }
      else if (props2.size() > 1) {
        // TODO
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

  private List<SemanticJSONDifference> semDiffASTs(ASTJSONArray a1, ASTJSONArray a2) {
    List<SemanticJSONDifference> result = new ArrayList<>();
    for (int i = 0; i < a1.getJSONValueList().size(); i++) {
      if (i >= a2.getJSONValueList().size()) {
        result.add(SemanticJSONDifference.missingProperty(a1, i));
        //TODO: Spielt die Reihenfolge einer List in JSON eine Rolle?
        // sind z.B. diese Listen semantisch äquivalent: ["A", "B"], ["B", "A"]
      }
      else {
        ASTJSONValue v1 = a1.getJSONValue(i);
        ASTJSONValue v2 = a2.getJSONValue(i);

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
    return Collections.singletonList(SemanticJSONDifference.differentValue(n1, n1.toString())); // TODO get String from number
  }

  private static boolean equalJSONType(ASTJSONValue n1, ASTJSONValue n2) {
    return (isJSONObject(n1) && isJSONObject(n2)) ||
            (isJSONArray(n1) && isJSONArray(n2)) ||
            (isJSONBoolean(n1) && isJSONBoolean(n2)) ||
            (isJSONString(n1) && isJSONString(n2)) ||
            (isJSONNumber(n1) && isJSONNumber(n2)) ||
            isJSONNull(n1) && isJSONNull(n2);
  }

  private static String jsonTypeToString(ASTJSONValue value) {
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
    // TODO
    throw new SemanticJSONDiffException(null);
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
