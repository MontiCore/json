/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.semdiff;

import de.monticore.lang.json.semdiff.exceptions.SemanticJSONDiffException;
import de.monticore.lang.json.semdiff.messages.JSONSemDiffMessage;
import de.monticore.lang.json.semdiff.messages.MissingPropertyMessage;

import java.util.List;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSONSemDiffTest {
  
  private static final String MODEL_PATH = "src/test/resources/json/semdiff/";
  
  private SemanticJSONDifferencer semJsonDiffer;
  
  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    semJsonDiffer = new SemanticJSONDifferencer();
  }
  
  private String getModelPath(String pkg, String artifact) {
    return String.format("%s/%s/%s", MODEL_PATH, pkg, artifact);
  }
  
  private List<JSONSemDiffMessage> run(String pkg, String d1, String d2) {
    try {
      return semJsonDiffer.semDiffJSONArtifacts(getModelPath(pkg, d1), getModelPath(pkg, d2));
    }
    catch (SemanticJSONDiffException e) {
      fail();
    }
    throw new IllegalStateException("Should not reach this point");
  }
  
  /*
   * ===========================================================================
   * ===================== TEST EXAMPLE 1
   * ===========================================================================
   * =====================
   */
  
  @Test
  public void testExample1_diff_d1_d2() {
    // given
    
    // when
    List<JSONSemDiffMessage> result = run("example1", "d1.json", "d2.json");
    
    // then
    assertEquals(1, result.size());
    assertTrue(result.get(0) instanceof MissingPropertyMessage);
    MissingPropertyMessage message = (MissingPropertyMessage) result.get(0);
    assertEquals(
        "Object in document has property 'Alice' missing in other document.", 
        message.getSimpleErrorMessage());
  }
  
  @Test
  public void testExample1_diff_d2_d1() {
    // given
    
    // when
    List<JSONSemDiffMessage> result = run("example1", "d2.json", "d1.json");
    
    // then
    assertEquals(1, result.size());
    assertEquals(
        "Object in document has property 'Bob' missing in other document.", 
        result.get(0).getSimpleErrorMessage());
  }
  
  /*
   * ===========================================================================
   * ===================== TEST EXAMPLE 2
   * ===========================================================================
   * =====================
   */
  
  @Test
  public void testExample2_diff_d1_d2() {
    // given
    
    // when
    List<JSONSemDiffMessage> result = run("example2", "d1.json", "d2.json");
    
    // then
    // result.forEach(m -> System.out.println(m.toString() + "\n\n"));
    assertEquals(8, result.size());
    assertEquals(
        "Object in document has property 'name', which is different on other document.", 
        result.get(0).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'street', which is different on other document.", 
        result.get(1).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'number', which is different on other document.", 
        result.get(2).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'name', which is different on other document.", 
        result.get(3).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'models[0]', which is different on other document.", 
        result.get(4).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'models[1]', which is different on other document.", 
        result.get(5).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'models[2]' missing in other document.", 
        result.get(6).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'cars[1]' missing in other document.", 
        result.get(7).getSimpleErrorMessage());
  }
  
  @Test
  public void testExample2_diff_d2_d1() {
    // given
    
    // when
    List<JSONSemDiffMessage> result = run("example2", "d2.json", "d1.json");
    
    // then
    // result.forEach(m -> System.out.println(m.toString() + "\n\n"));
    assertEquals(6, result.size());
    assertEquals(
        "Object in document has property 'name', which is different on other document.", 
        result.get(0).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'street', which is different on other document.", 
        result.get(1).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'number', which is different on other document.", 
        result.get(2).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'name', which is different on other document.", 
        result.get(3).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'models[0]', which is different on other document.", 
        result.get(4).getSimpleErrorMessage());
    assertEquals(
        "Object in document has property 'models[1]', which is different on other document.", 
        result.get(5).getSimpleErrorMessage());
  }
  
  /*
   * ===========================================================================
   * ===================== TEST EXAMPLE 3
   * ===========================================================================
   * =====================
   */
  
  @Test
  public void testExample3_diff_d1_d2() {
    // given
    
    // when
    List<JSONSemDiffMessage> result = run("example3", "d1.json", "d2.json");
    
    // then
    // result.forEach(m -> System.out.println(m.toString() + "\n\n"));
    assertEquals(1, result.size());
    assertEquals(
        "Object in document has property 'name', which is different on other document.", 
        result.get(0).getSimpleErrorMessage());
  }
  
  @Test
  public void testExample3_diff_d2_d1() {
    // given
    
    // when
    List<JSONSemDiffMessage> result = run("example3", "d2.json", "d1.json");
    
    // then
    // result.forEach(m -> System.out.println(m.toString() + "\n\n"));
    assertEquals(1, result.size());
    assertEquals(
        "Object in document has property 'name', which is different on other document.", 
        result.get(0).getSimpleErrorMessage());
  }
  
  /*
   * ===========================================================================
   * ===================== TEST EXAMPLE 4
   * ===========================================================================
   * =====================
   */
  
  @Test
  public void testExample4_diff_d1_d2() {
    // given
    
    // when
    List<JSONSemDiffMessage> result = run("example4", "d1.json", "d2.json");
    
    // then
    assertEquals(1, result.size());
    assertEquals("Models are equivalent.", result.get(0).getSimpleErrorMessage());
  }
  
  @Test
  public void testExample4_diff_d2_d1() {
    // given
    
    // when
    List<JSONSemDiffMessage> result = run("example4", "d2.json", "d1.json");
    
    // then
    assertEquals(1, result.size());
    assertEquals("Models are equivalent.", result.get(0).getSimpleErrorMessage());
  }
  
}
