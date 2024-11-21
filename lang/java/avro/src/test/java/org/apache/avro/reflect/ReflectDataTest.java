package org.apache.avro.reflect;

import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(Parameterized.class)
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

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private ParamType paramType;
  private NamesType namesType;

  // Constructor to receive parameters
  public ReflectDataTest(ParamType paramType, NamesType namesType) {
    this.paramType = paramType;
    this.namesType = namesType;
  }

  // Parameterized data
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { ParamType.NULL, NamesType.NULL },
        { ParamType.VALID, NamesType.NULL },
        { ParamType.VALID, NamesType.EMPTY },
        { ParamType.VALID, NamesType.VALID },
        { ParamType.INVALID, NamesType.INVALID }
    });
  }

  // Configuring test parameters
  private void configure() {
    this.exceptionExpected = false;

    // Configure names map based on NamesType
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

    // Configure type based on ParamType
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

  @Test
  public void createSchemaTest() {
    configure();

    if (exceptionExpected) {
      thrown.expect(Exception.class);
    }

    try {
      ReflectData reflectData = new ReflectData();
      Schema actual = reflectData.createSchema(this.type, this.names);

      // Verify names map
      if (this.names != null) {
        if (this.names.isEmpty()) {
          Assert.assertEquals("La mappa dei nomi dovrebbe essere vuota.", 0, this.names.size());
        } else {
          Assert.assertTrue("La mappa dei nomi dovrebbe avere almeno una voce.", this.names.size() > 0);
        }
      }

      // Fail if an exception was expected but not thrown
      if (this.exceptionExpected) {
        Assert.fail("Ci si aspettava un'eccezione, ma non è stata lanciata.");
      }

      // Verify the generated schema
      Assert.assertEquals(this.expectedSchema, actual);

    } catch (Exception e) {
      // Fail if an exception was not expected
      if (!this.exceptionExpected) {
        Assert.fail("Eccezione imprevista: " + e);
      }
    }
  }
}
