package com.academy.project.ncproject.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;


public class NcServer {


        // clientId: 클라이언트 전송용 OutputStream
        private final ConcurrentHashMap<String, DataOutputStream> clientOutMap = new ConcurrentHashMap();

        public static void main(String[] args) throws IOException {
            NcServer server = new NcServer();
            server.start();
        }

        public void start() throws IOException {
            try (ServerSocket serverSocket = new ServerSocket(8888)) {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        ClientSession client = new ClientSession(socket);
                        client.start();
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

        private void sendToAll(String message) {
            for (DataOutputStream out : clientOutMap.values()) {
                try {
                    out.writeUTF(message);
                } catch (IOException e) {
                    // TODO: 해당 클라이언트로 송출 스트림이 실패함(네트워크 끈김)
                }
            }
        }

        class ClientSession extends Thread {
            private final Socket socket;
            private final DataInputStream in;
            private final DataOutputStream out;
            private String id;

            ClientSession(Socket socket) throws IOException {
                this.socket = socket;
                this.in = new DataInputStream(socket.getInputStream());
                this.out = new DataOutputStream(socket.getOutputStream());
            }

            @Override
            public void run() {
                initialize();
                connect();
            }

            private void initialize() {
                try {
                    this.id = in.readUTF();
                    joinChat(this);
                } catch (IOException cause) {
                    // TODO: 최초 통신(아이디 받기)이 실패하는 경우
                }
            }

            private void connect() {
                try {
                    while (isConnect()) {
                        sendToAll(in.readUTF());
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
    }
}
