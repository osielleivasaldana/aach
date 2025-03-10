package cl.aach.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.aach.models.TestData;

import java.io.File;
import java.io.IOException;

public class TestDataLoader {

    private static TestData testData;

    // Método para cargar datos desde el archivo JSON
    public static TestData loadTestData(String filePath) {
        if (testData == null) { // Cargar datos solo una vez
            ObjectMapper mapper = new ObjectMapper();
            try {
                testData = mapper.readValue(new File(filePath), TestData.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al cargar testData desde " + filePath, e);
            }
        }
        return testData;
    }

    // Sobrecarga para un archivo predeterminado
    public static TestData loadDefaultTestData() {
        return loadTestData("src/test/resources/testData.json");
    }
}
