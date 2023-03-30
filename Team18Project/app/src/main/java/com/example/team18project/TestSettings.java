package com.example.team18project;

/**
 * Singleton class that holds data relevant to testing. For personal use, directly modify
 * the values of the fields. For use in unit and intent tests, use
 * setTesting() and setTestAndroidID in the test code
 */
public class TestSettings {
    private static TestSettings instance;
    private boolean isTesting;
    private String testAndroidID;

    private TestSettings() {

    }

    public static TestSettings getInstance() {
        if (instance == null) {
            instance = new TestSettings();
        }
        return instance;
    }

    public static void resetSettings() {
        instance = null;
    }

    public static boolean instanceIsNull() {
        return (instance == null);
    }

    public boolean isTesting() {
        return isTesting;
    }

    public void setTesting(boolean testing) {
        isTesting = testing;
    }

    public String getTestAndroidID() {
        return testAndroidID;
    }

    public void setTestAndroidID(String testAndroidID) {
        this.testAndroidID = testAndroidID;
    }
}
