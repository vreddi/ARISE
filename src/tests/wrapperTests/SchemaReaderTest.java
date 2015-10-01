package tests.wrapperTests;

import junit.framework.TestCase;
import net.sf.json.xml.XMLSerializer;

public class SchemaReaderTest extends TestCase {

    public void testGetSchemaObject() throws Exception {
//        SchemaReader reader = new SchemaReader("src/tests/wrapperTests/testData/publicationSchema.tsv");
//        SchemaObj obj = reader.getSchemaObject();
//        obj.printFields();
        XMLSerializer xml = new XMLSerializer();
        xml.read("");
    }
}