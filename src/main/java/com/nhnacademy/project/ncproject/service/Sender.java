package com.nhnacademy.project.ncproject.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Sender extends Thread{

    private DataOutputStream out;

    Sender(Socket socket) throws IOException {
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            sendMessage();
        } catch (IOException e) {

        }
    }

    private boolean isSendable() {
        return this.out != null;
    }

    private void sendMessage() throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (isSendable()) {
                this.out.writeUTF(scanner.nextLine());
            }
        }
    }
}
