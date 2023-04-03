package com.example.team18project;

/**
 * Singleton class that holds data relevant to testing. For personal use, directly modify
 * the values of the fields. For use in unit and intent tests, use
 * setTesting() and setTestAndroidID() in the test code
 * Note: setting firebaseEnabled to false only turns off writing, not reading. It only needs
 * to be set to false for unit tests though, and I don't think any of the functionality tested
 * in those involves reading, so it should be fine
 */
public class TestSettings {
    private static TestSettings instance;
    private boolean isTesting;
    private boolean firebaseEnabled;
    private String testAndroidID;

    private TestSettings() {
        isTesting = false;
        firebaseEnabled = true;
    }

    /**
     * Gets an instance of TestSettings, or makes a new instance
     * if there isn't one yet
     * @return An instance of TestSettings
     */
    public static TestSettings getInstance() {
        if (instance == null) {
            instance = new TestSettings();
        }
        return instance;
    }

    /**
     * Sets the instance of TestSettings to null
     */
    public static void resetSettings() {
        instance = null;
    }

    /**
     * Checks if the instance of TestSettings is null
     * @return true if the instance is null and false otherwise
     */
    public static boolean instanceIsNull() {
        return (instance == null);
    }

    /**
     * Checks if testing mode is on
     * @return true is testing mode is on and false otherwise
     */
    public boolean isTesting() {
        return isTesting;
    }

    /**
     * Sets whether testing mode is on or not
     * @param testing true is testing mode is on and false otherwise
     */
    public void setTesting(boolean testing) {
        isTesting = testing;
    }

    /**
     * Gets the android ID to be used for testing
     * @return The android ID to be used for testing
     */
    public String getTestAndroidID() {
        return testAndroidID;
    }

    /**
     * Sets the android ID to be used for testing
     * @param testAndroidID The android ID to be used for testing
     */
    public void setTestAndroidID(String testAndroidID) {
        this.testAndroidID = testAndroidID;
    }

    /**
     * Checks whether Firebase should be used or not
     * @return true if Firebase should be used and false otherwise
     */
    public boolean isFirebaseEnabled() {
        return firebaseEnabled;
    }

    /**
     * Sets whether Firebase should be used or not
     * @param firebaseEnabled true if Firebase should be used and false otherwise
     */
    public void setFirebaseEnabled(boolean firebaseEnabled) {
        this.firebaseEnabled = firebaseEnabled;
    }
}
