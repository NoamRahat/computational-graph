# Computational Graph Project

Welcome to the Computational Graph project repository. This project demonstrates the implementation of a computational graph using a subscriber/publisher architecture in Java. The project includes components for managing agents, configurations, and an HTTP server with servlets for handling various requests.

## Authors 
Noam Rahat  - noamrht@gmail.com  
Samy Nehmad - samy.nehmad1@gmail.com

## Background

Understanding the operation of complex libraries can be greatly enhanced by creating simplified imitations. In this project, we implement a simplified version of several complex mechanisms, combining them into a complete project. This includes design patterns, architecture, a generic server, and different client sides.

We aimed to create a system based on the subscriber/publisher architecture. This system performs complex calculations using computational nodes that operate in parallel, where the output of one node serves as the input for another. For example, one node reads a signal from a video camera and publishes a compressed image. Another node subscribes to this and performs image processing using a neural network to detect humans, publishing a data array of their "skeleton." A third node subscribes to this data and attempts to recognize visual gestures from the skeleton updates, and so on.

### Terminology

- **Message**: A carrier of relevant information.
- **Topic**: A subject to which one can subscribe to receive messages or publish messages for others.
- **Agent**: A software component that can subscribe to topics, perform computations based on received messages, and publish results to other topics. Each agent can subscribe to and publish messages to multiple topics.

### Project Layers

The project is divided into several programming exercises:

1. **Model Layer**: Infrastructure for creating and computing a computational graph.
2. **Controller Layer**: Code library for a generic server that implements a RESTful API.
3. **View Layer**: Web (and mobile) application that allows remote loading and operation of computational graphs via a browser.

## Project Structure

The repository is organized into several directories:

- **configs**: Contains configurations (e.g., `Agent`, `MathExampleConfig`) for initializing agents based on external data.
- **graph**: Implements agent management (`Agent`, `Message`) and communication (`TopicManagerSingleton`) within the computational graph.
- **server**: Houses the HTTP server implementation (`HTTPServer`, `MyHTTPServer`) and request handling (`RequestParser`) logic.
- **servlets**: Provides servlet implementations (`CalculateServlet`, `CalculatorServlet`, etc.) for processing specific HTTP requests.

## Features

- **Agent Management**: Utilizes an agent-based architecture where agents perform specific operations based on configurations loaded from files.
- **HTTP Server**: Implements a multithreaded HTTP server (`MyHTTPServer`) capable of handling GET, POST, and DELETE requests concurrently using servlets.
- **Servlets**: Includes servlet implementations for handling various types of requests (`CalculateServlet`, `CalculatorServlet`, etc.) and generating appropriate responses.

## Getting Started

To get started with the Computational Graph project:

1. **Clone the Repository**: Clone this repository to your local machine:
   ```sh
   git clone https://github.com/NoamRahat/computational-graph.git
   cd computational-graph
   ```

2. **Build and Run**: Ensure you have the Java Development Kit (JDK) installed. Compile and run the project using your IDE or command line tools.

3. **Explore the Code**: Review the code in each directory (`configs`, `graph`, `server`, `servlets`) to understand how agents, configurations, and the HTTP server are implemented.

4. **Test the Server**: Use HTTP clients like Postman or curl to test the server with various HTTP requests (GET, POST, DELETE) against different servlet endpoints.

5. **Contribute**: Enhance existing functionality, add new features, improve documentation, or fix bugs. Submit pull requests for review and collaboration.

## Usage Examples

### Example 1: Running the HTTP Server

To run the HTTP server (`MyHTTPServer`), instantiate it with a port number and number of threads, add servlets for handling requests, and start the server:
```java
MyHTTPServer server = new MyHTTPServer(8080, 10); // Port 8080, 10 threads
server.addServlet("GET", "/calculate", new CalculateServlet());
server.addServlet("POST", "/calculator", new CalculatorServlet());
server.start();
```

### Example 2: Handling GET Request with CalculateServlet

Send a GET request to `http://localhost:8080/calculate?a=10&b=5&op=add` to get the result of adding numbers 10 and 5:
```
Result: 15
```

### Example 3: Running the Project as a Local Server-Client from Main
1. To run the HTTP server, build and execute the project (main function on `Main.java`).
2. Open your browser and navigate to 'http://localhost:8080/app/index.html'.
3. Follow the instructions on the website.

### Configuration File Format

Create a configuration file in the following format:
```
project_biu.configs.PlusAgent
A,B
C
project_biu.configs.IncAgent
C
D
```
Each agent is defined by three lines:
1. Full class name of the agent.
2. Comma-separated list of topics to subscribe to.
3. Comma-separated list of topics to publish to.

## Dependencies

- **Java Development Kit (JDK)**: Ensure JDK 8 or higher is installed to compile and run the project.

## Conclusion

This project demonstrates the implementation of a computational graph using a subscriber/publisher architecture in Java. It includes agent management, a multithreaded HTTP server, and various servlet implementations to handle HTTP requests, showcasing how complex systems can be built using simple yet powerful design patterns.

### [Project Demo](https://www.canva.com/design/DAGM6PesPCs/rQLHkLuuXkE0fylXO559dw/view?utm_content=DAGM6PesPCs&utm_campaign=designshare&utm_medium=link&utm_source=editor)
