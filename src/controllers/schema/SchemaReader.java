package controllers.schema;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SchemaReader {

    private BufferedReader fileReader;
    private boolean validReader;
    private String[] headers;

    public SchemaReader(String filePath) {
        try {
            this.fileReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("Error: file at "+filePath+" does not exist.");
            this.validReader = false;
        } finally {
            this.validReader = true;
            readHeader();
        }
    }

    public SchemaReader(BufferedReader reader) {
        this.fileReader = reader;
        this.validReader = true;
        readHeader();
    }

    public boolean isValid() {
        return this.validReader;
    }

    public String[] getHeader() {
        return this.headers;
    }

    public SchemaObj getSchemaObject() {
        try {
            String line;
            SchemaObj schema = new SchemaObj();
            while ((line = this.fileReader.readLine()) != null) {
                String[] fields = line.split("\\t");
                if (fields.length != this.headers.length && fields.length <= 2) {
                    return null;
                } else {
                    String fieldName = "";
                    String isPrimaryKey = "";
                    String dataType = "";
                    for (int i = 0; i < this.headers.length; i++) {
                        if (this.headers[i] == null) {
                            break;
                        } else if (this.headers[i].equals("Field Name")) {
                            fieldName = fields[i];
                        } else if (this.headers[i].equals("Is Primary Key")) {
                            isPrimaryKey = fields[i];
                        } else if (this.headers[i].equals("Syntax")) {
                            dataType = fields[i];
                        }
                    }
                    if (fieldName.length() != 0 && isPrimaryKey.length() != 0 && dataType.length() != 0) {
                        schema.addField(fieldName, isPrimaryKey, dataType);
                    } else {
                        return null;
                    }
                }
            }
            return schema;
        } catch (IOException e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    private void readHeader() {
        //  Assumes to be used at the beginning
        String line = null;
        try {
            line = fileReader.readLine();
        } catch (IOException e) {
            System.out.print(e.getMessage());
            return;
        }
        if (line != null) {
            this.headers = line.split("\\t");
        }
    }
}
