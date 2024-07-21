package views;

import graph.Graph;
import configs.Node;

import java.util.ArrayList;
import java.util.List;

public class HtmlGraphWriter {
    public static String getGraphHTML(Graph graph) {
        List<String> htmlLines = new ArrayList<>();
        htmlLines.add("<!DOCTYPE html>");
        htmlLines.add("<html lang='en'>");
        htmlLines.add("<head>");
        htmlLines.add("    <meta charset='UTF-8'>");
        htmlLines.add("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        htmlLines.add("    <title>Computational Graph</title>");
        htmlLines.add("    <style>");
        htmlLines.add("        .node { position: absolute; padding: 10px; border: 1px solid black; }");
        htmlLines.add("        .topic { background-color: lightblue; border-radius: 0; }");
        htmlLines.add("        .agent { background-color: lightgreen; border-radius: 50%; }");
        htmlLines.add("    </style>");
        htmlLines.add("</head>");
        htmlLines.add("<body>");
        htmlLines.add("    <div id='graph' style='position: relative; width: 800px; height: 600px;'>");

        for (Node node : graph) {
            String nodeType = node.getName().startsWith("T") ? "topic" : "agent";
            String shape = nodeType.equals("topic") ? "square" : "circle";
            htmlLines.add(String.format("        <div class='node %s' style='left: %dpx; top: %dpx;'>%s</div>",
                    nodeType, (int)(Math.random() * 700), (int)(Math.random() * 500), node.getName()));
        }

        htmlLines.add("    </div>");
        htmlLines.add("    <script>");
        htmlLines.add("        // Add JavaScript for drawing edges between nodes");
        htmlLines.add("    </script>");
        htmlLines.add("</body>");
        htmlLines.add("</html>");

        return String.join("\n", htmlLines);
    }
}
