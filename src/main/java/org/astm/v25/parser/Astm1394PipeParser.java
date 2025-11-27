package org.astm.v25.parser;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.parser.*;
import ca.uhn.hl7v2.util.Terser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Astm1394PipeParser extends PipeParser {

    private static final Logger log = LoggerFactory.getLogger(Astm1394PipeParser.class);

    public static final String ASTM_ENCODING = "ASTM-1394";
    public static final String ASTM_VERSION = "2.5";

    public Astm1394PipeParser(HapiContext context) {
        super(context);
    }

    @Override
    public String getEncoding(String message) {
        if (message != null && message.trim().startsWith("H|")) {
            return ASTM_ENCODING;
        }
        return null;
    }

    @Override
    public boolean supportsEncoding(String encoding) {
        return ASTM_ENCODING.equals(encoding);
    }

    @Override
    public String getDefaultEncoding() {
        return ASTM_ENCODING;
    }

    @Override
    public String getVersion(String message) throws HL7Exception {
        return ASTM_VERSION;
    }

    @Override
    protected Message doParse(String message, String version) throws HL7Exception {
        // TODO: instead of hardcoding, this should be detected inside the message
        EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '@', '\\', '~');

        Message m = instantiateMessage("ASTM_MSG", ASTM_VERSION, true);
        this.parseAstm(m, message, encodingCharacters);
        return m;
    }

    private void parseAstm(Message message, String messageString, EncodingCharacters encodingCharacters) throws HL7Exception {
        message.setParser(this);

        String[] segments = split(messageString, "\r");

        if (segments.length == 0) {
            throw new HL7Exception("Invalid message content: \"" + messageString + "\"");
        }

        String prevName = null;
        int repNum = 1;

        for (String seg : segments) {

            if (seg == null || seg.isEmpty()) {
                continue;
            }

            if (Character.isWhitespace(seg.charAt(0))) {
                seg = stripLeadingWhitespace(seg);
            }

            if (seg.isEmpty()) {
                continue;
            }

            int idx = seg.indexOf(encodingCharacters.getFieldSeparator());
            String name = (idx > 0 ? seg.substring(0, idx) : seg);

            log.trace("Parsing ASTM segment {}", name);

            if (name.equals(prevName)) {
                repNum++;
            } else {
                repNum = 1;
                prevName = name;
            }

            try {
                Structure struct = message.get(name);
                if (struct instanceof Segment) {
                    Segment dest = (Segment) struct;

                    if ("H".equals(name)) {
                        parseAstmSegment(dest, seg, encodingCharacters, repNum);
                    }
                    else {
                        parse(dest, seg, encodingCharacters, repNum);
                    }
                } else {
                    log.warn("Structure '{}' is a group, not a segment; ignoring in ASTM parser", name);
                }
            } catch (HL7Exception e) {
                // Segment name not defined in model: ignore
                log.warn("ASTM_MSG has no segment '{}'; line ignored: {}", name, seg);
            }
        }
    }

    private void parseAstmSegment(Segment destination, String segment, EncodingCharacters encodingCharacters, int repetition) throws HL7Exception {

        int fieldOffset = 0;

        // Treat H like MSH: first "field" is the field separator
        if ("H".equals(destination.getName())) {
            fieldOffset = 1;
            // H-1 = field separator char
            Terser.set(destination, 1, 0, 1, 1, String.valueOf(encodingCharacters.getFieldSeparator()));
        }

        String[] fields = split(segment, String.valueOf(encodingCharacters.getFieldSeparator()));

        for (int i = 1; i < fields.length; ++i) {
            String[] reps = split(fields[i], String.valueOf(encodingCharacters.getRepetitionSeparator()));

            // Special case: H-2 (delimiter definition) should NOT be split on repetitions,
            boolean isDelimiterDefinition = "H".equals(destination.getName()) && i + fieldOffset == 2;
            if (isDelimiterDefinition) {
                reps = new String[]{fields[i]};
            }

            for (int j = 0; j < reps.length; ++j) {
                try {
                    log.trace("Parsing H field {} repetition {}", i + fieldOffset, j);
                    var field = destination.getField(i + fieldOffset, j);
                    if (isDelimiterDefinition) {
                        Terser.getPrimitive(field, 1, 1).setValue(reps[j]);
                    } else {
                        parse(field, reps[j], encodingCharacters);
                    }
                } catch (HL7Exception e) {
                    e.setFieldPosition(i);
                    if (repetition > 1) {
                        e.setSegmentRepetition(repetition);
                    }
                    e.setSegmentName(destination.getName());
                    throw e;
                }
            }
        }
    }
}
