document.addEventListener("DOMContentLoaded", function() {
    // Example data; replace with actual data from your Java backend
    const nodes = {
        TC: { x: 100, y: 100, type: 'topic' },
        TA: { x: 200, y: 200, type: 'topic' },
        TB: { x: 300, y: 100, type: 'topic' },
        TD: { x: 400, y: 200, type: 'topic' },
        AIncAgent: { x: 150, y: 150, type: 'agent' },
        APlusAgent: { x: 250, y: 250, type: 'agent' }
    };

    const edges = [
        { from: 'TC', to: 'AIncAgent' },
        { from: 'TA', to: 'AIncAgent' },
        { from: 'TB', to: 'APlusAgent' },
        { from: 'TD', to: 'APlusAgent' },
    ];

    const canvas = document.getElementById('graphCanvas');
    const ctx = canvas.getContext('2d');

    function drawNode(node, x, y) {
        ctx.beginPath();
        if (node.type === 'topic') {
            // Draw a square for topics
            ctx.rect(x - 20, y - 20, 40, 40);
            ctx.fillStyle = 'lightblue';
        } else {
            // Draw a circle for agents
            ctx.arc(x, y, 20, 0, 2 * Math.PI);
            ctx.fillStyle = 'lightgreen';
        }
        ctx.fill();
        ctx.stroke();
        ctx.fillStyle = 'black';
        ctx.fillText(node.name, x - 5, y + 5);
    }

    // Draw nodes
    for (let nodeName in nodes) {
        const { x, y, type } = nodes[nodeName];
        drawNode({ name: nodeName, type: type }, x, y);
    }

    // Draw edges
    edges.forEach(edge => {
        const fromNode = nodes[edge.from];
        const toNode = nodes[edge.to];

        ctx.beginPath();
        ctx.moveTo(fromNode.x, fromNode.y);
        ctx.lineTo(toNode.x, toNode.y);
        ctx.strokeStyle = 'black';
        ctx.stroke();

        // Draw arrowhead
        const headlen = 10; // length of head in pixels
        const angle = Math.atan2(toNode.y - fromNode.y, toNode.x - fromNode.x);
        ctx.beginPath();
        ctx.moveTo(toNode.x, toNode.y);
        ctx.lineTo(toNode.x - headlen * Math.cos(angle - Math.PI / 6), toNode.y - headlen * Math.sin(angle - Math.PI / 6));
        ctx.lineTo(toNode.x - headlen * Math.cos(angle + Math.PI / 6), toNode.y - headlen * Math.sin(angle + Math.PI / 6));
        ctx.lineTo(toNode.x, toNode.y);
        ctx.fillStyle = 'black';
        ctx.fill();
    });
});
