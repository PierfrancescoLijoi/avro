package org.apache.avro.reflect;

import org.apache.avro.Schema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ReflectDataTest {

  private Type type;
  private Map<String, Schema> names;
  private Schema expectedSchema;
  private boolean exceptionExpected;


  public enum ParamType {
    NULL, VALID, INVALID
  }


  public enum NamesType {
    NULL, EMPTY, VALID, INVALID
  }


  public static Stream<Object[]> data() {
    return Stream.of(
        new Object[]{ParamType.NULL, NamesType.NULL},
        new Object[]{ParamType.VALID, NamesType.NULL},
        new Object[]{ParamType.VALID, NamesType.EMPTY},
        new Object[]{ParamType.VALID, NamesType.VALID},
        new Object[]{ParamType.INVALID, NamesType.INVALID}
    );
  }


  private void configure(ParamType paramType, NamesType namesType) {
    this.exceptionExpected = false;


    switch (namesType) {
    case NULL:
      this.names = null;
      break;
    case EMPTY:
      this.names = new HashMap<>();
      break;
    case VALID:
      this.names = new HashMap<>();
      Schema schema1 = Schema.create(Schema.Type.INT);
      this.names.put("key", schema1);
      break;
    case INVALID:
      this.names = new HashMap<>();
      this.names.put("invalid", null);
      break;
    }


    switch (paramType) {
    case NULL:
      this.type = null;
      this.exceptionExpected = true;
      break;
    case VALID:
      this.type = Integer.TYPE;
      this.expectedSchema = Schema.create(Schema.Type.INT);
      break;
    case INVALID:
      this.type = Object.class;
      this.exceptionExpected = true;
      break;
    }
  }

  @ParameterizedTest
  @MethodSource("data")
  public void createSchemaTest(ParamType paramType, NamesType namesType) {
    configure(paramType, namesType);

    try {
      ReflectData reflectData = new ReflectData();
      Schema actual = reflectData.createSchema(this.type, this.names);
      if (this.names != null) {

        if (this.names.isEmpty()) {
          Assertions.assertEquals(0, this.names.size(), "Names map should be empty.");
        } else {
          Assertions.assertTrue(this.names.size() > 0, "Names map should have at least one entry.");
        }
      }

      if (this.exceptionExpected) {
        Assertions.fail("Expected exception was not thrown");
      }


      Assertions.assertEquals(this.expectedSchema, actual);

    } catch (Exception e) {

      if (!this.exceptionExpected) {
        Assertions.fail("Unexpected exception: " + e);
      }
    }
  }
}
