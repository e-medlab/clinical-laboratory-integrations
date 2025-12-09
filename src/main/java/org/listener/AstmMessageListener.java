package org.listener;

import org.astm.v25.tcp.AstmTcpServer;

public class AstmMessageListener {
    private static final int PORT_NUMBER = 56420;

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        AstmTcpServer server = new AstmTcpServer(PORT_NUMBER);

        System.out.println("Starting an ASTM server listening on port " + PORT_NUMBER);
        String message = server.waitForMessage(null);

        System.out.println(message);
        Thread.currentThread().join();
    }
}
