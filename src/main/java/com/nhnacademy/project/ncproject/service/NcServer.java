package com.nhnacademy.project.ncproject.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NcServer {
    private final ConcurrentHashMap<Integer, DataOutputStream> clientOutMap =
        new ConcurrentHashMap();
    private static final Log log = LogFactory.getLog(NcServer.class);

    public void start(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientSession client = new ClientSession(socket);
                    Thread sender = new Sender(socket);

                    client.start();
                    sender.start();
                } catch (IOException e) {
                    // TODO 클라이언트 접속 실패
                }
            }
        }
    }


    private void joinChat(ClientSession session) {
        clientOutMap.put(session.id, session.out);
    }

    private void leaveChat(ClientSession session) {
        clientOutMap.remove(session.id);
    }

    private void receiveMessage(String message) {
        log.info(message);

    }


    class ClientSession extends Thread {

        private final DataInputStream in;
        private final DataOutputStream out;
        private int id=0;

        ClientSession(Socket socket) throws IOException {

            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            initialize();
            connect();

        }

        private void initialize() {
            id++;
                joinChat(this);

        }

        private void connect() {
            try {
                while (isConnect()) {
                    receiveMessage(in.readUTF());
                }
            } catch (IOException cause) {
                // TODO: 채팅 중 연결이 끊기는 경우
            } finally {
                disconnect();
            }
        }

        private boolean isConnect() {
            return this.in != null;
        }

        private void disconnect() {
            leaveChat(this);
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
}