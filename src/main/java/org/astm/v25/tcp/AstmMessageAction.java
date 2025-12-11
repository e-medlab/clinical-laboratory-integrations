package org.astm.v25.tcp;

public interface AstmMessageAction {
    String onMessage(String incoming) throws Exception;
}
