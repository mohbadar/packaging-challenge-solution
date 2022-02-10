package eu.unite.challenge.utils;

import com.google.common.io.Files;
import eu.unite.challenge.dataobjects.RecordInstance;
import eu.unite.challenge.exceptions.FileFormatException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static eu.unite.challenge.sources.TestCaseSources.GIVEN_TEST_CASE;
import static eu.unite.challenge.validations.ConstraintRules.*;
import static eu.unite.challenge.validations.ConstraintRules.FILE_ENCODING;
import static org.junit.jupiter.api.Assertions.*;

public class FileParserUtilityTest {
    private static final Logger logger = LoggerFactory.getLogger(FileParserUtility.class);

    public static String setupGoodPath() throws IOException {
        String text = String.join(System.lineSeparator(), GIVEN_TEST_CASE);
        return getTempFile(text);
    }

    @Test
    void testGood() throws IOException {
        String path = setupGoodPath();
        FileParserUtility fileParser = new FileParserUtility(path);
        List<RecordInstance> recordInstances = assertDoesNotThrow(fileParser::parse);
        assertEquals(4, recordInstances.size());
        recordInstances.forEach(s -> logger.trace("{}", s == null ? "null" : s.getMaxWeight()));
    }

    @Test
    void testEmpty() throws IOException {
        String path = getTempFile(null);
        FileParserUtility fileParser = new FileParserUtility(path);
        assertThrows(FileFormatException.class, fileParser::parse);
    }


    private static String getTempFile(String text) throws IOException {
        File f = File.createTempFile("items-file", ".txt");
        f.deleteOnExit();
        if (text != null)
            Files.asCharSink(f, FILE_ENCODING).write(text);
        return f.getAbsolutePath();
    }

    @Test
    void testBadLine() throws IOException {
        String text = String.join(System.lineSeparator(),
                List.of("DUMMY", "8 : (1,15.3,€34)", "DUMMY"));
        String path = getTempFile(text);
        FileParserUtility fileParser = new FileParserUtility(path);
        List<RecordInstance> recordInstances = assertDoesNotThrow(fileParser::parse);
        assertEquals(3, recordInstances.size());
        recordInstances.forEach(s -> logger.trace("{}", s == null ? "null" : s.getMaxWeight()));
    }

    @Test
    void testLargeFile() {
        FileParserUtility fileParser = new FileParserUtility("C:\\pagefile.sys");
        assertThrows(FileFormatException.class, fileParser::parse);
    }

}