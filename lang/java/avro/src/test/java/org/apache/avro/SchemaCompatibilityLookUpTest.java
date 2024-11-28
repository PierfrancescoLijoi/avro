package org.apache.avro;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class SchemaCompatibilityLookUpTest {
  private Schema schema;
  private Schema.Field field;
  private Schema.Field expectedOutput;
  private boolean isExceptionExpected;

  public enum ParamField {
    NULL, PRESENT, NOT_PRESENT
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[][] {

            { false, Schema.Type.INT, ParamField.NULL },
            { false, Schema.Type.INT, ParamField.PRESENT },
            { false, Schema.Type.INT, ParamField.NOT_PRESENT},

            { false, Schema.Type.RECORD, ParamField.NULL },
            { false, Schema.Type.RECORD, ParamField.PRESENT },
            { false, Schema.Type.RECORD, ParamField.NOT_PRESENT},

            { false, Schema.Type.BOOLEAN, ParamField.NULL },
            { false, Schema.Type.BOOLEAN, ParamField.PRESENT },
            { false, Schema.Type.BOOLEAN, ParamField.NOT_PRESENT},

            { false, Schema.Type.DOUBLE, ParamField.NULL },
            { false, Schema.Type.DOUBLE, ParamField.PRESENT },
            { false, Schema.Type.DOUBLE, ParamField.NOT_PRESENT},

            { false, Schema.Type.STRING, ParamField.NULL },
            { false, Schema.Type.STRING, ParamField.PRESENT },
            { false, Schema.Type.STRING, ParamField.NOT_PRESENT},

            { false, Schema.Type.LONG, ParamField.NULL },
            { false, Schema.Type.LONG, ParamField.PRESENT },
            { false, Schema.Type.LONG, ParamField.NOT_PRESENT},

            { false, Schema.Type.BYTES, ParamField.NULL },
            { false, Schema.Type.BYTES, ParamField.PRESENT },
            { false, Schema.Type.BYTES, ParamField.NOT_PRESENT},

            { false, Schema.Type.FLOAT, ParamField.NULL },
            { false, Schema.Type.FLOAT, ParamField.PRESENT },
            { false, Schema.Type.FLOAT, ParamField.NOT_PRESENT},

            { false, Schema.Type.MAP, ParamField.NULL },
            { false, Schema.Type.MAP, ParamField.PRESENT },
            { false, Schema.Type.MAP, ParamField.NOT_PRESENT},

            { false, Schema.Type.ARRAY, ParamField.NULL },
            { false, Schema.Type.ARRAY, ParamField.PRESENT },
            { false, Schema.Type.ARRAY, ParamField.NOT_PRESENT},

            { false, Schema.Type.UNION, ParamField.NULL },
            { false, Schema.Type.UNION, ParamField.PRESENT },
            { false, Schema.Type.UNION, ParamField.NOT_PRESENT},

            { false, Schema.Type.ENUM, ParamField.NULL },
            { false, Schema.Type.ENUM, ParamField.PRESENT },
            { false, Schema.Type.ENUM, ParamField.NOT_PRESENT},

            { false, Schema.Type.NULL, ParamField.NULL },
            { false, Schema.Type.NULL, ParamField.PRESENT },
            { false, Schema.Type.NULL, ParamField.NOT_PRESENT},


        });
  }


  public void configure(boolean nullable, Schema.Type type, ParamField field) {
    if (nullable) {
      this.schema = null;
    } else {
      Schema valuetype = mock(Schema.class);
      Mockito.when(valuetype.getFullName()).thenReturn("test");
      switch (type) {
      case MAP:
        this.schema = Schema.createMap(valuetype);
        break;
      case FIXED:
        this.schema = Schema.createFixed("name", "doc", "space", 12);
        break;
      case UNION:
        this.schema = Schema.createUnion(valuetype);
        break;
      case ARRAY:
        this.schema = Schema.createArray(valuetype);
        break;
      case ENUM:
        List<String> stringList = new ArrayList<>();
        this.schema = Schema.createEnum("int", "doc", "int", stringList);
        break;
      case RECORD:
        this.schema = Schema.createRecord("INT", "doc", "int", false);
        Schema.Field recordField = new Schema.Field("Valore", this.schema, null, null);
        List<Schema.Field> recordFields = new ArrayList<>();
        recordFields.add(recordField);
        this.schema.setFields(recordFields);
        break;
      default:
        this.schema = Schema.create(type);
        break;
      }
      Schema schemaResultTest = Schema.createRecord("INT", "doc", "int", false);
      List<Schema.Field> list = new ArrayList<>();
      switch (field) {
      case PRESENT:
        this.field = new Schema.Field("Valore", schemaResultTest, null, null);
        this.field.addAlias("Value");
        list.add(this.field);
        schemaResultTest.setFields(list);
        expectedOutput = this.field;
        break;
      case NOT_PRESENT:
        this.field = new Schema.Field("ValoreNoNValido", schemaResultTest, null, null);
        list.add(this.field);
        schemaResultTest.setFields(list);
        expectedOutput = null;
        break;
      case NULL:
        this.field = null;
        expectedOutput = null;
        break;
      }
    }
    if (nullable || type != Schema.Type.RECORD || field == ParamField.NULL)
      this.isExceptionExpected = true;
    else
      this.isExceptionExpected = false;

  }

  public SchemaCompatibilityLookUpTest(boolean nullable, Schema.Type type, ParamField field) {
    configure(nullable, type, field);
  }

  @Test
  public void testLookUpWriterFields() {
    try {
      Schema.Field actual = SchemaCompatibility.lookupWriterField(this.schema, this.field);
      Assert.assertEquals(this.expectedOutput, actual);
    } catch (AssertionError | AvroRuntimeException | NullPointerException e) {
      Assert.assertTrue(this.isExceptionExpected);
    }
  }
}
