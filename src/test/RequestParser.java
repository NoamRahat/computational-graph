package test;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static class RequestInfo {
        private String httpCommand;
        private String uri;
        private String[] uriSegments;
        private Map<String, String> parameters;
        private byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }

    public static RequestInfo parseRequest(BufferedReader input) throws IOException {
        InputStream rawInput = new ByteArrayInputStream(input.readLine().getBytes());
        PushbackInputStream pushbackInput = new PushbackInputStream(rawInput);
        BufferedReader reader = new BufferedReader(new InputStreamReader(pushbackInput));
        
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid request line");
        }
    
        String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts.length < 2) {
            throw new IOException("Invalid request line format");
        }
    
        String httpCommand = requestLineParts[0];
        String uri = requestLineParts[1];
        String[] uriParts = uri.split("\\?");
        String[] uriSegments = uriParts[0].split("/");
    
        // Remove leading empty segment due to initial slash
        if (uriSegments.length > 0 && uriSegments[0].isEmpty()) {
            uriSegments = Arrays.copyOfRange(uriSegments, 1, uriSegments.length);
        }
    
        Map<String, String> parameters = new HashMap<>();
        if (uriParts.length > 1) {
            String[] params = uriParts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                parameters.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
            }
        }
    
        String line;
        int contentLength = 0;
        byte[] content = null;
    
        // Read headers
        while ((line = input.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Host:")) {
                // Ignore host header
                continue;
            }
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }
    
        // Read additional parameters
        while ((line = input.readLine()) != null && !line.isEmpty()) {
            if (line.contains("=")) {
                String[] keyValue = line.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                } else {
                    parameters.put(keyValue[0], "");
                }
            }
        }
    
        // Read body into content
        content = new byte[12];
        for (int bytesRead = 0; ; bytesRead++) {
            int result = input.read();
            if ((byte) result == '\n') break; // end of stream reached
            content[bytesRead] = (byte) result;
        }
    
        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
    }
}
