package aeterraes.handler;

import java.io.IOException;

public interface CustomHandler {
    void loadFromFile() throws IOException;
    void saveToFile() throws IOException;
}


