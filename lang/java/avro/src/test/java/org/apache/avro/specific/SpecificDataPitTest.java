package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;



public class SpecificDataPitTest {

  private static SpecificData specificData = new SpecificData(); // Classe contenente il metodo getClass

  @Test
  public void test1Enum() {
    // Crea uno schema ENUM con il nome completo e i valori "A", "B" e "C"
    Schema enumSchema = Schema.createEnum(null, "doc", null, Arrays.asList("A", "B", "C"));

    // Ottieni la classe usando il metodo getClass
    Class<?> result = specificData.getClass(enumSchema);

    // Verifica se la classe restituita è di tipo Enum
    Assert.assertEquals(null, result);
  }

  @Test
  public void test2Enum() {
    // Crea uno schema ENUM con il nome completo e i valori "A", "B" e "C"
    Schema enumSchema = Schema.createEnum("enum", "doc", null, Arrays.asList("A", "B", "C"));

    // Ottieni la classe usando il metodo getClass
    Class<?> result = specificData.getClass(enumSchema);

    // Verifica se la classe restituita è di tipo Enum
    Assert.assertEquals(null, result);
  }


}




