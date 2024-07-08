package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static class RequestInfo {
        private String httpCommand;
        private String uri;
        private String[] uriSegments;
        private Map<String, String> parameters;
        private byte[] content;

        // Getters
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

        // Setters
        public void setHttpCommand(String httpCommand) {
            this.httpCommand = httpCommand;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public void setUriSegments(String[] uriSegments) {
            this.uriSegments = uriSegments;
        }

        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }
    }

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        RequestInfo requestInfo = new RequestInfo();

        // Read the request line
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }

        // Split the request line into components
        String[] requestLineComponents = requestLine.split(" ");
        requestInfo.setHttpCommand(requestLineComponents[0]);
        requestInfo.setUri(requestLineComponents[1]);

        // Parse URI segments and parameters
        String[] uriParts = requestInfo.getUri().split("\\?");
        requestInfo.setUriSegments(uriParts[0].substring(1).split("/"));
        if (uriParts.length > 1) {
            requestInfo.setParameters(parseParameters(uriParts[1]));
        } else {
            requestInfo.setParameters(new HashMap<>());
        }

        // Read headers
        String line;
        int contentLength = 0;
        while (!(line = reader.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        // Read content based on the specified logic
        StringBuilder contentBuilder = new StringBuilder();
        while (reader.ready()) {
            contentBuilder.append((char) reader.read());
        }
        requestInfo.setContent(contentBuilder.toString().getBytes());

        // Add parameters if present
        String[] contentParts = contentBuilder.toString().split("\n");
        boolean nextLineAsContent = false;
        String content = ""; // Initialize content variable to store the next line as content
        for (String part : contentParts) {
            if (nextLineAsContent) {
                String trimmedPart = part.trim(); // Trim the part to remove leading and trailing whitespace
                if (!trimmedPart.isEmpty()) {
                    // This line should be added as content
                    content = part + "\n";
                    break; // Found the first non-empty line after a parameter, stop the loop
                }
                // If the line is empty, continue to the next iteration without setting nextLineAsContent to false
                continue;
            }
            String[] keyValue = part.split("=");
            if (keyValue.length > 1) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                requestInfo.getParameters().put(key, value);
                nextLineAsContent = true; // Next line should be treated as content
            }
        }
        // Assuming requestInfo has a method to set content
        if (!content.isEmpty()) {
            requestInfo.setContent(content.getBytes());
        }

        return requestInfo;
    }

    private static Map<String, String> parseParameters(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            params.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
        }
        return params;
    }
}
