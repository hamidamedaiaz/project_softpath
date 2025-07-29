package com.taskmanager.controller;

import com.taskmanager.model.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TaskDialogController implements Initializable {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<Task.Priority> priorityComboBox;
    @FXML
    private ComboBox<Task.Status> statusComboBox;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private Button clearDateButton;
    @FXML
    private VBox additionalInfoBox;
    @FXML
    private Label createdAtLabel;
    @FXML
    private Label completedAtLabel;

    private Task editingTask;
    private boolean editingMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComponents();
    }

    private void initializeComponents() {
        setupPriorityComboBox();
        setupStatusComboBox();
        setDefaultSelections();
        setupValidation();
    }

    private void setupPriorityComboBox() {
        priorityComboBox.getItems().clear();
        for (Task.Priority priority : Task.Priority.values()) {
            priorityComboBox.getItems().add(priority);
        }

        priorityComboBox.setConverter(new StringConverter<Task.Priority>() {
            @Override
            public String toString(Task.Priority priority) {
                return (priority != null) ? priority.getDisplayName() : "";
            }

            @Override
            public Task.Priority fromString(String text) {
                for (Task.Priority priority : Task.Priority.values()) {
                    if (priority.getDisplayName().equals(text)) {
                        return priority;
                    }
                }
                return null;
            }
        });
    }

    private void setupStatusComboBox() {
        statusComboBox.getItems().clear();
        for (Task.Status status : Task.Status.values()) {
            statusComboBox.getItems().add(status);
        }

        statusComboBox.setConverter(new StringConverter<Task.Status>() {
            @Override
            public String toString(Task.Status status) {
                return (status != null) ? status.getDisplayName() : "";
            }

            @Override
            public Task.Status fromString(String text) {
                for (Task.Status status : Task.Status.values()) {
                    if (status.getDisplayName().equals(text)) {
                        return status;
                    }
                }
                return null;
            }
        });
    }

    private void setDefaultSelections() {
        priorityComboBox.setValue(Task.Priority.MEDIUM);
        statusComboBox.setValue(Task.Status.TODO);
    }

    private void setupValidation() {
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            performValidation();
        });
    }

    public void setTask(Task task) {
        this.editingTask = task;
        this.editingMode = (task != null);

        if (editingMode) {
            populateFieldsForEditing();
            displayAdditionalInfo();
        } else {
            prepareForNewTask();
            hideAdditionalInfo();
        }

        performValidation();
    }

    private void populateFieldsForEditing() {
        titleField.setText(editingTask.getTitle());
        descriptionArea.setText(editingTask.getDescription());
        priorityComboBox.setValue(editingTask.getPriority());
        statusComboBox.setValue(editingTask.getStatus());
        dueDatePicker.setValue(editingTask.getDueDate());
    }

    private void displayAdditionalInfo() {
        additionalInfoBox.setVisible(true);

        String createdText = editingTask.getCreatedAt().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        createdAtLabel.setText(createdText);

        String completedText = (editingTask.getCompletedAt() != null) ?
                editingTask.getCompletedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Non terminée";
        completedAtLabel.setText(completedText);
    }

    private void prepareForNewTask() {
        titleField.clear();
        descriptionArea.clear();
        priorityComboBox.setValue(Task.Priority.MEDIUM);
        statusComboBox.setValue(Task.Status.TODO);
        dueDatePicker.setValue(null);
    }

    private void hideAdditionalInfo() {
        additionalInfoBox.setVisible(false);
    }

    public Task getResult() {
        if (!validateForm()) {
            return null;
        }

        Task resultTask;
        if (editingMode) {
            resultTask = editingTask.clone();
        } else {
            resultTask = new Task();
        }

        applyFormDataToTask(resultTask);
        return resultTask;
    }

    private void applyFormDataToTask(Task task) {
        String titleText = titleField.getText();
        if (titleText != null) {
            task.setTitle(titleText.trim());
        }

        String descriptionText = descriptionArea.getText();
        if (descriptionText != null) {
            task.setDescription(descriptionText.trim());
        }

        task.setPriority(priorityComboBox.getValue());
        task.setStatus(statusComboBox.getValue());
        task.setDueDate(dueDatePicker.getValue());
    }

    @FXML
    private void handleClearDate() {
        dueDatePicker.setValue(null);
    }

    private void performValidation() {
        // /// je vais le fair  :)
    }

    private boolean validateForm() {
        String titleText = titleField.getText();
        return titleText != null && !titleText.trim().isEmpty();
    }

}