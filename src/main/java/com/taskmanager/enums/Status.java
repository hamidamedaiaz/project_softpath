package com.taskmanager.enums;

public enum Status {
    TODO("À faire"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminée");

    private final String displayName;




    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}