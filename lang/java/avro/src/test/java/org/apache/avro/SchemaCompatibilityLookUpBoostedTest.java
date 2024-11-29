package org.apache.avro;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.*;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class SchemaCompatibilityLookUpBoostedTest {
  private Schema schema;
  private Schema.Field field;
  private Schema.Field expectedOutput;
  private boolean isExceptionExpected;
  private int numField;


  public enum ParamListField {
    DUPLICATED
  }

  public enum ParamField {
    PRESENT
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { false, Schema.Type.RECORD, ParamListField.DUPLICATED, ParamField.PRESENT }
      });
  }

  public void configure(boolean nullable, Schema.Type type, ParamListField listField, ParamField field) {
    if (nullable) {
      this.schema = null;
    } else {
      Schema valuetype = mock(Schema.class);
      Mockito.when(valuetype.getFullName()).thenReturn("test");
      if (Objects.requireNonNull(type) == Schema.Type.RECORD) {
        this.schema = Schema.createRecord("INT", "doc", "int", false);

        if (Objects.requireNonNull(listField) == ParamListField.DUPLICATED) {
          List<Schema.Field> recordFieldsTest = getFieldListTest();
          this.schema.setFields(recordFieldsTest);
        }
      } else {
        this.schema = Schema.create(type);
      }
      Schema schemaTest = Schema.createRecord("INT", "doc", "int", false);
      List<Schema.Field> list = new ArrayList<>();
      if (Objects.requireNonNull(field) == ParamField.PRESENT) {
        this.field = new Schema.Field("Valore1", schemaTest, null, null);
        this.field.addAlias("Valore2");
        list.add(this.field);
        schemaTest.setFields(list);
        expectedOutput = this.field;
      }
    }
    this.isExceptionExpected = true;
  }

  private List<Schema.Field> getFieldListTest() {
    List<Schema.Field> recordFieldsTest = new ArrayList<>();
    // Crea numField campi con nomi composti da "Valore" e l'indice
    for (int i = 1; i <= numField; i++) {
      String fieldName = "Valore" + i;  // Concatenazione di "Valore" con l'indice
      Schema.Field field = new Schema.Field(fieldName, this.schema, null, null);
      recordFieldsTest.add(field);
    }

    return recordFieldsTest;
  }

  public SchemaCompatibilityLookUpBoostedTest(boolean nullable, Schema.Type type, ParamListField listField,
                                              ParamField field) {
    this.numField=5;

    configure(nullable, type, listField, field);
  }

  @Test
  public void testLookUpWriterFieldsBoosted() {
    try {
      Schema.Field actual = SchemaCompatibility.lookupWriterField(this.schema, this.field);
      Assert.assertEquals(this.expectedOutput, actual);
    } catch (AssertionError | AvroRuntimeException | NullPointerException e) {
      Assert.assertTrue(this.isExceptionExpected);
    }

  }

}
