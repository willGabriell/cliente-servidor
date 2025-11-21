package com.todoapp.client;

import com.todoapp.model.Protocol;

import java.io.*;
import java.net.Socket;

public class TaskClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String serverHost;
    private int serverPort;
    private boolean connected = false;

    public TaskClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    public boolean connect() {
        try {
            socket = new Socket(serverHost, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            System.out.println("Conectado ao servidor: " + serverHost + ":" + serverPort);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    public void disconnect() {
        try {
            if (connected) {
                sendRequest(Protocol.DISCONNECT);
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
                connected = false;
                System.out.println("Desconectado do servidor");
            }
        } catch (IOException e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    private String sendRequest(String request) throws IOException {
        if (!isConnected()) {
            throw new IOException("Nao conectado ao servidor");
        }

        out.println(request);
        String response = in.readLine();

        if (response == null) {
            connected = false;
            throw new IOException("Conexao perdida com o servidor");
        }

        return response;
    }

    public String addTask(String description) throws IOException {
        String request = Protocol.ADD_TASK + Protocol.SEPARATOR + description;
        return sendRequest(request);
    }

    public String listTasks() throws IOException {
        String request = Protocol.LIST_TASKS;
        return sendRequest(request);
    }

    public String removeTask(int id) throws IOException {
        String request = Protocol.REMOVE_TASK + Protocol.SEPARATOR + id;
        return sendRequest(request);
    }

    public String completeTask(int id) throws IOException {
        String request = Protocol.COMPLETE_TASK + Protocol.SEPARATOR + id;
        return sendRequest(request);
    }

    public String clearTasks() throws IOException {
        String request = Protocol.CLEAR_TASKS;
        return sendRequest(request);
    }

    public static class TaskInfo {
        private int id;
        private String description;
        private boolean completed;

        public TaskInfo(int id, String description, boolean completed) {
            this.id = id;
            this.description = description;
            this.completed = completed;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return completed;
        }

        public String getStatus() {
            return completed ? "Completa" : "Pendente";
        }
    }
}
