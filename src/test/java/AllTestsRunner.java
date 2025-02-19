import org.testng.TestNG;

public class AllTestsRunner {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{
                aeterraes.client.SFTPClientTest.class,
                aeterraes.util.CustomJSONHandlerTest.class
        });
        testng.run();
    }
}
