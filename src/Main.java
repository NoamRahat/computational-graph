import server.HTTPServer;
import server.MyHTTPServer;
import servlets.TopicDisplayer;
import servlets.ConfLoader;
import servlets.HtmlLoader;

public class Main {
    public static void main(String[] args) throws Exception {
        HTTPServer server = new MyHTTPServer(8080, 5);
        System.out.println("**************   1 ***************");
        server.addServlet("GET", "/publish", new TopicDisplayer());
        System.out.println("**************   2 ***************");

        server.addServlet("POST", "/upload", new ConfLoader());
        System.out.println("**************   3 ***************");
        server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
        System.out.println("**************   4 ***************");
        server.start();
        System.out.println("**************   5 ***************");
        System.in.read();
        server.close();
        System.out.println("done");
    }
}
