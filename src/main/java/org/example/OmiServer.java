package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class OmiServer {
    private List<ClientHandler> clients = new ArrayList<>();
    private OmiGameLogic gameLogic;

    public OmiServer() {
        gameLogic = new OmiGameLogic();
    }

    public static void main(String[] args) {
        new OmiServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Listening on port 12345");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public OmiGameLogic getGameLogic() {
        return gameLogic;
    }
}

