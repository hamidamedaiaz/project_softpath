module com.taskmanager {

       requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires com.fasterxml.jackson.datatype.jsr310;

      exports com.taskmanager;
    exports com.taskmanager.controller;
    exports com.taskmanager.model;

    exports com.taskmanager.service;
     exports com.taskmanager.enums;

    opens com.taskmanager to javafx.fxml;
    opens com.taskmanager.controller to javafx.fxml;

    opens com.taskmanager.model to com.fasterxml.jackson.databind;
      opens com.taskmanager.enums to com.fasterxml.jackson.databind;
}