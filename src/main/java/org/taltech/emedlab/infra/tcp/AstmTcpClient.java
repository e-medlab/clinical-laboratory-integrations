package org.taltech.emedlab.infra.tcp;

import org.dew.comm.IMessage;
import org.dew.comm.tcpip.TcpIpDriver;

public class AstmTcpClient implements AutoCloseable {

    private final String host;
    private final int port;
    private final TcpIpDriver driver;

    public AstmTcpClient(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
        this.driver = new TcpIpDriver(host, port);
    }

    public String send(String astmText) throws Exception {
        IMessage request = AstmMessageMapper.convert(astmText);

        IMessage response = driver.sendMessage(request);

        if (response == null) {
            return null;
        }

        return AstmMessageMapper.convert(response);
    }

    @Override
    public void close() {
        driver.destroy();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
