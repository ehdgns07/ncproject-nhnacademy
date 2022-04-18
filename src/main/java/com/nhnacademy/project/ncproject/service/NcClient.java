package com.academy.project.ncproject.service;

import com.nhnacademy.project.ncproject.service.NcServer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class NcClient extends Thread{
    private static final Log log = LogFactory.getLog(NcClient.class);

    public void connect(String serverHost, int port) {
        try {
            Socket socket = new Socket(serverHost, port);
            Thread sender = new Sender(socket);
            Thread receiver = new Receiver(socket);

            sender.start();
            receiver.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Sender extends Thread {

        private DataOutputStream out;

        private Sender(Socket socket) throws IOException {
            this.out = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                sendMessage();
            } catch (IOException e) {
                // TODO
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

    private static class Receiver extends Thread {
        private final DataInputStream in;

        private Receiver(Socket socket) throws IOException {
            this.in = new DataInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            while (isReceivable()) {
                receiveMessage();
            }
        }

        private boolean isReceivable() {
            return this.in != null;
        }

        private void receiveMessage() {
            try {
                log.info(in.readUTF());
            } catch (IOException e) {
                // TODO
            }
        }
    }
}