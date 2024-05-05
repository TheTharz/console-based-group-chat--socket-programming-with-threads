package org.example;

import java.io.*;
import java.net.*;

public class OmiClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server.");

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput); // Send user input to server
                String serverResponse = in.readLine(); // Receive game updates from server
                System.out.println("Server response: " + serverResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}