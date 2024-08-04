import server.HTTPServer;
import server.MyHTTPServer;
import servlets.TopicDisplayer;
import servlets.ConfLoader;
import servlets.HtmlLoader;

/**
 * Main class for the Computational Graph project.
 * 
 * This project implements a computational graph using a subscriber/publisher architecture in Java.
 * It consists of agents that subscribe to topics, perform computations, and publish results to other topics.
 * The project includes an HTTP server that handles various types of HTTP requests through servlets.
 * 
 * Project Structure:
 * - configs: Contains configurations for initializing agents from external data.
 * - graph: Implements agent management and communication within the computational graph.
 * - server: Houses the HTTP server implementation and request handling logic.
 * - servlets: Provides servlet implementations for processing specific HTTP requests.
 * 
 * Usage:
 * 1. Clone the repository:
 *    git clone https://github.com/NoamRahat/computational-graph.git
 *    cd computational-graph
 * 
 * 2. Build and run the project:
 *    Ensure JDK is installed. Compile and run using your IDE or command line tools.
 * 
 * 3. Run the HTTP server by running the main function.
 * 
 * 4. Access the application:
 *    Open your browser and go to 'http://localhost:8080/app/index.html'
 *    Follow the instructions on the website.
 * 
 * Example Configuration File:
 * project_biu.configs.PlusAgent
 * A,B
 * C
 * project_biu.configs.IncAgent
 * C
 * D
 * 
 * Each agent is defined by three lines:
 * - Full class name of the agent.
 * - Comma-separated list of topics to subscribe to.
 * - Comma-separated list of topics to publish to.
 * 
 * 
 * This project demonstrates the use of design patterns and architecture to create a complex system
 * using simple yet powerful building blocks.
* */
public class Main {
    public static void main(String[] args) throws Exception {
        HTTPServer server = new MyHTTPServer(8080, 5);
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader());
        server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
        server.start();
        System.in.read();
        server.close();
    }
}
