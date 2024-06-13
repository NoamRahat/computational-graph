package test;

import java.io.BufferedReader;
import java.io.IOException;
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
        String requestLine = input.readLine();
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
            String[] paramPairs = uriParts[1].split("&");
            for (String pair : paramPairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                } else {
                    parameters.put(keyValue[0], "");
                }
            }
        }

        int contentLength = 0;
        String line;
        while ((line = input.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        byte[] content = new byte[contentLength];
        if (contentLength > 0) {
            int bytesRead = 0;
            while (bytesRead < contentLength) {
                int result = input.read();
                if (result == -1) break;
                bytesRead += result;
            }
        }

        // Additional parameter extraction from content
        if (contentLength > 0) {
            String contentString = new String(content);
            String[] contentPairs = contentString.split("&");
            for (String pair : contentPairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                } else {
                    parameters.put(keyValue[0], "");
                }
            }
        }

        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
    }
}
