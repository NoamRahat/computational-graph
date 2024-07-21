package graph;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(byte[] data) {
        this(data, new String(data, StandardCharsets.UTF_8), parseDouble(new String(data, StandardCharsets.UTF_8)), new Date());
    }

    public Message(String asText) {
        this(asText.getBytes(StandardCharsets.UTF_8), asText, parseDouble(asText), new Date());
    }

    public Message(double asDouble) {
        this(String.valueOf(asDouble).getBytes(StandardCharsets.UTF_8), String.valueOf(asDouble), asDouble, new Date());
    }

    private Message(byte[] data, String asText, double asDouble, Date date) {
        this.data = data;
        this.asText = asText;
        this.asDouble = asDouble;
        this.date = date;
    }

    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    public Object asText() {
            // Implement the logic to convert the message to text
            return asText;
    }
}
