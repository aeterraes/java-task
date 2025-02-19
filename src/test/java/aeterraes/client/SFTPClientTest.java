package aeterraes.client;

import com.jcraft.jsch.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class SFTPClientTest {
    private SFTPClient sftpClient;

    @BeforeClass
    public void setUp() {
        sftpClient = new SFTPClient("localhost", 22, "test", "password");
    }

    @Test
    public void testConnection() {
        try {
            sftpClient.connect();
            Assert.assertTrue(true, "Подключение успешно");
        } catch (JSchException e) {
            Assert.fail("Ошибка подключения: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods = "testConnection")
    public void testCreateAddressesFile() {
        try {
            sftpClient.createAddressesFileIfNotExists();
            Assert.assertTrue(true, "Файл успешно создан или уже существует");
        } catch (SftpException e) {
            Assert.fail("Ошибка при создании файла: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        sftpClient.disconnect();
    }
}
