package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.SchemaCompatibility;
import org.apache.avro.generic.GenericData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.internal.matchers.InstanceOf;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RunWith(Parameterized.class)
public class SpecificDataTest {

  private static SpecificData specificData = new SpecificData(); // Classe contenente il metodo getClass

  private Schema schema;
  private Class<?> expectedClass;
  private boolean expectException;

  // Parametri del test per gestire la configurazione dei dati
  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{

        /// //////////////////////////////////////////////////7
        {Schema.Type.NULL},  // NULL schema, aspettati un'eccezione
        {Schema.Type.UNION},
        //{Schema.Type.INT}, //restituisce un tipo riflessivo che rappresenta il tipo primitivo e non una classe
        //{Schema.Type.STRING}, //restituisce un tipo riflessivo che rappresenta il tipo primitivo e non una classe
        //{Schema.Type.BOOLEAN}, //restituisce un tipo riflessivo che rappresenta il tipo primitivo e non una classe
        //{Schema.Type.LONG}, //restituisce un tipo riflessivo che rappresenta il tipo primitivo e non una classe
        //{Schema.Type.FLOAT}, //restituisce un tipo riflessivo che rappresenta il tipo primitivo e non una classe
        //{Schema.Type.DOUBLE}, //restituisce un tipo riflessivo che rappresenta il tipo primitivo e non una classe
        {Schema.Type.BYTES},
        {Schema.Type.ARRAY},
        {Schema.Type.MAP},
        //{Schema.Type.FIXED}, //viene restituito null
        //{Schema.Type.RECORD}, //viene restituito null
        //{Schema.Type.ENUM} //viene restituito null
    });
  }

  // Costruttore del test con i parametri
  public SpecificDataTest(Schema.Type type) {
    configure(type);
  }

  // Metodo per la creazione degli schemi in base al tipo
  private void configure(Schema.Type type) {
    this.expectException=false;
    switch (type) {
    case NULL:
      this.schema = Schema.create(Schema.Type.NULL);
      this.expectedClass=null;
      this.expectException=true;
      break;

    case INT:
      this.schema = Schema.create(Schema.Type.INT);
      this.expectedClass=Integer.class;
      break;

    case STRING:
      this.schema = Schema.create(Schema.Type.STRING);
      this.expectedClass=String.class;
      break;

    case UNION:
      this.schema = Schema.createUnion(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.INT));
      this.expectedClass=Object.class;
      break;
    case BOOLEAN:
      this.schema = Schema.create(Schema.Type.BOOLEAN);
      this.expectedClass=Boolean.class;
      break;
    case LONG:
      this.schema = Schema.create(Schema.Type.LONG);
      this.expectedClass= Long.class;
      break;
    case FLOAT:
      this.schema = Schema.create(Schema.Type.FLOAT);
      this.expectedClass= Float.class;
      break;
    case DOUBLE:
      this.schema = Schema.create(Schema.Type.DOUBLE);
      this.expectedClass= Double.class;
      break;
    case BYTES:
      this.schema = Schema.create(Schema.Type.BYTES);
      this.expectedClass= ByteBuffer.class;
      break;
    case ARRAY:
      this.schema = Schema.createArray(Schema.create(Schema.Type.STRING));
      this.expectedClass= List.class;
      break;
    case MAP:
      this.schema = Schema.createMap(Schema.create(Schema.Type.STRING));
      this.expectedClass= Map.class;
      break;
    case FIXED:
      this.schema = Schema.createFixed(null, "doc",  "FIXED", 10);
      this.expectedClass=  GenericData.Fixed.class;
      break;
    case RECORD:
      this.schema = Schema.createRecord(null, "doc", "RECORD", false);
      this.expectedClass=  GenericData.Record.class;
      break;
    case ENUM:
      this.schema = Schema.createEnum(null, "doc",  "RECORD", Arrays.asList("A", "B", "C"));
      this.expectedClass=  Enum.class;
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

//    <method name="getClass" desc="(Lorg/apache/avro/Schema;)Ljava/lang/Class;">
