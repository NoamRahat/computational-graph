# Computational Graph Project

Welcome to the Computational Graph project repository. This project showcases the implementation of a computational graph using a subscriber/publisher architecture in Java. The project includes components for managing agents, configurations, and an HTTP server with servlets for handling various types of requests.

## Background

One of the best ways to understand how useful libraries work is by writing a simple imitation of them that demonstrates their operating principles. In this project, we have implemented a simple imitation of several complex mechanisms, step by step, until they come together into one complete project. We have implemented design patterns and architecture, a generic server, and different client sides.

We aimed to implement a system based on the subscriber/publisher architecture. Using this architecture, we implemented a computational graph with the goal of performing complex calculations - built from computational nodes that operate alongside each other, where the outputs of one node serve as the inputs for other nodes. For example, one node reads a signal from a video camera and publishes a compressed image. Another node subscribes to it, and whenever a frame arrives, it performs image processing using a neural network that detects humans and publishes a data array of their "skeleton." A third node subscribes to it, and whenever a skeleton update arrives, it tries to recognize which visual gesture the person in the image is making, and so on.

### Terminology

- **Message**: A message carrying some relevant information.
- **Topic**: A subject to which one can subscribe to receive messages or publish messages for others.
- **Agent**: A software agent that can subscribe to Topics, respond to messages received through them with some computation, and publish the results to other Topics. Each Agent can subscribe to multiple Topics and also publish messages to multiple Topics.

### Project Layers

The project is divided into several small programming exercises:

1. **Model Layer**: Infrastructure for creating and computing a computational graph.
2. **Controller Layer**: Code library for a generic server that allows us to implement a RESTful API.
3. **View Layer**: Web (and mobile) application that enables us to load and operate computational graphs remotely through a browser.

## Project Structure

The repository is organized into several directories:

- **configs**: Contains configurations (`Agent`, `MathExampleConfig`, etc.) for initializing agents based on external data.
- **graph**: Implements agent management (`Agent`, `Message`) and communication (`TopicManagerSingleton`) within the computational graph.
- **server**: Houses the HTTP server implementation (`HTTPServer`, `MyHTTPServer`) and request handling (`RequestParser`) logic.
- **servlets**: Provides servlet implementations (`CalculateServlet`, `CalculatorServlet`, etc.) for processing specific HTTP requests.

## Features

- **Agent Management**: Utilizes an agent-based architecture where agents perform specific operations based on configurations loaded from files.
- **HTTP Server**: Implements a multithreaded HTTP server (`MyHTTPServer`) capable of handling GET, POST, and DELETE requests concurrently using servlets.
- **Servlets**: Includes servlet implementations for handling various types of requests (`CalculateServlet`, `CalculatorServlet`, etc.) and generating appropriate responses.

## Getting Started

To get started with the Computational Graph project:

1. **Clone the Repository**: Clone this repository to your local machine using:
   ```sh
   git clone https://github.com/NoamRahat/computational-graph.git
   cd computational-graph
   ```

2. **Build and Run**: Ensure you have Java Development Kit (JDK) installed. Compile and run the project using your IDE or command line tools.

3. **Explore the Code**: Review the code in each directory (`configs`, `graph`, `server`, `servlets`) to understand how agents, configurations, and the HTTP server are implemented.

4. **Test the Server**: Use HTTP clients like Postman or curl to test the server with various HTTP requests (`GET`, `POST`, `DELETE`) against different servlet endpoints.

5. **Contribute**: Feel free to contribute to the project by enhancing existing functionality, adding new features, improving documentation, or fixing bugs. Submit pull requests for review and collaboration.

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

## Dependencies

- **Java Development Kit (JDK)**: Ensure JDK 8 or higher is installed to compile and run the project.
- **External Libraries**: No external libraries are required beyond standard JDK for basic functionality.

## Conclusion

This project showcases the implementation of a computational graph using a subscriber/publisher architecture in Java. The project includes agent management, a multithreaded HTTP server, and various servlet implementations to handle HTTP requests, demonstrating how complex systems can be built using simple yet powerful design patterns.
