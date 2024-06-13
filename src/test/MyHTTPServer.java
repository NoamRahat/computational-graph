package test;

import test.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class MyHTTPServer extends Thread implements HTTPServer {

    private final int port;
    private final int nThreads;
    private final Map<String, Map<String, Servlet>> servlets = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    private volatile boolean running = true;

    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
        this.executor = Executors.newFixedThreadPool(nThreads);
        servlets.put("GET", new ConcurrentHashMap<>());
        servlets.put("POST", new ConcurrentHashMap<>());
        servlets.put("DELETE", new ConcurrentHashMap<>());
    }

    public void addServlet(String httpCommand, String uri, Servlet s) {
        servlets.get(httpCommand.toUpperCase()).put(uri, s);
    }

    public void removeServlet(String httpCommand, String uri) {
        servlets.get(httpCommand.toUpperCase()).remove(uri);
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (!running) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(reader);
            String command = requestInfo.getHttpCommand();
            String uri = requestInfo.getUri();

            Map<String, Servlet> commandServlets = servlets.get(command.toUpperCase());
            Servlet servlet = commandServlets.get(uri);

            if (servlet != null) {
                servlet.handle(requestInfo, out);
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        running = false;
        try {
            new Socket("localhost", port).close(); // Trigger serverSocket.accept() to return
        } catch (IOException ignored) {
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
