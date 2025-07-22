package com.taskmanager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskmanager.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    public void deleteTask(int taskId) {
        Iterator<Task> iterator = taskList.iterator();
        while (iterator.hasNext()) {
            Task currentTask = iterator.next();
            if (currentTask.getId() == taskId) {
                iterator.remove();
                saveTasksToFile();
                break;
            }
        }
    }

    public Task findTaskById(int id) {
        for (Task task : taskList) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public List<Task> getTasksByStatus(Task.Status status) {
        List<Task> result = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getStatus() == status) {
                result.add(task);
            }
        }
        return result;
    }

    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskList.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {
        List<Task> overdueTasks = new ArrayList<>();
        for (Task task : taskList) {
            if (task.isOverdue()) {
                overdueTasks.add(task);
            }
        }
        return overdueTasks;
    }

    public List<Task> getTodayTasks() {
        return taskList.stream()
                .filter(Task::isDueToday)
                .collect(Collectors.toList());
    }

    public List<Task> getThisWeekTasks() {
        LocalDate now = LocalDate.now();
        LocalDate weekEnd = now.plusDays(7);

        List<Task> thisWeekTasks = new ArrayList<>();
        for (Task task : taskList) {
            LocalDate dueDate = task.getDueDate();
            if (dueDate != null &&
                    (dueDate.isEqual(now) || dueDate.isAfter(now)) &&
                    dueDate.isBefore(weekEnd)) {
                thisWeekTasks.add(task);
            }
        }
        return thisWeekTasks;
    }

    public List<Task> searchTasks(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return new ArrayList<>(taskList);
        }

        String lowerQuery = searchQuery.toLowerCase();
        List<Task> searchResults = new ArrayList<>();

        for (Task task : taskList) {
            String title = task.getTitle().toLowerCase();
            String description = task.getDescription().toLowerCase();

            if (title.contains(lowerQuery) || description.contains(lowerQuery)) {
                searchResults.add(task);
            }
        }
        return searchResults;
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

    public long countTasksByStatus(Task.Status status) {
        long count = 0;
        for (Task task : taskList) {
            if (task.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public void saveTasks() {
        saveTasksToFile();
    }

    private void saveTasksToFile() {
        try {
            File file = new File(SAVE_FILE);
            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, taskList);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde: " + e.getMessage());
        }
    }

    public void loadTasks() {
        loadTasksFromFile();
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