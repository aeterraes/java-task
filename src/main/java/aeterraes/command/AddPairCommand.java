package aeterraes.command;

import aeterraes.handler.CustomJSONHandler;

import java.io.IOException;

public class AddPairCommand implements Command {
    private final CustomJSONHandler handler;
    private final String domain;
    private final String ip;

    public AddPairCommand(CustomJSONHandler handler, String domain, String ip) {
        this.handler = handler;
        this.domain = domain;
        this.ip = ip;
    }

    @Override
    public void execute() {
        try {
            if (handler.addPair(domain, ip)) {
                System.out.println("Успешно добавлена пара: " + domain + ":" + ip);
            } else {
                System.out.println("Не удалось добавить пару: " + domain + ":" + ip
                + ". Проверьте правильность введённых данных");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
