package aeterraes.command;

import aeterraes.handler.CustomJSONHandler;

public class GetDomainByIpCommand implements Command {
    private final CustomJSONHandler handler;
    private final String ip;

    public GetDomainByIpCommand(CustomJSONHandler handler, String ip) {
        this.handler = handler;
        this.ip = ip;
    }

    @Override
    public void execute() {
        String domain = handler.getDomainByIp(ip);
        System.out.println(domain != null ? domain : "Домен не найден");
    }
}