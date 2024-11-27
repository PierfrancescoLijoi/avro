package org.apache.avro;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import java.util.Collection;

@RunWith(Parameterized.class)
public class SchemaCompatibilityBaduaTest {

  private boolean isExceptionExpected;
  private Schema schemaWriter;
  private Schema schemaReader;
  private SchemaCompatibility.SchemaCompatibilityType typeResult;

  public enum SchemaWriterType {
    NULL, BOOLEAN, INT, LONG, FLOAT, DOUBLE, BYTES, STRING, ARRAY, MAP, FIXED, ENUM, RECORD, UNION
  }

  public enum SchemaReaderType {
    NULL, SAME, NOT_SAME
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        // Test combinations
        { SchemaWriterType.INT, SchemaReaderType.SAME },
        { SchemaWriterType.STRING, SchemaReaderType.SAME },
        { SchemaWriterType.UNION, SchemaReaderType.SAME },
        { SchemaWriterType.BOOLEAN, SchemaReaderType.SAME },
        { SchemaWriterType.INT, SchemaReaderType.SAME }
    });
  }

  public SchemaCompatibilityBaduaTest(SchemaWriterType typeWriter, SchemaReaderType typeReader) {
    configure(typeWriter, typeReader);
  }

  private void configure(SchemaWriterType typeWriter, SchemaReaderType typeReader) {
    this.isExceptionExpected = false;
    if (typeReader == SchemaReaderType.NULL) {
      this.schemaReader = null;
      this.isExceptionExpected = true;
    }
    switch (typeWriter) {
    case NULL:
        this.schemaWriter = null;
        this.schemaReader = null;
        this.isExceptionExpected = true;
      break;
    case INT:
        this.schemaWriter = Schema.create(Schema.Type.INT);
        this.schemaReader = Schema.create(Schema.Type.INT);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
      break;
    case STRING:
        this.schemaWriter = Schema.create(Schema.Type.STRING);
        this.schemaReader = Schema.create(Schema.Type.STRING);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
      break;
    case UNION:
        this.schemaWriter = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.STRING)));
        this.schemaReader = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.STRING)));
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;

      break;
    case BOOLEAN:
        this.schemaWriter = Schema.create(Schema.Type.BOOLEAN);
        this.schemaReader = Schema.create(Schema.Type.BOOLEAN);
        this.typeResult = SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
      break;
    default:
      throw new IllegalArgumentException("Unsupported schema writer type: " + typeWriter);
    }
  }

  @Test
  public void testcheckReaderWriterCompatibilityBadua() {
    try {
      // Perform the compatibility check
      SchemaCompatibility.SchemaPairCompatibility compatibility = SchemaCompatibility
          .checkReaderWriterCompatibility(this.schemaReader, this.schemaWriter);

      // Validate that the compatibility result matches expectations
      Assert.assertEquals("Expected compatibility type to match",
          this.typeResult, compatibility.getType());

      // Ensure no unexpected exception was thrown
      Assert.assertFalse("Exception was not expected but occurred", this.isExceptionExpected);

    } catch (Exception e) {
      System.out.println("Caught exception: " + e.getMessage());
      Assert.assertTrue("Unexpected exception occurred", this.isExceptionExpected);
    } catch (AssertionError e) {
      System.out.println("Caught AssertionError: " + e.getMessage());
      Assert.assertTrue("Unexpected AssertionError occurred", this.isExceptionExpected);
    }
  }
}
