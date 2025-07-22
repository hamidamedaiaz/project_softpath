package com.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private IntegerProperty id;
    private StringProperty title;
    private StringProperty description;
    private ObjectProperty<Priority> priority;
    private ObjectProperty<Status> status;
    private ObjectProperty<LocalDate> dueDate;
    private ObjectProperty<LocalDateTime> createdAt;
    private ObjectProperty<LocalDateTime> completedAt;

    public enum Priority {
        LOW("Faible", "#4CAF50"),
        MEDIUM("Moyenne", "#FF9800"),
        HIGH("Élevée", "#F44336");

        private String displayName;
        private String color;

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

    public enum Status {
        TODO("À faire"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminée");

        private String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Task() {
        initializeProperties();
        setDefaultValues();
    }

    public Task(String title, String description) {
        initializeProperties();
        this.title.set(title);


        this.description.set(description);
        setDefaultValues();
    }


    private void initializeProperties() {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.priority = new SimpleObjectProperty<>();
        this.status = new SimpleObjectProperty<>();
        this.dueDate = new SimpleObjectProperty<>();
        this.createdAt = new SimpleObjectProperty<>();
        this.completedAt = new SimpleObjectProperty<>();
    }

    private void setDefaultValues() {
        this.id.set(0);
        this.title.set("");
        this.description.set("");
        this.priority.set(Priority.MEDIUM);
        this.status.set(Status.TODO);
        this.createdAt.set(LocalDateTime.now());
    }

    public int getId() {
        return this.id.getValue();
    }

    public void setId(int id) {
        this.id.setValue(id);
    }

    public String getTitle() {
        return this.title.getValue();
    }

    public void setTitle(String title) {
        this.title.setValue(title);
    }

    public String getDescription() {
        return this.description.getValue();
    }

    public void setDescription(String description) {
        this.description.setValue(description);
    }




    public Priority getPriority() {
        return this.priority.getValue();
    }

    public void setPriority(Priority priority) {
        this.priority.setValue(priority);
    }



    public Status getStatus() {
        return this.status.getValue();
    }

    public void setStatus(Status status) {
        Status oldStatus = this.status.getValue();
        this.status.setValue(status);

        if (status == Status.COMPLETED && oldStatus != Status.COMPLETED) {
            this.completedAt.setValue(LocalDateTime.now());
        } else if (status != Status.COMPLETED) {
            this.completedAt.setValue(null);
        }
    }




    public LocalDate getDueDate() {
        return this.dueDate.getValue();
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate.setValue(dueDate);
    }


    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getCreatedAt() {
        return this.createdAt.getValue();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.setValue(createdAt);
    }




    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getCompletedAt() {
        return this.completedAt.getValue();
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt.setValue(completedAt);
    }



    public boolean isOverdue() {
        LocalDate due = getDueDate();
        if (due == null) return false;
        if (getStatus() == Status.COMPLETED) return false;

        LocalDate today = LocalDate.now();
        return due.isBefore(today);
    }

    public boolean isDueToday() {
        LocalDate due = getDueDate();
        if (due == null) return false;
        if (getStatus() == Status.COMPLETED) return false;

        LocalDate today = LocalDate.now();
        return due.isEqual(today);
    }




    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task{");
        sb.append("id=").append(getId());
        sb.append(", title='").append(getTitle()).append('\'');
        sb.append(", priority=").append(getPriority());
        sb.append(", status=").append(getStatus());
        sb.append(", dueDate=").append(getDueDate());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Task other = (Task) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Task clone() {
        Task copy = new Task();
        copy.setId(this.getId());
        copy.setTitle(this.getTitle());
        copy.setDescription(this.getDescription());
        copy.setPriority(this.getPriority());
        copy.setStatus(this.getStatus());
        copy.setDueDate(this.getDueDate());
        copy.setCreatedAt(this.getCreatedAt());
        copy.setCompletedAt(this.getCompletedAt());
        return copy;
    }
}