package aeterraes.command;

import aeterraes.handler.CustomJSONHandler;

public class GetIpByDomainCommand implements Command {
    private final CustomJSONHandler handler;
    private final String domain;

    public GetIpByDomainCommand(CustomJSONHandler handler, String domain) {
        this.handler = handler;
        this.domain = domain;
    }

    @Override
    public void execute() {
        String ip = handler.getIpByDomain(domain);
        System.out.println(ip != null ? ip : "IP не найден");
    }
}
