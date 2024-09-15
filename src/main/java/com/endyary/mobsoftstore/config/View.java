package com.endyary.mobsoftstore.config;

/**
 * Available Views definition
 */
public enum View {
    HOME("home"),
    APP_DETAILS("appdetails"),
    NEW_APP("newapp"),
    LOGIN("login");

    private final String value;

    View(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
