package aeterraes.command;

import aeterraes.handler.CustomJSONHandler;

import java.io.IOException;

public class DeletePairByIpCommand implements Command {
    private final CustomJSONHandler handler;
    private final String ip;

    public DeletePairByIpCommand(CustomJSONHandler handler, String ip) {
        this.handler = handler;
        this.ip = ip;
    }

    @Override
    public void execute() {
        try {
            if (handler.removePairByIp(ip)) {
                System.out.println("Успешно удалена пара по IP: " + ip);
            } else {
                System.out.println("Не удалось удалить пару по IP: " + ip);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
