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
        htmlLines.add("        canvas { border: 1px solid black; display: block; margin: auto; }");
        htmlLines.add("        .node { position: absolute; padding: 10px; border: 1px solid black; }");
        htmlLines.add("        .topic { background-color: lightblue; border-radius: 0; }");
        htmlLines.add("        .agent { background-color: lightgreen; border-radius: 50%; }");
        htmlLines.add("    </style>");
        htmlLines.add("</head>");
        htmlLines.add("<body>");
        htmlLines.add("    <canvas id='graphCanvas' width='800' height='600'></canvas>");
        htmlLines.add("    <script>");

        // Pass node data to JavaScript
        htmlLines.add("const nodes = {");
        for (Node node : graph) {
            String nodeType = node.getName().startsWith("T") ? "topic" : "agent";
            htmlLines.add(String.format("    '%s': { x: %d, y: %d, type: '%s' },", node.getName(), (int)(Math.random() * 700), (int)(Math.random() * 500), nodeType));
        }
        htmlLines.add("};");

        // Pass edge data to JavaScript
        htmlLines.add("const edges = [");
        for (Node node : graph) {
            for (Node connectedNode : node.getEdges()) {
                htmlLines.add(String.format("    { from: '%s', to: '%s' },", node.getName(), connectedNode.getName()));
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
        htmlLines.add("    ctx.strokeStyle = 'black';");
        htmlLines.add("    ctx.stroke();");
        htmlLines.add("    const headlen = 10;");
        htmlLines.add("    const angle = Math.atan2(toNode.y - fromNode.y, toNode.x - fromNode.x);");
        htmlLines.add("    ctx.beginPath();");
        htmlLines.add("    ctx.moveTo(toNode.x, toNode.y);");
        htmlLines.add("    ctx.lineTo(toNode.x - headlen * Math.cos(angle - Math.PI / 6), toNode.y - headlen * Math.sin(angle - Math.PI / 6));");
        htmlLines.add("    ctx.lineTo(toNode.x - headlen * Math.cos(angle + Math.PI / 6), toNode.y - headlen * Math.sin(angle + Math.PI / 6));");
        htmlLines.add("    ctx.lineTo(toNode.x, toNode.y);");
        htmlLines.add("    ctx.fillStyle = 'black';");
        htmlLines.add("    ctx.fill();");
        htmlLines.add("});");

        htmlLines.add("    </script>");
        htmlLines.add("</body>");
        htmlLines.add("</html>");

        return String.join("\n", htmlLines);
    }
}
