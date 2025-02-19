package aeterraes.handler;
import aeterraes.util.CustomPair;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomJSONHandler implements CustomHandler {
    private final String filePath;
    private final List<CustomPair<String, String>> data;

    private static final String IPV4_PATTERN = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
    private static final String DOMAIN_PATTERN = "^[A-Za-z0-9-]{1,63}\\.[A-Za-z]{2,6}$";

    public CustomJSONHandler(String filePath) {
        this.filePath = filePath;
        this.data = new ArrayList<>();
    }

    @Override
    public void loadFromFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return;

        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
        }

        String fileContentReplaced = fileContent.toString().replaceAll("\\s", "");
        data.clear();

        Pattern pattern = Pattern.compile("\\{\"domain\":\"(.*?)\",\"ip\":\"(.*?)\"}");
        Matcher matcher = pattern.matcher(fileContentReplaced);
        while (matcher.find()) {
            data.add(new CustomPair<>(matcher.group(1), matcher.group(2)));
        }
    }

    public void saveToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("{\"addresses\": [");
            for (int i = 0; i < data.size(); i++) {
                writer.write(String.format("{\"domain\":\"%s\",\"ip\":\"%s\"}",
                        data.get(i).getKey(), data.get(i).getValue()));
                if (i < data.size() - 1) {
                    writer.write(",");
                }
            }
            writer.write("]}");
        }
    }

    public List<CustomPair<String, String>> getData() {
        return data;
    }

    public List<CustomPair<String, String>> getSortedData() {
        List<CustomPair<String, String>> sortedData = new ArrayList<>(data);
        sortedData.sort(Comparator.comparing(CustomPair::getKey));
        return sortedData;
    }

    public boolean isValidDomain(String domain) {
        return Pattern.matches(DOMAIN_PATTERN, domain);
    }

    public boolean isValidAddress(String ip) {
        return Pattern.matches(IPV4_PATTERN, ip);
    }

    public boolean addPair(String domain, String ip) throws IOException {
        if (!isValidDomain(domain) || !isValidAddress(ip)) {
            return false;
        }

        for (CustomPair<String, String> pair : data) {
            if (pair.getKey().equals(domain) || pair.getValue().equals(ip)) {
                return false;
            }
        }

        data.add(new CustomPair<>(domain, ip));
        saveToFile();
        return true;
    }
    /*
    public boolean removePair(String domain, String ip) throws IOException {
        Iterator<CustomPair<String, String>> iterator = data.iterator();
        while (iterator.hasNext()) {
            CustomPair<String, String> pair = iterator.next();
            if (pair.getKey().equals(domain) && pair.getValue().equals(ip)) {
                iterator.remove();
                saveToFile();
                return true;
            }
        }
        return false;
    }
     */

    public boolean removePairByDomain(String domain) throws IOException {
        Iterator<CustomPair<String, String>> iterator = data.iterator();
        while (iterator.hasNext()) {
            CustomPair<String, String> pair = iterator.next();
            if (pair.getKey().equals(domain)) {
                iterator.remove();
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public boolean removePairByIp(String ip) throws IOException {
        Iterator<CustomPair<String, String>> iterator = data.iterator();
        while (iterator.hasNext()) {
            CustomPair<String, String> pair = iterator.next();
            if (pair.getValue().equals(ip)) {
                iterator.remove();
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public String getDomainByIp(String ip) {
        for (CustomPair<String, String> pair : data) {
            if (pair.getValue().equals(ip)) {
                return pair.getKey();
            }
        }
        return null;
    }

    public String getIpByDomain(String domain) {
        for (CustomPair<String, String> pair : data) {
            if (pair.getKey().equals(domain)) {
                return pair.getValue();
            }
        }
        return null;
    }
}
