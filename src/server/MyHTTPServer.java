package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import servlets.ConfLoader;
import servlets.Servlet;

public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final int nThreads;
    private final ExecutorService threadPool;
    private final ConcurrentHashMap<String, Servlet> getServerlet;
    private final ConcurrentHashMap<String, Servlet> postServerlet;
    private final ConcurrentHashMap<String, Servlet> deleteServerlet;
    private final Lock lock;
    private volatile boolean running;

    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
        this.getServerlet = new ConcurrentHashMap<>();
        this.postServerlet = new ConcurrentHashMap<>();
        this.deleteServerlet = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
        this.running = true;
    }

    @Override
    public void addServlet(String httpCommand, String uri, Servlet s) {
        lock.lock();
        try {
            switch (httpCommand) {
                case "GET":
                    getServerlet.put(uri, s);
                    break;
                case "POST":
                    postServerlet.put(uri, s);
                    break;
                case "DELETE":
                    deleteServerlet.put(uri, s);
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
                    getServerlet.remove(uri);
                    break;
                case "POST":
                    postServerlet.remove(uri);
                    break;
                case "DELETE":
                    deleteServerlet.remove(uri);
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
            serverSocket.setSoTimeout(1000);
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    client.setSoTimeout(5000);
                    threadPool.submit(() -> handleClient(client));
                } catch (SocketTimeoutException e) {
                    // Continue on timeout to allow for clean server shutdown
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
                    servlet = findMatchingServlet(getServerlet, requestInfo.getUri());
                    break;
                case "POST":
                    servlet = findMatchingServlet(postServerlet, requestInfo.getUri());
                    if (requestInfo.getUri().equals("/upload")) {
                        servlet = new ConfLoader();
                    }
                    break;
                case "DELETE":
                    servlet = findMatchingServlet(deleteServerlet, requestInfo.getUri());
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
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
