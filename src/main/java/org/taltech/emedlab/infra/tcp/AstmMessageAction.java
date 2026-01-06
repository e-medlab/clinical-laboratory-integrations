package org.taltech.emedlab.infra.tcp;

public interface AstmMessageAction {
    String onMessage(String incoming) throws Exception;
}
