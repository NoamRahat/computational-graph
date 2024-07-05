package test;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final int nThreads;
    private final ExecutorService threadPool;
    private final Map<String, Servlet> getServlets;
    private final Map<String, Servlet> postServlets;
    private final Map<String, Servlet> deleteServlets;
    private final Lock lock;
    private volatile boolean running;

    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
        this.getServlets = new ConcurrentHashMap<>();
        this.postServlets = new ConcurrentHashMap<>();
        this.deleteServlets = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
        this.running = true;
    }

    @Override
    public void addServlet(String httpCommand, String uri, Servlet s) {
        lock.lock();
        try {
            switch (httpCommand) {
                case "GET":
                    getServlets.put(uri, s);
                    break;
                case "POST":
                    postServlets.put(uri, s);
                    break;
                case "DELETE":
                    deleteServlets.put(uri, s);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeServlet(String httpCommand, String uri) {
        lock.lock();
        try {
            switch (httpCommand) {
                case "GET":
                    getServlets.remove(uri);
                    break;
                case "POST":
                    postServlets.remove(uri);
                    break;
                case "DELETE":
                    deleteServlets.remove(uri);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void start() {
        new Thread(this).start();
    }

    @Override
    public void close() {
        running = false;
        threadPool.shutdown();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
                    threadPool.execute(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (!running) break;  // Stop accepting new connections if server is closing
                }
            }
        } catch (BindException e) {
            System.err.println("Port " + port + " is already in use. Please try a different port.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            System.out.println("Handling client request");

            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(in);
            System.out.println("Parsed request: " + requestInfo.getHttpCommand() + " " + requestInfo.getUri());

            Servlet servlet = null;
            switch (requestInfo.getHttpCommand()) {
                case "GET":
                    servlet = findMatchingServlet(getServlets, requestInfo.getUri());
                    break;
                case "POST":
                    servlet = findMatchingServlet(postServlets, requestInfo.getUri());
                    break;
                case "DELETE":
                    servlet = findMatchingServlet(deleteServlets, requestInfo.getUri());
                    break;
            }

            if (servlet != null) {
                servlet.handle(requestInfo, out);
                System.out.println("Servlet handled the request");
            } else {
                String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
                out.write(notFoundResponse.getBytes());
                System.out.println("No matching servlet found. Sent 404 Not Found.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Servlet findMatchingServlet(Map<String, Servlet> servletMap, String uri) {
        Servlet matchedServlet = null;
        String longestMatch = "";

        for (String registeredUri : servletMap.keySet()) {
            if (uri.startsWith(registeredUri) && registeredUri.length() > longestMatch.length()) {
                longestMatch = registeredUri;
                matchedServlet = servletMap.get(registeredUri);
            }
        }

        return matchedServlet;
    }
}
