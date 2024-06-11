package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {
    private String confFile;
    private List<ParallelAgent> agents;

    public GenericConfig() {
        this.agents = new ArrayList<>();
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    @Override
    public void create() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(confFile));
            if (lines.size() % 3 != 0) {
                throw new IllegalArgumentException("Invalid configuration file format");
            }

            for (int i = 0; i < lines.size(); i += 3) {
                String className = lines.get(i);
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");

                Class<?> clazz = Class.forName(className);
                Agent agent = (Agent) clazz.getConstructor(String[].class, String[].class).newInstance((Object) subs, (Object) pubs);
                ParallelAgent parallelAgent = new ParallelAgent(agent, 10);
                agents.add(parallelAgent);
            }
        } catch (IOException | ReflectiveOperationException e) {
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
