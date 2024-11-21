package org.apache.avro.reflect;

import org.apache.avro.Schema;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ReflectDataTest {

  private ReflectData reflectData;

  @Before
  public void setUp() {
    reflectData = new ReflectData();
  }

  // Test caso 1: Type è null e names è null
  @Test
  public void testCreateSchemaWithNullTypeAndNullNames() {
    Type type = null;
    Map<String, Schema> names = null;

    try {
      reflectData.createSchema(type, names);
      Assert.fail("Ci si aspettava un'eccezione NullPointerException.");
    } catch (NullPointerException e) {
      // Questo è il comportamento atteso, quindi non fare nulla.
    }
  }

  // Test caso 2: Type è valido (es. Integer.TYPE) e Map è valida (es. mappa con una chiave-valore)
  @Test
  public void testCreateSchemaWithValidTypeAndValidMap() {
    Type type = Integer.TYPE; // Type valido
    Map<String, Schema> names = new HashMap<>();
    names.put("validSchema", Schema.create(Schema.Type.INT)); // Mappa valida con un elemento

    // Eseguiamo la creazione dello schema
    Schema result = reflectData.createSchema(type, names);

    // Assert che lo schema creato sia di tipo INT e la mappa non è cambiata
    Assert.assertEquals(Schema.Type.INT, result.getType());
    Assert.assertTrue(names.isEmpty()); // Le mappe non dovrebbero essere modificate per i tipi primitivi
  }

  // Test caso 3: Type è valido (es. Integer.TYPE) e Map è vuota
  @Test
  public void testCreateSchemaWithValidTypeAndEmptyMap() {
    Type type = Integer.TYPE; // Type valido
    Map<String, Schema> names = new HashMap<>(); // Mappa vuota

    // Eseguiamo la creazione dello schema
    Schema result = reflectData.createSchema(type, names);

    // Assert che lo schema creato sia di tipo INT
    Assert.assertEquals(Schema.Type.INT, result.getType());
    Assert.assertTrue(names.isEmpty()); // La mappa vuota non dovrebbe essere modificata
  }

}
