package server;

import servlets.Servlet;

public interface HTTPServer extends Runnable {
    void addServlet(String httpCommand, String uri, Servlet s);
    void removeServlet(String httpCommand, String uri);
    void start();
    void close();
}
