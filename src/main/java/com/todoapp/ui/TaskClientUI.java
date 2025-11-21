package com.todoapp.ui;

import com.todoapp.client.TaskClient;
import com.todoapp.model.Protocol;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TaskClientUI extends Application {
    private TaskClient client;
    private TextField serverHostField;
    private TextField serverPortField;
    private Button connectButton;
    private TextField taskDescriptionField;
    private ListView<String> taskListView;
    private Label statusLabel;
    private VBox mainContent;
    private boolean isConnected = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema de Gerenciamento de Tarefas - Cliente");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        HBox connectionPanel = createConnectionPanel();
        mainContent = createMainContent();
        mainContent.setDisable(true);

        statusLabel = new Label("Desconectado");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        statusLabel.setTextFill(Color.RED);

        root.getChildren().addAll(
            createTitle(),
            connectionPanel,
            new Separator(),
            statusLabel,
            mainContent
        );

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (client != null && isConnected) {
                client.disconnect();
            }
        });
        primaryStage.show();
    }

    private VBox createTitle() {
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));
        titleBox.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 5;");

        Label title = new Label("Sistema de Gerenciamento de Tarefas");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Aplicacao Cliente-Servidor com Sockets");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

        titleBox.getChildren().addAll(title, subtitle);
        return titleBox;
    }

    private HBox createConnectionPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label hostLabel = new Label("Host:");
        serverHostField = new TextField("localhost");
        serverHostField.setPrefWidth(150);

        Label portLabel = new Label("Porta:");
        serverPortField = new TextField("5000");
        serverPortField.setPrefWidth(80);

        connectButton = new Button("Conectar");
        connectButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        connectButton.setOnAction(e -> toggleConnection());

        panel.getChildren().addAll(hostLabel, serverHostField, portLabel, serverPortField, connectButton);
        return panel;
    }

    private VBox createMainContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(10));

        HBox addTaskPanel = createAddTaskPanel();
        VBox taskListPanel = createTaskListPanel();

        content.getChildren().addAll(addTaskPanel, taskListPanel);
        return content;
    }

    private HBox createAddTaskPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label label = new Label("Nova Tarefa:");
        label.setStyle("-fx-font-weight: bold;");

        taskDescriptionField = new TextField();
        taskDescriptionField.setPromptText("Digite a descricao da tarefa...");
        taskDescriptionField.setPrefWidth(400);
        taskDescriptionField.setOnAction(e -> addTask());

        Button addButton = new Button("Adicionar");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> addTask());

        panel.getChildren().addAll(label, taskDescriptionField, addButton);
        return panel;
    }

    private VBox createTaskListPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label label = new Label("Lista de Tarefas:");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        taskListView = new ListView<>();
        taskListView.setPrefHeight(300);
        taskListView.setPlaceholder(new Label("Nenhuma tarefa cadastrada"));

        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);

        Button refreshButton = new Button("Atualizar");
        refreshButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> refreshTasks());

        Button completeButton = new Button("Marcar como Completa");
        completeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        completeButton.setOnAction(e -> completeTask());

        Button removeButton = new Button("Remover");
        removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeButton.setOnAction(e -> removeTask());

        Button clearButton = new Button("Limpar Todas");
        clearButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearAllTasks());

        buttonPanel.getChildren().addAll(refreshButton, completeButton, removeButton, clearButton);

        panel.getChildren().addAll(label, taskListView, buttonPanel);
        VBox.setVgrow(taskListView, Priority.ALWAYS);

        return panel;
    }

    private void toggleConnection() {
        if (!isConnected) {
            connectToServer();
        } else {
            disconnectFromServer();
        }
    }

    private void connectToServer() {
        String host = serverHostField.getText().trim();
        String portText = serverPortField.getText().trim();

        if (host.isEmpty() || portText.isEmpty()) {
            showAlert("Erro", "Por favor, preencha o host e a porta", Alert.AlertType.ERROR);
            return;
        }

        try {
            int port = Integer.parseInt(portText);
            client = new TaskClient(host, port);

            if (client.connect()) {
                isConnected = true;
                statusLabel.setText("Conectado ao servidor: " + host + ":" + port);
                statusLabel.setTextFill(Color.GREEN);
                connectButton.setText("Desconectar");
                connectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                mainContent.setDisable(false);
                serverHostField.setDisable(true);
                serverPortField.setDisable(true);
                refreshTasks();
                showAlert("Sucesso", "Conectado ao servidor com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erro", "Nao foi possivel conectar ao servidor", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Erro", "Porta invalida", Alert.AlertType.ERROR);
        }
    }

    private void disconnectFromServer() {
        if (client != null) {
            client.disconnect();
        }
        isConnected = false;
        statusLabel.setText("Desconectado");
        statusLabel.setTextFill(Color.RED);
        connectButton.setText("Conectar");
        connectButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        mainContent.setDisable(true);
        serverHostField.setDisable(false);
        serverPortField.setDisable(false);
        taskListView.getItems().clear();
    }

    private void addTask() {
        String description = taskDescriptionField.getText().trim();

        if (description.isEmpty()) {
            showAlert("Erro", "Por favor, digite uma descricao para a tarefa", Alert.AlertType.WARNING);
            return;
        }

        try {
            String response = client.addTask(description);
            processResponse(response, "Tarefa adicionada com sucesso!");
            taskDescriptionField.clear();
            refreshTasks();
        } catch (Exception e) {
            showAlert("Erro", "Erro ao adicionar tarefa: " + e.getMessage(), Alert.AlertType.ERROR);
            checkConnection();
        }
    }

    private void refreshTasks() {
        try {
            String response = client.listTasks();
            String[] parts = response.split("\\" + Protocol.SEPARATOR, 2);

            taskListView.getItems().clear();

            if (parts[0].equals(Protocol.SUCCESS)) {
                if (parts.length > 1 && !parts[1].equals("Nenhuma tarefa cadastrada")) {
                    String[] tasks = parts[1].split(";");

                    for (String taskData : tasks) {
                        String[] taskParts = taskData.split(":");
                        if (taskParts.length == 3) {
                            int id = Integer.parseInt(taskParts[0]);
                            String description = taskParts[1];
                            boolean completed = Boolean.parseBoolean(taskParts[2]);

                            String status = completed ? "[COMPLETA]" : "[PENDENTE]";
                            String displayText = String.format("ID: %d | %s %s", id, status, description);
                            taskListView.getItems().add(displayText);
                        }
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Erro", "Erro ao listar tarefas: " + e.getMessage(), Alert.AlertType.ERROR);
            checkConnection();
        }
    }

    private void completeTask() {
        String selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showAlert("Aviso", "Por favor, selecione uma tarefa", Alert.AlertType.WARNING);
            return;
        }

        try {
            int id = extractTaskId(selectedTask);
            String response = client.completeTask(id);
            processResponse(response, "Tarefa marcada como completa!");
            refreshTasks();
        } catch (Exception e) {
            showAlert("Erro", "Erro ao completar tarefa: " + e.getMessage(), Alert.AlertType.ERROR);
            checkConnection();
        }
    }

    private void removeTask() {
        String selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showAlert("Aviso", "Por favor, selecione uma tarefa", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Remocao");
        confirmation.setHeaderText("Deseja realmente remover esta tarefa?");
        confirmation.setContentText(selectedTask);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int id = extractTaskId(selectedTask);
                    String serverResponse = client.removeTask(id);
                    processResponse(serverResponse, "Tarefa removida com sucesso!");
                    refreshTasks();
                } catch (Exception e) {
                    showAlert("Erro", "Erro ao remover tarefa: " + e.getMessage(), Alert.AlertType.ERROR);
                    checkConnection();
                }
            }
        });
    }

    private void clearAllTasks() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Limpeza");
        confirmation.setHeaderText("Deseja realmente remover TODAS as tarefas?");
        confirmation.setContentText("Esta acao nao pode ser desfeita!");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String serverResponse = client.clearTasks();
                    processResponse(serverResponse, "Todas as tarefas foram removidas!");
                    refreshTasks();
                } catch (Exception e) {
                    showAlert("Erro", "Erro ao limpar tarefas: " + e.getMessage(), Alert.AlertType.ERROR);
                    checkConnection();
                }
            }
        });
    }

    private int extractTaskId(String taskDisplay) {
        String[] parts = taskDisplay.split("\\|");
        String idPart = parts[0].trim().replace("ID:", "").trim();
        return Integer.parseInt(idPart);
    }

    private void processResponse(String response, String successMessage) {
        String[] parts = response.split("\\" + Protocol.SEPARATOR, 2);

        if (parts[0].equals(Protocol.SUCCESS)) {
            showAlert("Sucesso", successMessage, Alert.AlertType.INFORMATION);
        } else {
            String errorMessage = parts.length > 1 ? parts[1] : "Erro desconhecido";
            showAlert("Erro", errorMessage, Alert.AlertType.ERROR);
        }
    }

    private void checkConnection() {
        if (client != null && !client.isConnected()) {
            disconnectFromServer();
            showAlert("Conexao Perdida", "A conexao com o servidor foi perdida", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
