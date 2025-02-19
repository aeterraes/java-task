package aeterraes.command;

import aeterraes.handler.CustomJSONHandler;

public class GetSortedDataCommand implements Command {

    private final CustomJSONHandler handler;

    public GetSortedDataCommand(CustomJSONHandler handler) {
        this.handler = handler;
    }

    @Override
    public void execute() {
        handler.getSortedData().forEach(
                pair -> System.out.println(
                        pair.getKey() + " " + pair.getValue()));
    }
}
