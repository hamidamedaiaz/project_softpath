package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private TaskService service;
    private FilteredList<Task> filteredTaskList;

    @FXML private MenuItem newTaskMenuItem;
    @FXML private MenuItem importMenuItem;
    @FXML private MenuItem exportMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem editTaskMenuItem;
    @FXML private MenuItem deleteTaskMenuItem;
    @FXML private MenuItem clearAllMenuItem;
    @FXML private MenuItem showAllMenuItem;
    @FXML private MenuItem showTodoMenuItem;
    @FXML private MenuItem showInProgressMenuItem;
    @FXML private MenuItem showCompletedMenuItem;
    @FXML private MenuItem showOverdueMenuItem;
    @FXML private MenuItem showTodayMenuItem;
    @FXML private MenuItem aboutMenuItem;


    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    @FXML private ToggleButton allFilterButton;
    @FXML private ToggleButton todoFilterButton;
    @FXML private ToggleButton inProgressFilterButton;
    @FXML private ToggleButton completedFilterButton;
    @FXML private ToggleButton overdueFilterButton;
    @FXML private ToggleButton todayFilterButton;

    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> dueDateColumn;
    @FXML private TableColumn<Task, String> createdColumn;


    @FXML private TextArea taskTitleArea;
    @FXML private TextArea taskDescriptionArea;
    @FXML private Label taskPriorityValue;
    @FXML private Label taskStatusValue;
    @FXML private Label taskDueDateValue;
    @FXML private Label taskCreatedValue;
    @FXML private Label taskCompletedValue;
    @FXML private Button markTodoButton;
    @FXML private Button markInProgressButton;
    @FXML private Button markCompletedButton;

    @FXML private Label statusLabel;
    @FXML private Label taskCountLabel;
    @FXML private Label todoCountLabel;
    @FXML private Label inProgressCountLabel;
    @FXML private Label completedCountLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Démarrage du contrôleur principal...");


        service = new TaskService();

        initializeTableColumns();
        configureFiltersAndControls();
        setupEventHandlers();
        refreshData();
        resetTaskDetails();
        setTaskActionsEnabled(false);

        System.out.println("Contrôleur principal initialisé!");
    }


    private void initializeTableColumns() {
        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().getDisplayName()));

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        priorityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPriority().getDisplayName()));

        dueDateColumn.setCellValueFactory(data -> {
            LocalDate date = data.getValue().getDueDate();
            String dateText = (date != null) ?
                    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            return new SimpleStringProperty(dateText);
        });

        createdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

        taskTableView.setRowFactory(tableView -> {
            TableRow<Task> row = new TableRow<>();

            row.itemProperty().addListener((observable, oldTask, newTask) -> {
                if (newTask != null) {
                    row.getStyleClass().removeAll("overdue-task", "due-today-task", "completed-task");

                    if (newTask.getStatus() == Task.Status.COMPLETED) {
                        row.getStyleClass().add("completed-task");
                    } else if (newTask.isOverdue()) {
                        row.getStyleClass().add("overdue-task");
                    } else if (newTask.isDueToday()) {
                        row.getStyleClass().add("due-today-task");
                    }
                }
            });
            return row;
        });

        taskTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, previousTask, currentTask) -> {
                    if (currentTask != null) {
                        displayTaskDetails(currentTask);
                        setTaskActionsEnabled(true);
                    } else {
                        resetTaskDetails();
                        setTaskActionsEnabled(false);
                    }
                });
    }

    private void configureFiltersAndControls() {
        filteredTaskList = new FilteredList<>(service.getTasks(), predicate -> true);
        taskTableView.setItems(filteredTaskList);

        ToggleGroup filterGroup = new ToggleGroup();

        allFilterButton.setToggleGroup(filterGroup);
        todoFilterButton.setToggleGroup(filterGroup);
        inProgressFilterButton.setToggleGroup(filterGroup);

        completedFilterButton.setToggleGroup(filterGroup);
        overdueFilterButton.setToggleGroup(filterGroup);
        todayFilterButton.setToggleGroup(filterGroup);

        allFilterButton.setSelected(true);

        sortComboBox.setItems(FXCollections.observableArrayList(

                "Titre", "Priorité", "Échéance", "Statut", "Date de création"));
        sortComboBox.setValue("Titre");
    }

    private void setupEventHandlers() {

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                performSearch();
            }
        });

        searchField.setOnAction(event -> performSearch());

        service.getTasks().addListener((javafx.collections.ListChangeListener<Task>) change -> {
            refreshStatusBar();
        });
    }

    private void refreshData() {
        refreshStatusBar();
    }

    public void handleNewTask(ActionEvent event) {
        openTaskDialog(null);
    }

    public void handleEditTask(ActionEvent event) {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            openTaskDialog(selectedTask);
        } else {
            displayMessage("Aucune sélection", "Veuillez sélectionner une tâche à modifier.");
        }
    }

    public void handleDeleteTask(ActionEvent event) {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmer la suppression");
            confirmAlert.setHeaderText("Supprimer la tâche");
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer la tâche \"" +
                    selectedTask.getTitle() + "\" ?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                service.deleteTask(selectedTask);
                statusLabel.setText("Tâche supprimée");
            }
        } else {
            displayMessage("Aucune sélection", "Veuillez sélectionner une tâche à supprimer.");
        }
    }

    public void handleSearch() {
        performSearch();
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            filteredTaskList.setPredicate(null);
        } else {
            filteredTaskList.setPredicate(task ->
                    task.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                            task.getDescription().toLowerCase().contains(query.toLowerCase()));
        }
        statusLabel.setText("Recherche: " + filteredTaskList.size() + " résultats");
    }


    public void handleSort(ActionEvent event) {
        String selectedCriteria = sortComboBox.getValue();
        if (selectedCriteria != null) {
            String criteriaKey = "";
            switch (selectedCriteria) {
                case "Titre":
                    criteriaKey = "title";
                    break;
                case "Priorité":
                    criteriaKey = "priority";
                    break;
                case "Échéance":
                    criteriaKey = "duedate";
                    break;
                case "Statut":
                    criteriaKey = "status";
                    break;
                case "Date de création":
                    criteriaKey = "created";
                    break;
            }
            if (!criteriaKey.isEmpty()) {
                service.sortTasks(criteriaKey);
                statusLabel.setText("Trié par " + selectedCriteria.toLowerCase());
            }
        }
    }

    public void handleShowAll(ActionEvent event) {
        filteredTaskList.setPredicate(null);
        statusLabel.setText("Affichage: Toutes les tâches");
    }

    public void handleShowTodo(ActionEvent event) {
        filteredTaskList.setPredicate(task -> task.getStatus() == Task.Status.TODO);
        statusLabel.setText("Affichage: Tâches à faire");
    }

    public void handleShowInProgress(ActionEvent event) {
        filteredTaskList.setPredicate(task -> task.getStatus() == Task.Status.IN_PROGRESS);
        statusLabel.setText("Affichage: Tâches en cours");
    }


    public void handleShowCompleted(ActionEvent event) {
        filteredTaskList.setPredicate(task -> task.getStatus() == Task.Status.COMPLETED);
        statusLabel.setText("Affichage: Tâches terminées");
    }

    public void handleShowOverdue(ActionEvent event) {
        filteredTaskList.setPredicate(Task::isOverdue);
        statusLabel.setText("Affichage: Tâches en retard");
    }

    public void handleShowToday(ActionEvent event) {
        filteredTaskList.setPredicate(Task::isDueToday);
        statusLabel.setText("Affichage: Tâches dues aujourd'hui");
    }


    public void handleMarkAsTodo(ActionEvent event) {
        updateTaskStatus(Task.Status.TODO);
    }

    public void handleMarkAsInProgress(ActionEvent event) {
        updateTaskStatus(Task.Status.IN_PROGRESS);
    }

    public void handleMarkAsCompleted(ActionEvent event) {
        updateTaskStatus(Task.Status.COMPLETED);
    }

    private void updateTaskStatus(Task.Status status) {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setStatus(status);
            service.updateTask(selectedTask);
            displayTaskDetails(selectedTask);
            statusLabel.setText("Statut mis à jour: " + status.getDisplayName());
        }
    }

    public void handleImport(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importer des tâches");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));

        File file = chooser.showOpenDialog(getCurrentStage());
        if (file != null) {
            try {
                service.importTasks(file);
                statusLabel.setText("Tâches importées avec succès");
            } catch (IOException e) {
                displayMessage("Erreur d'importation",
                        "Impossible d'importer le fichier: " + e.getMessage());
            }
        }
    }

    public void handleExport(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter les tâches");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));
        chooser.setInitialFileName("taches_export.json");

        File file = chooser.showSaveDialog(getCurrentStage());
        if (file != null) {
            try {
                service.exportTasks(file);
                statusLabel.setText("Tâches exportées avec succès");
            } catch (IOException e) {
                displayMessage("Erreur d'exportation",
                        "Impossible d'exporter le fichier: " + e.getMessage());
            }
        }
    }

    public void handleClearAll(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer l'effacement");
        confirmAlert.setHeaderText("Effacer toutes les tâches");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer TOUTES les tâches ?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.clearAllTasks();
            statusLabel.setText("Toutes les tâches ont été supprimées");
        }
    }

    public void handleExit(ActionEvent event) {
        getCurrentStage().close();
    }

    public void handleAbout(ActionEvent event) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("À Propos");
        infoAlert.setHeaderText("Gestionnaire de Tâches JavaFX");
        infoAlert.setContentText("Version 1.0.0\n\n" +
                   "Application de gestion de tâches développée en JavaFX\n" +
                "avec sauvegarde locale et interface moderne.\n\n" + "Technologies utilisées:\n" +
                "• JavaFX 24\n" +

                "• FXML\n" +

                "• CSS\n" +
                "• Maven\n" +
                "• Jackson JSON");
        infoAlert.showAndWait();
    }

    private void openTaskDialog(Task taskToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TaskDialog.fxml"));
            DialogPane pane = loader.load();

            TaskDialogController dialogController = loader.getController();
            dialogController.setTask(taskToEdit);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle(taskToEdit == null ? "Nouvelle Tâche" : "Modifier Tâche");

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Task taskResult = dialogController.getResult();
                if (taskResult != null) {
                    if (taskToEdit == null) {
                        service.addTask(taskResult);
                        statusLabel.setText("Nouvelle tâche ajoutée");
                    } else {
                        service.updateTask(taskResult);
                        statusLabel.setText("Tâche mise à jour");
                    }
                }
            }
        } catch (IOException e) {
            displayMessage("Erreur", "Impossible d'ouvrir la fenêtre de dialogue: " + e.getMessage());
        }
    }

    private void displayTaskDetails(Task task) {


        taskTitleArea.setText(task.getTitle());
        taskDescriptionArea.setText(task.getDescription());
        taskPriorityValue.setText(task.getPriority().getDisplayName());
        taskPriorityValue.setStyle("-fx-text-fill: " + task.getPriority().getColor());
        taskStatusValue.setText(task.getStatus().getDisplayName());

        String dueDateText = (task.getDueDate() != null) ?
                task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Aucune";
        taskDueDateValue.setText(dueDateText);

        taskCreatedValue.setText(task.getCreatedAt().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        String completedText = (task.getCompletedAt() != null) ?
                task.getCompletedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Non terminée";
        taskCompletedValue.setText(completedText);
    }

    private void resetTaskDetails() {
        taskTitleArea.clear();
        taskDescriptionArea.clear();
        taskPriorityValue.setText("");

        taskStatusValue.setText("");
        taskDueDateValue.setText("");
        taskCreatedValue.setText("");
        taskCompletedValue.setText("");
    }

    private void setTaskActionsEnabled(boolean enabled) {
        editButton.setDisable(!enabled);
        deleteButton.setDisable(!enabled);
        markTodoButton.setDisable(!enabled);
        markInProgressButton.setDisable(!enabled);
        markCompletedButton.setDisable(!enabled);
    }


    private void refreshStatusBar() {
        long total = service.getTasks().size();
        long todoTasks = service.countTasksByStatus(Task.Status.TODO);
        long inProgressTasks = service.countTasksByStatus(Task.Status.IN_PROGRESS);
        long completedTasks = service.countTasksByStatus(Task.Status.COMPLETED);

        taskCountLabel.setText("Total: " + total + " tâches");
        todoCountLabel.setText("À faire: " + todoTasks);
        inProgressCountLabel.setText("En cours: " + inProgressTasks);
        completedCountLabel.setText("Terminées: " + completedTasks);
    }

    private void displayMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Stage getCurrentStage() {
        return (Stage) taskTableView.getScene().getWindow();
    }
}