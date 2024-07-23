package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

import configs.BinOpAgent;
import configs.Config;
import configs.Node;
import configs.MathExampleConfig;

import server.MyHTTPServer;
import server.RequestParser;
import servlets.CalculateServlet;
import servlets.CalculatorServlet;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.SubServlet;
import servlets.TestServlet;

public class ConfigTest {
    public static void main(String[] args) {
        testConfigInterfaceMethods();
    }

    public static void testConfigInterfaceMethods() {
        Config config = new MathExampleConfig();

        if ("Math Example".equals(config.getName())) {
            System.out.println("testConfigInterfaceMethods getName passed");
        } else {
            System.out.println("testConfigInterfaceMethods getName failed");
        }

        if (1 == config.getVersion()) {
            System.out.println("testConfigInterfaceMethods getVersion passed");
        } else {
            System.out.println("testConfigInterfaceMethods getVersion failed");
        }
    }
}
