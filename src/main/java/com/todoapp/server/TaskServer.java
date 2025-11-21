package com.todoapp.server;

import com.todoapp.model.Protocol;
import com.todoapp.model.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskServer {
    private static final int PORT = 5000;
    private static final List<Task> tasks = new ArrayList<>();
    private static final AtomicInteger taskIdCounter = new AtomicInteger(1);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    SERVIDOR DE GERENCIAMENTO DE TAREFAS");
        System.out.println("========================================");
        System.out.println("Iniciando servidor na porta " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado com sucesso!");
            System.out.println("Aguardando conexoes de clientes...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Requisicao recebida: " + request);
                    String response = processRequest(request);
                    out.println(response);
                }
            } catch (IOException e) {
                System.err.println("Erro na comunicacao com cliente: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    System.out.println("Cliente desconectado: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private synchronized String processRequest(String request) {
            String[] parts = request.split("\\" + Protocol.SEPARATOR);
            String command = parts[0];

            try {
                switch (command) {
                    case Protocol.ADD_TASK:
                        if (parts.length < 2) {
                            return Protocol.ERROR + Protocol.SEPARATOR + "Descricao da tarefa nao fornecida";
                        }
                        return addTask(parts[1]);

                    case Protocol.LIST_TASKS:
                        return listTasks();

                    case Protocol.REMOVE_TASK:
                        if (parts.length < 2) {
                            return Protocol.ERROR + Protocol.SEPARATOR + "ID da tarefa nao fornecido";
                        }
                        return removeTask(Integer.parseInt(parts[1]));

                    case Protocol.COMPLETE_TASK:
                        if (parts.length < 2) {
                            return Protocol.ERROR + Protocol.SEPARATOR + "ID da tarefa nao fornecido";
                        }
                        return completeTask(Integer.parseInt(parts[1]));

                    case Protocol.CLEAR_TASKS:
                        return clearTasks();

                    case Protocol.DISCONNECT:
                        return Protocol.SUCCESS + Protocol.SEPARATOR + "Desconectado com sucesso";

                    default:
                        return Protocol.ERROR + Protocol.SEPARATOR + "Comando desconhecido: " + command;
                }
            } catch (NumberFormatException e) {
                return Protocol.ERROR + Protocol.SEPARATOR + "ID invalido";
            } catch (Exception e) {
                return Protocol.ERROR + Protocol.SEPARATOR + "Erro ao processar requisicao: " + e.getMessage();
            }
        }

        private String addTask(String description) {
            int id = taskIdCounter.getAndIncrement();
            Task task = new Task(id, description);
            tasks.add(task);
            System.out.println("Tarefa adicionada: " + task);
            return Protocol.SUCCESS + Protocol.SEPARATOR + "Tarefa adicionada com ID: " + id;
        }

        private String listTasks() {
            if (tasks.isEmpty()) {
                return Protocol.SUCCESS + Protocol.SEPARATOR + "Nenhuma tarefa cadastrada";
            }

            StringBuilder sb = new StringBuilder();
            sb.append(Protocol.SUCCESS).append(Protocol.SEPARATOR);

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                sb.append(task.getId()).append(":")
                  .append(task.getDescription()).append(":")
                  .append(task.isCompleted());

                if (i < tasks.size() - 1) {
                    sb.append(";");
                }
            }

            return sb.toString();
        }

        private String removeTask(int id) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == id) {
                    Task removed = tasks.remove(i);
                    System.out.println("Tarefa removida: " + removed);
                    return Protocol.SUCCESS + Protocol.SEPARATOR + "Tarefa removida com sucesso";
                }
            }
            return Protocol.ERROR + Protocol.SEPARATOR + "Tarefa nao encontrada com ID: " + id;
        }

        private String completeTask(int id) {
            for (Task task : tasks) {
                if (task.getId() == id) {
                    task.setCompleted(true);
                    System.out.println("Tarefa marcada como completa: " + task);
                    return Protocol.SUCCESS + Protocol.SEPARATOR + "Tarefa marcada como completa";
                }
            }
            return Protocol.ERROR + Protocol.SEPARATOR + "Tarefa nao encontrada com ID: " + id;
        }

        private String clearTasks() {
            int count = tasks.size();
            tasks.clear();
            System.out.println("Todas as tarefas foram removidas (" + count + " tarefas)");
            return Protocol.SUCCESS + Protocol.SEPARATOR + count + " tarefa(s) removida(s)";
        }
    }
}
