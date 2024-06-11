package test;

import java.io.IOException;
import java.io.OutputStream;

import test.RequestParser.RequestInfo;

public interface Servlet {
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;
    void close() throws IOException;
}
