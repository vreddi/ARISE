package tests.wrapperTests;

import junit.framework.TestCase;
import controllers.schema.SchemaObj;
import controllers.schema.SchemaReader;

public class SchemaReaderTest extends TestCase {

    public void testGetSchemaObject() throws Exception {
//        SchemaReader reader = new SchemaReader("src/tests/wrapperTests/testData/publicationSchema.tsv");
//        SchemaObj obj = reader.getSchemaObject();
//        obj.printFields();
        Process p = Runtime.getRuntime().exec("java -jar getJSON.jar -first Craig -last Zilles".split(" "));
        p.waitFor();
    }
}