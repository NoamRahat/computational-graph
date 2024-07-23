package configs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;



import graph.Agent;

public class GenericConfig implements Config {
    private String confContent;
    private List<ParallelAgent> agents;

    public GenericConfig() {
        this.agents = new ArrayList<>();
    }

    public void setConfFile(String filePath) {
        // Read all bytes from the file and convert to String
    	try {
    		byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
    		this.confContent = new String(fileBytes, StandardCharsets.UTF_8);
    	} catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
        }

    }

    @Override
    public void create() {
        try {
            String[] lines = confContent.split("\n");
            if (lines.length % 3 != 0) {
                throw new IllegalArgumentException("Invalid configuration content format");
            }

            for (int i = 0; i < lines.length; i += 3) {
                String className = lines[i].trim();
                String[] subs = lines[i + 1].trim().split(",");
                String[] pubs = lines[i + 2].trim().split(",");

                Class<?> clazz = Class.forName(className);
                Agent agent = (Agent) clazz.getConstructor(String[].class, String[].class).newInstance((Object) subs, (Object) pubs);
                ParallelAgent parallelAgent = new ParallelAgent(agent, 10);
                agents.add(parallelAgent);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create configuration", e);
        }
    }

    @Override
    public void close() {
        for (ParallelAgent agent : agents) {
            agent.close();
        }
        agents.clear();
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
