package com.taskmanager.enums;

public enum Priority {
    LOW("Faible", "#4CAF50"),
    MEDIUM("Moyenne", "#FF9800"),
    HIGH("Élevée", "#F44336");

    private final String displayName;
    private final String color;




    Priority(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}