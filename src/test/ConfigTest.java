package test;

import configs.GenericConfig;

import java.io.IOException;

public class ConfigTest {
    public static void main(String[] args) {
        System.out.println("Starting tests...");
        try {
            testConfigInitialization();
            testConfigCreation();
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
        }
        System.out.println("Tests completed.");
    }

    public static void testConfigInitialization() {
        System.out.println("Running testConfigInitialization...");
        GenericConfig config = new GenericConfig();
        if ("GenericConfig".equals(config.getName()) && config.getVersion() == 1) {
            System.out.println("testConfigInitialization passed");
        } else {
            System.out.println("testConfigInitialization failed");
        }
    }

    public static void testConfigCreation() throws IOException {
        System.out.println("Running testConfigCreation...");
        GenericConfig config = new GenericConfig();
        config.setConfFile("/Users/samynehmad/studies/studies_codes/advanced_coding/computational-graph/simple.conf");
        config.create();
        config.close();
        System.out.println("testConfigCreation passed");
    }
}
