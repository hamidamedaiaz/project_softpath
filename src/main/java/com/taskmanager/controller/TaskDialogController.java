package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Status;
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
    private ComboBox<Priority> priorityComboBox;
    @FXML
    private ComboBox<Status> statusComboBox;
    @FXML
    private DatePicker dueDatePicker;
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
        for (Priority priority : Priority.values()) {
            priorityComboBox.getItems().add(priority);
        }

        priorityComboBox.setConverter(new StringConverter<Priority>() {
            @Override
            public String toString(Priority priority) {

                return (priority != null) ? priority.getDisplayName() : "";
            }

            @Override
            public Priority fromString(String text) {
                for (Priority priority : Priority.values()) {
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
        for (Status status : Status.values()) {
            statusComboBox.getItems().add(status);
        }

        statusComboBox.setConverter(new StringConverter<Status>() {
            @Override
            public String toString(Status status) {
                return (status != null) ? status.getDisplayName() : "";
            }

            @Override
            public Status fromString(String text) {
                for (Status status : Status.values()) {
                    if (status.getDisplayName().equals(text)) {
                        return status;
                    }
                }
                return null;
            }
        });
    }

    private void setDefaultSelections() {
        priorityComboBox.setValue(Priority.MEDIUM);
        statusComboBox.setValue(Status.TODO);
    }

    private void setupValidation() {
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            performValidation();});

        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            performValidation();

        });


          priorityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            performValidation();
        });

        statusComboBox.valueProperty().addListener((observable, oldValue, newValue) ->

        {
            performValidation();});
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
        priorityComboBox.setValue(Priority.MEDIUM);
        statusComboBox.setValue(Status.TODO);
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


         DialogPane dialogPane = titleField.getScene() != null ?
                (DialogPane) titleField.getScene().getRoot() : null;

        if (dialogPane != null) {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);

            if (okButton != null) {



                String titleText = titleField.getText();

                boolean isTitleValid = titleText != null && !titleText.trim().isEmpty();

                boolean isTitleLengthValid = isTitleValid && titleText.trim().length() <= 100;

                String descriptionText = descriptionArea.getText();
                boolean isDescriptionValid = descriptionText == null ||
                        descriptionText.trim().length() <= 500;


                boolean isPriorityValid = priorityComboBox.getValue() != null;


                boolean isStatusValid = statusComboBox.getValue() != null;

                boolean isFormValid = isTitleValid && isTitleLengthValid &&
                        isDescriptionValid && isPriorityValid && isStatusValid;



                   okButton.setDisable(!isFormValid);

                updateFieldAppearance(titleField, isTitleValid && isTitleLengthValid);

                updateFieldAppearance(descriptionArea, isDescriptionValid);

                        updateComboBoxAppearance(priorityComboBox, isPriorityValid);
                updateComboBoxAppearance(statusComboBox, isStatusValid);

                updateValidationMessages(isTitleValid, isTitleLengthValid, isDescriptionValid);
            }
        }
    }

    private void updateFieldAppearance(Control field, boolean isValid) {
        if (isValid) {
            field.getStyleClass().removeAll("error-field");
            field.getStyleClass().add("valid-field");
        } else {
                 field.getStyleClass().removeAll("valid-field");
            field.getStyleClass().add("error-field");
        }
    }

    private void updateComboBoxAppearance(ComboBox<?> comboBox, boolean isValid) {
        if (isValid) {
            comboBox.getStyleClass().removeAll("error-field");
        } else {
            comboBox.getStyleClass().add("error-field");
        }
    }

    private void updateValidationMessages(boolean isTitleValid, boolean isTitleLengthValid, boolean isDescriptionValid) {

        StringBuilder errorMessage = new StringBuilder();

        if (!isTitleValid) {
            errorMessage.append("Le titre est obligatoire.\n");
        } else if (!isTitleLengthValid) {


            errorMessage.append("Le titre ne peut pas dépasser 100 caractères.\n");
        }

        if (!isDescriptionValid) {

            errorMessage.append("La description ne peut pas dépasser 500 caractères svp.\n");
        }

        if (errorMessage.length() > 0) {


            Tooltip errorTooltip = new Tooltip(errorMessage.toString().trim());
            titleField.setTooltip(errorTooltip);
        } else {
            titleField.setTooltip(null);
        }
    }

    private boolean validateForm() {

        String titleText = titleField.getText();
        return titleText != null && !titleText.trim().isEmpty();
    }
}