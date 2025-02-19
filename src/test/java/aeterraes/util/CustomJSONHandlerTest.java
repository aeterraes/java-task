package aeterraes.util;

import aeterraes.handler.CustomJSONHandler;
import org.testng.Assert;
import org.testng.annotations.*;
import java.io.*;
import java.util.List;

public class CustomJSONHandlerTest {
    private static final String testData = "test_data";
    private CustomJSONHandler handler;

    @BeforeClass
    public void setUp() throws IOException {
        String initialFile = "{\"addresses\": " +
                "[ { \"domain\": \"first.domain\", \"ip\": \"192.168.0.1\" }, " +
                "{ \"domain\": \"second.domain\", \"ip\": \"192.168.0.2\" } ] }";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testData))) {
            writer.write(initialFile);
        }
        handler = new CustomJSONHandler(testData);
        handler.loadFromFile();
    }

    @AfterClass
    public void tearDown() {
        new File(testData).delete();
    }

    @Test
    public void testGetDomainByIpSuccess() {
        Assert.assertEquals(handler.getDomainByIp("192.168.0.1"), "first.domain");
    }

    @Test(dependsOnMethods = "testGetDomainByIpSuccess")
    public void testGetIpByDomainSuccess() {
        Assert.assertEquals(handler.getIpByDomain("second.domain"), "192.168.0.2");
    }

    @Test(dependsOnMethods = "testGetIpByDomainSuccess")
    public void testGetDomainByIpNotFound() {
        Assert.assertNull(handler.getDomainByIp("192.168.1.1"));
    }

    @Test(dependsOnMethods = "testGetDomainByIpNotFound")
    public void testGetIpByDomainNotFound() {
        Assert.assertNull(handler.getIpByDomain("nonexistent.org"));
    }

    @Test(dependsOnMethods = "testGetIpByDomainNotFound")
    public void testAddNewPairSuccess() throws IOException {
        Assert.assertTrue(handler.addPair("third.domain", "192.168.0.3"));
        Assert.assertEquals(handler.getIpByDomain("third.domain"), "192.168.0.3");
    }

    @Test(dependsOnMethods = "testAddNewPairSuccess")
    public void testAddExistingPairFails() throws IOException {
        Assert.assertFalse(handler.addPair("first.domain", "192.168.0.1"));
    }

    @Test(dependsOnMethods = "testAddExistingPairFails")
    public void testAddDuplicateDomainFails() throws IOException {
        Assert.assertFalse(handler.addPair("first.domain", "192.168.0.3"));
    }

    @Test(dependsOnMethods = "testAddDuplicateDomainFails")
    public void testAddDuplicateIpFails() throws IOException {
        Assert.assertFalse(handler.addPair("third.domain", "192.168.0.1"));
    }

    @Test(dependsOnMethods = "testAddDuplicateIpFails")
    public void testRemovePairByDomainSuccess() throws IOException {
        Assert.assertTrue(handler.removePairByDomain("first.domain"));
        Assert.assertNull(handler.getIpByDomain("first.domain"));
    }

    @Test(dependsOnMethods = "testRemovePairByDomainSuccess")
    public void testRemovePairByIpSuccess() throws IOException {
        Assert.assertTrue(handler.removePairByIp("192.168.0.2"));
        Assert.assertNull(handler.getDomainByIp("192.168.0.2"));
    }

    @Test(dependsOnMethods = "testRemovePairByIpSuccess")
    public void testRemoveNonexistentDomainFails() throws IOException {
        Assert.assertFalse(handler.removePairByDomain("fake.domain"));
    }

    @Test(dependsOnMethods = "testRemoveNonexistentDomainFails")
    public void testRemoveNonexistentIpFails() throws IOException {
        Assert.assertFalse(handler.removePairByIp("192.168.0.99"));
    }

    @Test(dependsOnMethods = "testRemoveNonexistentIpFails")
    public void testValidIpSuccess() throws IOException {
        Assert.assertTrue(handler.addPair("forth.domain", "192.168.1.100"));
    }

    @Test(dependsOnMethods = "testValidIpSuccess")
    public void testInvalidIpFails() throws IOException {
        Assert.assertFalse(handler.addPair("somanynines.domain", "999.999.999.999"));
    }

    @Test(dependsOnMethods = "testInvalidIpFails")
    public void testExtremeCasesIpValidation() throws IOException {
        Assert.assertFalse(handler.addPair("twoineight.domain", "256.256.256.256"));
        Assert.assertFalse(handler.addPair("tooshort.domain", "192.168.0"));
        Assert.assertFalse(handler.addPair("letters.domain", "abcd"));
        Assert.assertFalse(handler.addPair("trapchar.domain", "192.100.@1.1"));
        Assert.assertFalse(handler.addPair("empty.domain", ""));
        Assert.assertTrue(handler.addPair("simple.domain", "10.0.0.1"));
    }

    @Test(dependsOnMethods = "testExtremeCasesIpValidation")
    public void testLoadFromFile() {
        List<CustomPair<String, String>> data = handler.getData();
        Assert.assertEquals(data.size(), 3);
        Assert.assertEquals(data.get(0).getKey(), "third.domain");
        Assert.assertEquals(data.get(0).getValue(), "192.168.0.3");
    }

    @Test(dependsOnMethods = "testLoadFromFile")
    public void testSortedData() {
        List<CustomPair<String, String>> sortedData = handler.getSortedData();
        Assert.assertEquals(sortedData.get(1).getKey(), "simple.domain");
    }
}

