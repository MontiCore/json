package de.monticore.lang.json._ast;

import java.util.List;
import java.util.stream.Collectors;

public class ASTJSONObject extends ASTJSONObjectTOP {

    public boolean hasProp(String key) {
        return getPropList().stream().anyMatch(p -> key.equals(p.getKey()));
    }

    public List<ASTJSONProperty> getProps(String key) {
        return getPropList().stream().filter(p -> key.equals(p.getKey())).collect(Collectors.toList());
    }
}
