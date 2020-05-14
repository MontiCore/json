package de.monticore.lang.json.semdiff;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.lang.json.semdiff.messages.JSONSemDiffMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SemanticJSONDifferencer {

  public static Optional<List<JSONSemDiffMessage>> semDiffJSONArtifacts(String artifactName1, String artifactName2) throws IOException {
    JSONParser parser = new JSONParser();
    Optional<ASTJSONDocument> d1 = parser.parse(artifactName1);
    Optional<ASTJSONDocument> d2 = parser.parse(artifactName2);

    if(d1.isPresent() && d2.isPresent()) {
      return Optional.of(semDiffASTs(d1.get(), d2.get()));
    }
    return Optional.empty();
  }

  public static List<JSONSemDiffMessage> semDiffASTs(ASTJSONDocument d1, ASTJSONDocument d2) {
    List<JSONSemDiffMessage> res = new ArrayList<>();

    // DO THE DFS

    return res;
  }
}
