package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import servlets.Servlet;

public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final int nThreads;
    private final ExecutorService threadPool;
    private ConcurrentHashMap<String, Servlet> getServerlet = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> postServerlet = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> deleteServerlet = new ConcurrentHashMap<>();
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

    private BufferedReader getBufferedReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void addServlet(String httpCommand, String uri, Servlet s) {
        lock.lock();
        try {
            switch (httpCommand) {
                case "GET":
                    getServerlet.put(uri, s); // Use getServerlet for GET requests
                    break;
                // Add cases for "POST" and "DELETE", using postServerlet and deleteServerlet respectively
                case "POST":
                    postServerlet.put(uri, s); // Use postServerlet for POST requests
                    break;
                case "DELETE":
                    deleteServerlet.put(uri, s); // Use deleteServerlet for DELETE requests
                    break;
                // Consider adding a default case to handle unexpected httpCommand values
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
                    getServerlet.remove(uri); // Corrected from getServlets to get_map
                    break;
                case "POST":
                    postServerlet.remove(uri); // Corrected from postServlets to postServerlet
                    break;
                case "DELETE":
                    deleteServerlet.remove(uri); // Corrected from deleteServlets to deleteServerlet
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
        // create the server socket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //System.out.println("inside run after sleep");

            // define 1s timeout
            serverSocket.setSoTimeout(1000);

            //System.out.println("inside run after sleep");

            while (running) {
                try {
                    //System.out.println("inside run after sleep");
                    // accept a new connection
                    Socket client = serverSocket.accept();
                    //client.setSoTimeout(1000);
                    // each connected client will go through this procedure when it connects to the server
                    threadPool.submit(() -> {
                        try {
                            // get the client input:
                            BufferedReader reader = getBufferedReader(client);

                            // parse the request
                            RequestParser.RequestInfo ri = RequestParser.parseRequest(reader);
                            ConcurrentHashMap<String, Servlet> servletMap;
                            if (ri != null) {
                                switch (ri.getHttpCommand()) {
                                    case "GET":
                                        servletMap = getServerlet;
                                        break;
                                    case "POST":
                                        servletMap = postServerlet;
                                        break;
                                    case "DELETE":
                                        servletMap = deleteServerlet;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported HTTP command: " + ri.getHttpCommand());
                                }

                                // search for the longest uri match:
                                String longestMatch = "";
                                Servlet servlet = null;
                                for (Map.Entry<String, Servlet> entry : servletMap.entrySet()) {
                                    if (ri.getUri().startsWith(entry.getKey()) && entry.getKey().length() > longestMatch.length()) {
                                        longestMatch = entry.getKey();
                                        servlet = entry.getValue();
                                    }
                                }

                                // if servlet is not null, activate the handle() method
                                if (servlet != null) {
                                    servlet.handle(ri, client.getOutputStream());
                                }
                            }
                            reader.close();
                        } catch (IOException e) {
                            System.out.println("Timeout expired and no request was received.");
                            e.printStackTrace();
                        } finally {
                            // close the client socket
                            try {
                                client.close();
                            } catch (IOException e) {
                                System.out.println("IOException 1");
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    // accept() timeout exception, do nothing
                    // if the server is closed, break
                    if (!running) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // timeout exception, do nothing
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
