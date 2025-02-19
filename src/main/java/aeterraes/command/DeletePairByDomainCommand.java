package aeterraes.command;

import aeterraes.handler.CustomJSONHandler;

import java.io.IOException;

public class DeletePairByDomainCommand implements Command {
    private final CustomJSONHandler handler;
    private final String domain;

    public DeletePairByDomainCommand(CustomJSONHandler handler, String domain) {
        this.handler = handler;
        this.domain = domain;
    }

    @Override
    public void execute() {
        try {
            if (handler.removePairByDomain(domain)) {
                System.out.println("Успешно удалена пара по домену: " + domain);
            } else {
                System.out.println("Не удалось удалить пару по домену: " + domain);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
