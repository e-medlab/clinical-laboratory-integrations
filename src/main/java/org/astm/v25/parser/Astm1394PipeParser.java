package org.astm.v25.parser;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.*;
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
                    } else {
                        parse(dest, seg, encodingCharacters, repNum);
                    }
                } else if (struct instanceof Group) {
                    log.warn("Structure '{}' is a group; ASTM parser expects segments here", name);
                }
                else {
                    log.warn("Structure '{}' is a not a group nor a segment; ignoring in ASTM parser", name);
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

    @Override
    protected String doEncode(Message source) throws HL7Exception {
        Segment h = (Segment) source.get("H");
        String fieldSepString = Terser.get(h, 1, 0, 1, 1);

        if (fieldSepString == null)
            throw new HL7Exception("Can't encode message: H-1 (field separator) is missing");

        char fieldSep = '|';
        if (!fieldSepString.isEmpty()) fieldSep = fieldSepString.charAt(0);

        String encCharString = Terser.get(h, 2, 0, 1, 1);
        EncodingCharacters en = new EncodingCharacters(fieldSep, encCharString + '~');

        StringBuilder out = new StringBuilder();

        for (String name : source.getNames()) {
            if ("H".equals(name)) {
                out.append(encodeH(h, en)).append('\r');
                continue;
            }
            Structure[] reps = source.getAll(name);
            for (Structure s : reps) {
                if (s instanceof Segment) {
                    out.append(encode((Segment) s, en)).append('\r');
                } else if (s instanceof Group) {
                    out.append(encode((Group) s, en)).append('\r');
                }
            }
        }

        return out.toString();
    }

    private String encodeH(Segment h, EncodingCharacters enc) throws HL7Exception {
        StringBuilder result = new StringBuilder();
        char fs = enc.getFieldSeparator();

        // Segment name + field separator
        result.append("H").append(fs);

        // H-2: delimiter definition, written RAW
        Type[] h2Reps = h.getField(2);
        if (h2Reps.length > 0) {
            Primitive p = Terser.getPrimitive(h2Reps[0], 1, 1);
            String delimDef = p.getValue();
            if (delimDef != null) {
                result.append(delimDef);
            }
        }

        // Field separator after H-2
        result.append(fs);

        // H-3..N – encode normally
        int numFields = h.numFields();
        for (int i = 3; i <= numFields; i++) {
            Type[] reps = h.getField(i);
            for (int r = 0; r < reps.length; r++) {
                String fieldText = PipeParser.encode(reps[r], enc);
                result.append(fieldText);
                if (r < reps.length - 1) {
                    result.append(enc.getRepetitionSeparator());
                }
            }
            result.append(fs);
        }

        return result.toString();
    }

}
