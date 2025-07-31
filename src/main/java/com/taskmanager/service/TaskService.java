package com.taskmanager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskmanager.model.Task;
import com.taskmanager.enums.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class TaskService {


    private static final String SAVE_FILE = "tasks.json";
    private ObservableList<Task> taskList;


    private ObjectMapper jsonMapper;
    private int currentId;

    public TaskService() {
        this.taskList = FXCollections.observableArrayList();
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.registerModule(new JavaTimeModule());
        this.currentId = 1;

        loadTasksFromFile();
    }

    public ObservableList<Task> getTasks() {
        return this.taskList;
    }

    public void addTask(Task task) {
        if (task != null) {
            task.setId(generateNextId());
            this.taskList.add(task);
            saveTasksToFile();
        }
    }

    public void updateTask(Task updatedTask) {
        if (updatedTask == null) return;

        for (int i = 0; i < taskList.size(); i++) {


            Task existing = taskList.get(i);
            if (existing.getId() == updatedTask.getId()) {
                taskList.set(i, updatedTask);
                saveTasksToFile();
                break;

            }
        }
    }

    public void deleteTask(Task taskToDelete) {
        if (taskToDelete != null) {
            taskList.remove(taskToDelete);


            saveTasksToFile();
        }
    }

    public void sortTasks(String sortCriteria) {
        if (sortCriteria == null) return;

        switch (sortCriteria.toLowerCase()) {
            case "priority":

                Collections.sort(taskList, (t1, t2) -> t2.getPriority().compareTo(t1.getPriority()));
                break;
            case "duedate":


                Collections.sort(taskList, (t1, t2) -> {
                    LocalDate date1 = t1.getDueDate();
                    LocalDate date2 = t2.getDueDate();
                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return 1;
                    if (date2 == null) return -1;
                    return date1.compareTo(date2);
                });
                break;
            case "status":
                Collections.sort(taskList, Comparator.comparing(Task::getStatus));
                break;
            case "created":
                Collections.sort(taskList, Comparator.comparing(Task::getCreatedAt));
                break;
            case "title":
            default:
                Collections.sort(taskList, (t1, t2) ->
                        t1.getTitle().compareToIgnoreCase(t2.getTitle()));
                break;
        }
    }

    public long countTasksByStatus(Status status) {

           long count = 0;
        for (Task task : taskList) {
            if (task.getStatus() == status) {
                count++;
            }
        }
        return count;
    }


    private void saveTasksToFile() {
        try {
            File file = new File(SAVE_FILE);
               jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, taskList);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde: " + e.getMessage());
        }
    }


    private void loadTasksFromFile() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return;
        }

        try {
            List<Task> loadedTasks = jsonMapper.readValue(file, new TypeReference<List<Task>>() {});
               taskList.clear();
            taskList.addAll(loadedTasks);

               updateCurrentId();
        } catch (IOException e) {
            System.err.println("Erreur chargement: " + e.getMessage());
        }
    }

    private void updateCurrentId() {
        int maxId = 0;
        for (Task task : taskList) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        currentId = maxId + 1;
    }

    private int generateNextId() {
        return currentId++;
    }

    public void clearAllTasks() {
        taskList.clear();
        saveTasksToFile();
    }

    public void importTasks(File file) throws IOException {
        List<Task> importedTasks = jsonMapper.readValue(file, new TypeReference<List<Task>>() {});

        for (Task task : importedTasks) {

               task.setId(generateNextId());
            taskList.add(task);
        }
        saveTasksToFile();
    }

    public void exportTasks(File file) throws IOException {


        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, taskList);
    }
}