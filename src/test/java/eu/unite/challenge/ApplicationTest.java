package eu.unite.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static eu.unite.challenge.utils.FileParserUtilityTest.setupGoodPath;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ApplicationTest {

    @Test
    void main() throws IOException {
        String[] args1 = new String[0];
        assertDoesNotThrow(() -> Application.main(args1));

        String[] args2 = new String[]{""};
        assertThrows(IOException.class, () -> Application.main(args2));

        String[] args3 = new String[]{setupGoodPath()};
        assertDoesNotThrow(() -> Application.main(args3));
    }
}