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
    NULL, SAME, NOT_SAME
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
      // Perform the compatibility check
      SchemaCompatibility.SchemaPairCompatibility compatibility = SchemaCompatibility
          .checkReaderWriterCompatibility(this.schemaReader, this.schemaWriter);

      // Print debug information to check what is returned
      System.out.println("Compatibility Type: " + compatibility.getType());
      System.out.println("Compatibility Description: " + compatibility.getDescription());

      // Assert that the compatibility result matches expectations
      Assert.assertEquals("Expected compatibility type to match", this.typeResult, compatibility.getType());
      Assert.assertFalse("Exception was not expected but occurred", this.isExceptionExpected);

    } catch (AssertionError e) {
      // Log the exception details for diagnosis
      System.out.println("Caught AssertionError: " + e.getMessage());
      Assert.assertTrue("Unexpected AssertionError occurred", this.isExceptionExpected);
    }
  }

}
