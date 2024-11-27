package org.apache.avro;

import org.junit.Assert;
import org.junit.Test;
import java.util.Collections;

public class CheckReaderWriterCompatibilityTestIT {

  @Test
  public void testCheckReaderWriterCompatibilityWithActualSchemas() {
    // Creazione dello schema di scrittura (SchemaWriter)
    Schema writerSchema = Schema.createRecord("Record", "doc", "namespace", false);
    Schema.Field writerField = new Schema.Field("Valore", Schema.create(Schema.Type.STRING), null, null);
    writerSchema.setFields(Collections.singletonList(writerField));

    // Creazione dello schema di lettura (SchemaReader)
    Schema readerSchema = Schema.createRecord("Record", "doc", "namespace", false);
    Schema.Field readerField = new Schema.Field("Valore", Schema.create(Schema.Type.STRING), null, null);
    readerSchema.setFields(Collections.singletonList(readerField));

    SchemaCompatibility.SchemaPairCompatibility compatibility = SchemaCompatibility.checkReaderWriterCompatibility(readerSchema, writerSchema);

    // Verifica che i due schemi siano compatibili
    Assert.assertEquals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE, compatibility.getType());
  }

  @Test
  public void testIncompatibleReaderWriterCompatibilityWithDifferentFieldTypes() {
    // Creazione dello schema di scrittura (SchemaWriter)
    Schema writerSchema = Schema.createRecord("WriterRecord", "doc", "namespace", false);
    Schema.Field writerField = new Schema.Field("Valore", Schema.create(Schema.Type.STRING), null, null);
    writerSchema.setFields(Collections.singletonList(writerField));

    // Creazione dello schema di lettura (SchemaReader) con tipo diverso
    Schema readerSchema = Schema.createRecord("ReaderRecord", "doc", "namespace", false);
    Schema.Field readerField = new Schema.Field("Valore", Schema.create(Schema.Type.INT), null, null);
    readerSchema.setFields(Collections.singletonList(readerField));

    // Chiamata al metodo di compatibilità
    SchemaCompatibility.SchemaPairCompatibility compatibility = SchemaCompatibility.checkReaderWriterCompatibility(readerSchema, writerSchema);

    // Verifica che i due schemi siano incompatibili
    Assert.assertEquals(SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE, compatibility.getType());
  }
}
