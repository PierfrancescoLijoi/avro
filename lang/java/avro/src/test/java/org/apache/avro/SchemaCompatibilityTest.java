package org.apache.avro;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class SchemaCompatibilityTest {
  private boolean isExceptionExpected;

  private Schema schemaWriter;
  private Schema schemaReader;
  private SchemaCompatibility.SchemaCompatibilityType typeResult;

  public enum SchemaWriterType {
    NULL, BOOLEAN, INT, LONG, FLOAT, DOUBLE, BYTES, STRING, ARRAY, MAP, FIXED, ENUM, RECORD, UNION
  }

  public enum SchemaReaderType {
    NULL, NOT_SAME
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {

        { SchemaWriterType.NULL, SchemaReaderType.NULL },
        { SchemaWriterType.INT, SchemaReaderType.NOT_SAME },
        { SchemaWriterType.STRING, SchemaReaderType.NOT_SAME },
        { SchemaWriterType.BOOLEAN, SchemaReaderType.NOT_SAME },
        { SchemaWriterType.LONG, SchemaReaderType.NOT_SAME },
        { SchemaWriterType.FLOAT, SchemaReaderType.NOT_SAME },
        { SchemaWriterType.DOUBLE, SchemaReaderType.NOT_SAME },
        { SchemaWriterType.BYTES, SchemaReaderType.NOT_SAME }



    });
  }

  public void configure(SchemaWriterType typeWriter, SchemaReaderType typeReader) {
    this.isExceptionExpected = false;
    if (typeReader == SchemaReaderType.NULL) {
      this.schemaReader = null;
      this.isExceptionExpected = true;
    }
    switch (typeWriter) {
    case NULL:
      this.schemaWriter = null;
      this.isExceptionExpected = true;
      this.schemaReader = null;
      break;
    case INT:
        this.schemaWriter = Schema.create(Schema.Type.INT);
        this.schemaReader = Schema.createMap(Schema.create(Schema.Type.STRING));
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      break;
    case STRING:
        this.schemaWriter = Schema.create(Schema.Type.STRING);
        this.schemaReader = Schema.create(Schema.Type.INT);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      break;
    case BOOLEAN:
        this.schemaWriter = Schema.create(Schema.Type.BOOLEAN);
        this.schemaReader = Schema.createArray(Schema.create(Schema.Type.INT));
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      break;
    case LONG:
        this.schemaWriter = Schema.create(Schema.Type.LONG);
        this.schemaReader = Schema.createFixed("Fixed", null, null, 32);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      break;
    case FLOAT:
        this.schemaWriter = Schema.create(Schema.Type.FLOAT);
      this.schemaReader = Schema.createEnum("EnumName", null, null, Arrays.asList("A", "B", "C"));
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;

      break;
    case DOUBLE:
        this.schemaWriter = Schema.create(Schema.Type.DOUBLE);
        this.schemaReader = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.BOOLEAN)));
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      break;
    case BYTES:
        this.schemaWriter = Schema.create(Schema.Type.BYTES);
        this.schemaReader = Schema.createArray(Schema.create(Schema.Type.STRING));
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      break;

    }
  }

  public SchemaCompatibilityTest(SchemaWriterType typeWriter, SchemaReaderType typeReader) {
    configure(typeWriter, typeReader);
  }

  @Test
  public void testCheckReaderWriterCompatibility() {
    try {
      // Logging iniziale per la configurazione
      System.out.println("Testing with configuration:");
      System.out.println("SchemaWriter: " + (schemaWriter == null ? "null" : schemaWriter.toString()));
      System.out.println("SchemaReader: " + (schemaReader == null ? "null" : schemaReader.toString()));
      System.out.println("Expected Exception: " + isExceptionExpected);
      System.out.println("Expected Compatibility Type: " + typeResult);

      // Gestione specifica per configurazioni null
      if (schemaWriter == null && schemaReader == null) {
        Assert.assertTrue("Exception expected for null, null configuration", isExceptionExpected);
        return; // Non eseguire ulteriori verifiche
      }

      // Esegui il controllo di compatibilità
      SchemaCompatibility.SchemaPairCompatibility compatibility = SchemaCompatibility
          .checkReaderWriterCompatibility(schemaReader, schemaWriter);

      // Logging dei risultati
      System.out.println("Actual Compatibility Type: " + compatibility.getType());
      System.out.println("Compatibility Description: " + compatibility.getDescription());

      // Asserzioni
      Assert.assertEquals("Expected compatibility type to match", typeResult, compatibility.getType());
      Assert.assertFalse("Exception was not expected but occurred", isExceptionExpected);

    } catch (Exception e) {
      // Logging dettagliato dell'eccezione
      System.out.println("Caught Exception: " + e.getMessage());
      e.printStackTrace();

      // Verifica che l'eccezione sia prevista
      Assert.assertTrue("Unexpected exception occurred", isExceptionExpected);
    }
  }


}
