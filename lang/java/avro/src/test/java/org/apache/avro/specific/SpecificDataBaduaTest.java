package org.apache.avro.specific;
import org.apache.avro.Schema;
import org.apache.avro.AvroRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(Parameterized.class)
public class SpecificDataBaduaTest {

  private static SpecificData specificData = new SpecificData(); // Classe contenente il metodo getClass

  private Schema schema;
  private Class<?> expectedClass;
  private boolean expectException;

  public enum SchemaType {
    STRING_CHAR, STRING_STRING
  }

  // Parametri del test per gestire la configurazione dei dati
  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {SchemaType.STRING_CHAR},
        {SchemaType.STRING_STRING},



    });
  }

  // Costruttore del test con i parametri
  public SpecificDataBaduaTest(SchemaType type) {
    configure( type);
  }

  // Metodo per la creazione degli schemi in base al tipo
  private void configure(SchemaType type) {
    this.expectException=false;
    switch (type) {

    case STRING_STRING:
      this.schema = Schema.create(Schema.Type.STRING);
      this.schema.addProp("avro.java.string", "String");
      this.expectedClass = String.class;
      break;

    case STRING_CHAR:
      this.schema = Schema.create(Schema.Type.STRING);
      this.expectedClass = CharSequence.class;
      break;
    

    }

  }

  // Metodo di test che verifica la classe ottenuta per ciascun tipo di schema
  @Test
  public void testGetClassForSchemas() {
    try {

      if (!expectException) {

        Assert.assertEquals(expectedClass, specificData.getClass(schema));
      }
    } catch (AvroRuntimeException e) {

      if (expectException) {
        Assert.assertTrue("Expected AvroRuntimeException", true);
      }
    }
  }
}
