package aeterraes.command;

import java.util.HashMap;
import java.util.Map;

public class CommandInvoker {
    private final Map<String, Command> commands = new HashMap<>();

    public void register(String key, Command command) {
        commands.put(key, command);
    }

    public void execute(String key) {
        Command command = commands.get(key);
        if (command != null) {
            command.execute();
        } else {
            System.out.println("Команда не найдена");
        }
    }
}

