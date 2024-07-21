package server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static class RequestInfo {
        private String httpCommand;
        private String uri;
        private String[] uriSegments;
        private Map<String, String> parameters;
        private byte[] content;
        private String contentType;
        private Map<String, String> headers; // New field for headers

        // Constructor
        public RequestInfo() {
            this.headers = new HashMap<>(); // Initialize headers map
        }

        // Getters
        public Map<String, String> getHeaders() {
            return headers;
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

        public String getContentType() {
            return contentType;
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

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public InputStream getContentStream() {
            if (this.content != null && this.content.length > 0) {
                return new ByteArrayInputStream(this.content);
            } else {
                // Handle the case where there is no content
                return null;
            }
        }

        public void addHeader(String key, String value) {
            this.headers.put(key, value);
        }

        @Override
        public String toString() {
            return "RequestInfo{" +
                    "httpCommand='" + httpCommand + '\'' +
                    ", uri='" + uri + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", headers=" + headers +
                    '}';
        }

        public String getHeader(String key) {
            return headers.get(key);
        }
    }

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        RequestInfo requestInfo = new RequestInfo();

        // Read the request line
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }
        System.out.println("Parsed request line: " + requestLine);

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
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            } else if (line.startsWith("Content-Type:")) {
                requestInfo.setContentType(line.split(": ")[1]);
            }
            System.out.println("Header: " + line);
        }
        System.out.println("Content-Length: " + contentLength);

        // Read content based on the specified content length
        if (contentLength > 0) {
            char[] contentChars = new char[contentLength];
            int bytesRead = reader.read(contentChars, 0, contentLength);
            if (bytesRead != contentLength) {
                throw new IOException("Expected " + contentLength + " bytes, but read " + bytesRead);
            }
            requestInfo.setContent(new String(contentChars).getBytes(StandardCharsets.UTF_8));
        } else {
            requestInfo.setContent(new byte[0]);
        }

        System.out.println("Parsed request: " + requestInfo);
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
