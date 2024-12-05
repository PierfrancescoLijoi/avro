package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.AvroRuntimeException;
import org.junit.Assert;
import org.junit.Test;

public class SpecificDataBoostedTest {

  private static SpecificData specificData = new SpecificData(); // Classe contenente il metodo getClass

  // Parametri del test per gestire la configurazione dei dati
  @Test
  public void testUnionWithNull() {
    // Test per una UNION contenente NULL
    Schema unionSchema = Schema.createUnion(Schema.create(Schema.Type.NULL), Schema.create(Schema.Type.INT));
    Class<?> result = specificData.getClass(unionSchema);
    Assert.assertEquals(Integer.class, result);  // Cambiato da Integer.class a Integer.TYPE
  }

  @Test
  public void testUnionWithoutNull() {
    // Test per una UNION che non contiene NULL
    Schema unionSchema = Schema.createUnion(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.INT));
    Class<?> result = specificData.getClass(unionSchema);
    Assert.assertEquals(Object.class, result);
  }

  @Test
  public void testUnionWithMultipleTypes() {
    // Test per UNION che contiene più di due tipi
    Schema unionSchema = Schema.createUnion(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.INT), Schema.create(Schema.Type.LONG));
    Class<?> result = specificData.getClass(unionSchema);
    Assert.assertEquals(Object.class, result);  // La logica prevede che ritorni Object.class
  }


}
