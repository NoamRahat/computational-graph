package servlets;

import configs.GenericConfig;
import graph.Graph;
import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfLoader implements Servlet {

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        try {
            if (ri.getContent() == null || ri.getContent().length == 0) {
                sendErrorResponse(toClient, "No content received", 400);
                return;
            }

            System.out.println("Request content: " + new String(ri.getContent(), StandardCharsets.UTF_8));

            // Extract boundary from Content-Type header
            String contentType = ri.getContentType();
            String boundary = null;
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                String[] parts = contentType.split(";");
                for (String part : parts) {
                    if (part.trim().startsWith("boundary=")) {
                        boundary = "--" + part.split("=", 2)[1].trim();
                        break;
                    }
                }
            }

            if (boundary == null) {
                sendErrorResponse(toClient, "Invalid Content-Type or missing boundary", 400);
                return;
            }

            // Read and parse the multipart data
            Map<String, String> formData = parseMultipartFormData(ri.getContent(), boundary);
            String configContent = formData.get("configFile");

            if (configContent == null) {
                sendErrorResponse(toClient, "Config file not found in the request", 400);
                return;
            }

            GenericConfig config = new GenericConfig();
            config.setConfFile(configContent);
            config.create();

            Graph graph = new Graph();
            graph.createFromTopics();

            String graphHtml = HtmlGraphWriter.getGraphHTML(graph);

            // Send a proper HTTP response
            String response = "HTTP/1.1 200 OK\r\n" +
                              "Content-Type: text/html\r\n" +
                              "Content-Length: " + graphHtml.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                              "\r\n" +
                              graphHtml;

            toClient.write(response.getBytes(StandardCharsets.UTF_8));
            toClient.flush();

            System.out.println("Response sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(toClient, "Internal server error: " + e.getMessage(), 500);
        }
    }

    private void sendErrorResponse(OutputStream toClient, String message, int statusCode) throws IOException {
        String htmlResponse = "<html><body><h1>" + statusCode + " Error</h1><p>" + message + "</p></body></html>";
        String response = "HTTP/1.1 " + statusCode + " Error\r\n" +
                          "Content-Type: text/html\r\n" +
                          "Content-Length: " + htmlResponse.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                          "\r\n" +
                          htmlResponse;
    
        toClient.write(response.getBytes(StandardCharsets.UTF_8));
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }

    private Map<String, String> parseMultipartFormData(byte[] content, String boundary) throws IOException {
        Map<String, String> formData = new HashMap<>();
        InputStream inputStream = new ByteArrayInputStream(content);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        StringBuilder fileContent = new StringBuilder();
        boolean readingFileContent = false;
        String currentFieldName = null;

        while ((line = reader.readLine()) != null) {
            System.out.println("Parsing line: " + line);

            if (line.startsWith(boundary)) {
                if (readingFileContent) {
                    formData.put(currentFieldName, fileContent.toString().trim());
                    fileContent.setLength(0);
                    readingFileContent = false;
                }
                // Reset for next part
                currentFieldName = null;
            } else if (line.startsWith("Content-Disposition:")) {
                String[] tokens = line.split(";");
                for (String token : tokens) {
                    String[] keyValue = token.trim().split("=");
                    if (keyValue.length == 2 && keyValue[0].equals("name")) {
                        currentFieldName = keyValue[1].replace("\"", "").trim();
                        System.out.println("Field name extracted: " + currentFieldName);
                        break;
                    }
                }
            } else if (line.trim().isEmpty()) {
                readingFileContent = true;
            } else if (readingFileContent) {
                fileContent.append(line).append("\n");
            }
        }

        // Add the last file content if available
        if (readingFileContent && currentFieldName != null) {
            formData.put(currentFieldName, fileContent.toString().trim());
        }

        System.out.println("Parsed formData: " + formData);
        return formData;
    }
}
