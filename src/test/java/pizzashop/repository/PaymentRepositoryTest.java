package pizzashop.repository;

import org.junit.jupiter.api.*;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryTest {

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("payments_test", ".txt");
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void testPath01_fileDoesNotExist() {
        File nonExistent = new File("not_existing_file.txt");
        PaymentRepository repo = new PaymentRepository(nonExistent.getAbsolutePath());
        repo.readPayments();

        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void testPath02_IOExceptionFolderInsteadOfFile() throws IOException {
        File folder = Files.createTempDirectory("not_a_file").toFile();

        PaymentRepository repo = new PaymentRepository(folder.getAbsolutePath());
        repo.readPayments();

        assertTrue(repo.getAll().isEmpty());
        assertTrue(folder.delete());
    }


    @Test
    void testPath03_loopNoEntry() throws IOException {
        Files.writeString(tempFile.toPath(), "");

        PaymentRepository repo = new PaymentRepository(tempFile.getAbsolutePath());
        repo.readPayments();

        List<Payment> payments = repo.getAll();
        assertEquals(0, payments.size());
    }


    @Test
    void testPath03_loopOneEntry() throws IOException {
        Files.writeString(tempFile.toPath(), "1,Cash,12.5\n");

        PaymentRepository repo = new PaymentRepository(tempFile.getAbsolutePath());
        repo.readPayments();

        List<Payment> payments = repo.getAll();
        assertEquals(1, payments.size());
        assertEquals(1, payments.get(0).getTableNumber());
        assertEquals(PaymentType.Cash, payments.get(0).getType());
        assertEquals(12.5, payments.get(0).getAmount());
    }


    @Test
    void testPath03_loopCoverageMultipleLines() throws IOException {
        String content = String.join("\n",
                "1,Cash,10.0",
                "-1,Cash,12.5",
                " ",
                "table,Card,30.0"
        );
        Files.writeString(tempFile.toPath(), content);

        PaymentRepository repo = new PaymentRepository(tempFile.getAbsolutePath());
        repo.readPayments();

        List<Payment> payments = repo.getAll();
        assertEquals(1, payments.size());

        assertEquals(1, payments.get(0).getTableNumber());
    }
}
