package views;

import graph.Graph;
import configs.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        htmlLines.add("        canvas { border: 1px solid black; display: block; margin: auto; }");
        htmlLines.add("        .node { position: absolute; padding: 10px; border: 1px solid black; }");
        htmlLines.add("        .topic { background-color: lightblue; border-radius: 0; }");
        htmlLines.add("        .agent { background-color: lightgreen; border-radius: 50%; }");
        htmlLines.add("        .info-text { margin-top: 20px; text-align: center; font-size: 14px; color: #333; }");
        htmlLines.add("    </style>");
        htmlLines.add("</head>");
        htmlLines.add("<body>");
        htmlLines.add("    <canvas id='graphCanvas' width='800' height='600'></canvas>");
        htmlLines.add("    <div class='info-text'>");
        htmlLines.add("        Each new upload of a config file will add new Topics and new Agents and update the graph.<br>");
        htmlLines.add("        The colors on the graph's edges are for readability only and have no real meaning.");
        htmlLines.add("    </div>");
        htmlLines.add("    <script>");

        // Predefined list of 10 colors
        String[] colors = {
            "#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd",
            "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"
        };
        Random rand = new Random();

        // Calculate grid positions for nodes
        int rows = (int) Math.ceil(Math.sqrt(graph.size()));
        int cols = rows;
        int index = 0;

        // Pass node data to JavaScript
        htmlLines.add("const nodes = {");
        for (Node node : graph) {
            String nodeType = node.getName().startsWith("T") ? "topic" : "agent";
            int row = index / cols;
            int col = index % cols;
            int x = 100 + col * 150;
            int y = 100 + row * 150;
            htmlLines.add(String.format("    '%s': { x: %d, y: %d, type: '%s' },", node.getName(), x, y, nodeType));
            index++;
        }
        htmlLines.add("};");

        // Pass edge data to JavaScript with random colors
        htmlLines.add("const edges = [");
        for (Node node : graph) {
            for (Node connectedNode : node.getEdges()) {
                String color = colors[rand.nextInt(colors.length)];
                htmlLines.add(String.format("    { from: '%s', to: '%s', color: '%s' },", node.getName(), connectedNode.getName(), color));
            }
        }
        htmlLines.add("];");

        // JavaScript code for drawing nodes and edges
        htmlLines.add("const canvas = document.getElementById('graphCanvas');");
        htmlLines.add("const ctx = canvas.getContext('2d');");
        htmlLines.add("// Draw nodes");
        htmlLines.add("for (let nodeName in nodes) {");
        htmlLines.add("    const { x, y, type } = nodes[nodeName];");
        htmlLines.add("    function drawNode(node, x, y) {");
        htmlLines.add("        ctx.beginPath();");
        htmlLines.add("        if (node.type === 'topic') {");
        htmlLines.add("            ctx.rect(x - 20, y - 20, 40, 40);");
        htmlLines.add("            ctx.fillStyle = 'lightblue';");
        htmlLines.add("        } else {");
        htmlLines.add("            ctx.arc(x, y, 20, 0, 2 * Math.PI);");
        htmlLines.add("            ctx.fillStyle = 'lightgreen';");
        htmlLines.add("        }");
        htmlLines.add("        ctx.fill();");
        htmlLines.add("        ctx.stroke();");
        htmlLines.add("        ctx.fillStyle = 'black';");
        htmlLines.add("        ctx.fillText(node.name, x - 5, y + 5);");
        htmlLines.add("    }");
        htmlLines.add("    drawNode({ name: nodeName, type: type }, x, y);");
        htmlLines.add("}");
        htmlLines.add("// Draw edges");
        htmlLines.add("edges.forEach(edge => {");
        htmlLines.add("    const fromNode = nodes[edge.from];");
        htmlLines.add("    const toNode = nodes[edge.to];");
        htmlLines.add("    ctx.beginPath();");
        htmlLines.add("    ctx.moveTo(fromNode.x, fromNode.y);");
        htmlLines.add("    ctx.lineTo(toNode.x, toNode.y);");
        htmlLines.add("    ctx.strokeStyle = edge.color;");
        htmlLines.add("    ctx.stroke();");
        htmlLines.add("    const headlen = 10;");
        htmlLines.add("    const angle = Math.atan2(toNode.y - fromNode.y, toNode.x - fromNode.x);");
        htmlLines.add("    ctx.beginPath();");
        htmlLines.add("    ctx.moveTo(toNode.x, toNode.y);");
        htmlLines.add("    ctx.lineTo(toNode.x - headlen * Math.cos(angle - Math.PI / 6), toNode.y - headlen * Math.sin(angle - Math.PI / 6));");
        htmlLines.add("    ctx.lineTo(toNode.x - headlen * Math.cos(angle + Math.PI / 6), toNode.y - headlen * Math.sin(angle + Math.PI / 6));");
        htmlLines.add("    ctx.lineTo(toNode.x, toNode.y);");
        htmlLines.add("    ctx.fillStyle = edge.color;");
        htmlLines.add("    ctx.fill();");
        htmlLines.add("});");

        htmlLines.add("    </script>");
        htmlLines.add("</body>");
        htmlLines.add("</html>");

        return String.join("\n", htmlLines);
    }
}
