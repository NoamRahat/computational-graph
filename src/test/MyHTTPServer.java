package test;

import test.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class MyHTTPServer extends Thread implements HTTPServer {
    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final Map<String, Servlet> getServlets = new ConcurrentHashMap<>();
    private final Map<String, Servlet> postServlets = new ConcurrentHashMap<>();
    private final Map<String, Servlet> deleteServlets = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public MyHTTPServer(int port, int maxThreads) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(maxThreads);
    }

    @Override
    public void addServlet(String httpCommand, String uri, Servlet servlet) {
        switch (httpCommand.toUpperCase()) {
            case "GET":
                getServlets.put(uri, servlet);
                break;
            case "POST":
                postServlets.put(uri, servlet);
                break;
            case "DELETE":
                deleteServlets.put(uri, servlet);
                break;
        }
    }

    @Override
    public void removeServlet(String httpCommand, String uri) {
        switch (httpCommand.toUpperCase()) {
            case "GET":
                getServlets.remove(uri);
                break;
            case "POST":
                postServlets.remove(uri);
                break;
            case "DELETE":
                deleteServlets.remove(uri);
                break;
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (!running) break;
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
            Servlet servlet = getMatchingServlet(requestInfo.getHttpCommand(), requestInfo.getUri());
            if (servlet != null) {
                servlet.handle(requestInfo, clientSocket.getOutputStream());
            } else {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-Type: text/plain");
                out.println("Content-Length: 0");
                out.println();
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Servlet getMatchingServlet(String httpCommand, String uri) {
        Map<String, Servlet> servletMap;
        switch (httpCommand.toUpperCase()) {
            case "GET":
                servletMap = getServlets;
                break;
            case "POST":
                servletMap = postServlets;
                break;
            case "DELETE":
                servletMap = deleteServlets;
                break;
            default:
                return null;
        }
        List<String> matchingKeys = servletMap.keySet().stream()
                .filter(uri::startsWith)
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .collect(Collectors.toList());
        return matchingKeys.isEmpty() ? null : servletMap.get(matchingKeys.get(0));
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void close() throws IOException {
        running = false;
        serverSocket.close();
        threadPool.shutdown();
    }
}
