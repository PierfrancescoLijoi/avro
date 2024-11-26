package org.apache.avro;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SchemaCompatibilityTest {
  private boolean isExceptionExpected;

  private Schema schemaWriter;
  private Schema schemaReader;
  private SchemaCompatibility.SchemaCompatibilityType typeResult;

  public enum SchemaWriterType {
    NULL, INT, STRING, UNION, RECORD
  }

  public enum SchemaReaderType {
    NULL, SAME, NOT_SAME
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {

        // Combinazioni che testano la compatibilità dei vari tipi di schema
        // Combinazione 1: Entrambi gli schemi sono nulli - Si attende una AvroRuntimeException
        { SchemaWriterType.NULL, SchemaReaderType.SAME },
        { SchemaWriterType.INT, SchemaReaderType.NOT_SAME },

        { SchemaWriterType.STRING, SchemaReaderType.NOT_SAME },

        { SchemaWriterType.UNION, SchemaReaderType.NOT_SAME },

        { SchemaWriterType.UNION, SchemaReaderType.NULL },


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
      if (typeReader == SchemaReaderType.SAME) {
        this.schemaReader = Schema.create(Schema.Type.INT);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
      }
      if (typeReader == SchemaReaderType.NOT_SAME) {
        this.schemaReader = Schema.create(Schema.Type.STRING);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      }
      break;
    case STRING:
      this.schemaWriter = Schema.create(Schema.Type.STRING);
      if (typeReader == SchemaReaderType.SAME) {
        this.schemaReader = Schema.create(Schema.Type.STRING);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
      }
      if (typeReader == SchemaReaderType.NOT_SAME) {
        this.schemaReader = Schema.create(Schema.Type.INT);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      }
      break;
    case UNION:
      this.schemaWriter = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.STRING)));
      if (typeReader == SchemaReaderType.SAME) {
        this.schemaReader = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.STRING)));
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
      }
      if (typeReader == SchemaReaderType.NOT_SAME) {
        this.schemaReader = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.BOOLEAN)));
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
      }
      break;
    //case FLOAT:
    //  this.schemaWriter = Schema.create(Schema.Type.FLOAT);
    //  if (typeReader == SchemaReaderType.SAME) {
    //    this.schemaReader = Schema.create(Schema.Type.FLOAT);
    //    this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
    //  }
    //  if (typeReader == SchemaReaderType.NOT_SAME) {
    //    // FLOAT vs DOUBLE deve essere incompatibile
    //    if (this.schemaReader.getType() == Schema.Type.DOUBLE) {
    //      this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
    //    } else {
    //      this.schemaReader = Schema.create(Schema.Type.STRING); // esempio di incompatibilità
    //      this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
    //    }
    //  }
    //  break;
    //case BYTES:
    //  this.schemaWriter = Schema.create(Schema.Type.BYTES);
    //  if (typeReader == SchemaReaderType.SAME) {
    //    this.schemaReader = Schema.create(Schema.Type.BYTES);
    //    this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
    //  }
    //  if (typeReader == SchemaReaderType.NOT_SAME) {
    //    // Bytes vs String sono incompatibili
    //    this.schemaReader = Schema.create(Schema.Type.STRING);
    //    this.typeResult = SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
    //  }
    //  break;
    }
    // Debugging: Stampa le informazioni sui tipi di schema
    System.out.println("SchemaWriterType: " + typeWriter);
    System.out.println("SchemaReaderType: " + typeReader);
    System.out.println("SchemaWriter: " + (schemaWriter != null ? schemaWriter.getType().toString() : "null"));
    System.out.println("SchemaReader: " + (schemaReader != null ? schemaReader.getType().toString() : "null"));
    System.out.println("Expected Exception: " + isExceptionExpected);
    System.out.println("Expected Result: " + typeResult);
  }

  public SchemaCompatibilityTest(SchemaWriterType typeWriter, SchemaReaderType typeReader) {
    configure(typeWriter, typeReader);
  }

  @Test
  public void testcheckReaderWriterCompatibility() {
    try {
      System.out.println("Running test for Writer: " + schemaWriter + " and Reader: " + schemaReader); // Debugging: Mostra gli schemi che vengono testati
      SchemaCompatibility.SchemaPairCompatibility p = SchemaCompatibility
          .checkReaderWriterCompatibility(this.schemaReader, this.schemaWriter);
      Assert.assertEquals("Expected compatibility type to match", p.getType(), this.typeResult);
      Assert.assertFalse("Exception was not expected but occurred", this.isExceptionExpected);
    } catch (Exception e) {
      System.out.println("Caught exception: " + e.getMessage()); // Debugging: Mostra l'eccezione catturata
      Assert.assertTrue("Unexpected exception occurred", this.isExceptionExpected);
    } catch (AssertionError e) {
      System.out.println("Caught AssertionError: " + e.getMessage()); // Debugging: Mostra l'errore di asserzione
      Assert.assertTrue("Unexpected AssertionError occurred", this.isExceptionExpected);
    }
  }
}
