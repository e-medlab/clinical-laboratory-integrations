package org.astm.v25.tcp;

import org.dew.comm.IMessage;
import org.dew.comm.IRecord;
import org.dew.comm.astm.ASTMMessage;

import java.util.Arrays;

public class AstmMessageMapper {

    public static IMessage convert(String astmText) throws Exception {
        IMessage message = new ASTMMessage();

        String[] lines = astmText.split("\r\n|\r|\n");
        for (String line : lines) {
            if (line == null || line.isBlank()) continue;

            char recTypeChar = line.charAt(0);
            String[] fields = line.split("\\|", -1);

            String[] body = Arrays.copyOfRange(fields, 1, fields.length);

            IRecord.Type type = mapType(recTypeChar);
            message.addRecord(type, body);
        }

        return message;
    }

    public static String convert(IMessage msg) throws Exception {
        if (msg == null) return null;

        StringBuilder sb = new StringBuilder();
        int n = msg.getRecordsCount();
        for (int i = 0; i < n; i++) {
            IRecord r = msg.getRecord(i);
            char typeChar = mapTypeChar(r.getType());
            String[] fields = r.getData();
            sb.append(typeChar);
            if (fields != null && fields.length > 0) {
                sb.append("|");
                sb.append(String.join("|", fields));
            }
            sb.append("\r");
        }
        return sb.toString();
    }

    private static IRecord.Type mapType(char c) {
        return switch (c) {
            case 'H' -> IRecord.Type.HEADER;
            case 'P' -> IRecord.Type.PATIENT;
            case 'O' -> IRecord.Type.ORDER;
            case 'R' -> IRecord.Type.RESULT;
            case 'L' -> IRecord.Type.TERMINATION;
            case 'Q' -> IRecord.Type.QUERY;
            default -> throw new IllegalArgumentException("Unsupported record type: " + c);
        };
    }

    private static char mapTypeChar(IRecord.Type type) {
        return switch (type) {
            case HEADER -> 'H';
            case PATIENT -> 'P';
            case ORDER -> 'O';
            case RESULT -> 'R';
            case TERMINATION -> 'L';
            case QUERY -> 'Q';
            default -> '?';
        };
    }
}
