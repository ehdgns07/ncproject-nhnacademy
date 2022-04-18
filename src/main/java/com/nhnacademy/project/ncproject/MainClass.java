package com.nhnacademy.project.ncproject;

import com.nhnacademy.project.ncproject.service.NcClient;
import com.nhnacademy.project.ncproject.service.NcServer;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class MainClass {
    private static final Log log = LogFactory.getLog(NcClient.class);

        // clientId: 클라이언트 전송용 OutputStream

        public static void main(String[] args) throws IOException {
            NcServer server = new NcServer();
            NcClient client = new NcClient();

            if(usage(args)){
                log.info("Usage: snc [option] or [hostIp] [port]\n" +
                    "Options:\n" +
                    "-l   <port>     서버 모드로 동작, 입력 받은 포트로 listen");
                System.exit(1);
            }

            if (Objects.equals(args[0], "-l")) {
                server.start(Integer.parseInt(args[1]));
            } else {
                client.connect(args[0], Integer.parseInt(args[1]));
            }

        }

        public static boolean usage(String[] args){
            return args.length < 2 || Objects.equals(args[0], "-h") || Objects.equals(args[0], "-help") || Objects.equals(args[0], "--help");
        }
}
