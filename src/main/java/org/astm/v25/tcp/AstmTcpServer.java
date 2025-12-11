package org.astm.v25.tcp;

import org.dew.comm.IMessage;
import org.dew.comm.tcpip.TcpIpDriver;

public class AstmTcpServer implements AutoCloseable {

    private final int port;
    private final TcpIpDriver driver;

    public AstmTcpServer(int port) throws Exception {
        this.port = port;
        this.driver = new TcpIpDriver(String.valueOf(port));
    }

    public String waitForMessage(String responseAstmText) throws Exception {
        IMessage responseMsg = null;
        if (responseAstmText != null && !responseAstmText.isEmpty()) {
            responseMsg = AstmMessageMapper.convert(responseAstmText);
        }

        IMessage received = driver.waitForHostMessages(responseMsg);
        if (received == null) {
            return null;
        }
        return AstmMessageMapper.convert(received);
    }

    public String start(AstmMessageAction onMessage) throws Exception {
        driver.waitForHostMessagesContinuous(msg -> {
            String msgString = AstmMessageMapper.convert(msg);
            String responseString = onMessage.onMessage(msgString);
            return AstmMessageMapper.convert(responseString);
        });
        return "";
    }

    @Override
    public void close() {
        driver.destroy();
    }

    public int getPort() {
        return port;
    }
}
