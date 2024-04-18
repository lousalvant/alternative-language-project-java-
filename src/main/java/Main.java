import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Cell {
    private String oem;
    private String model;
    private int launchAnnounced;
    private Object launchStatus;
    private String bodyDimensions;
    private Float bodyWeight;
    private String bodySim;
    private String displayType;
    private Float displaySize;
    private String displayResolution;
    private String featuresSensors;
    private String platformOs;

    public Cell(String oem, String model, String launchAnnounced, String launchStatus, String bodyDimensions, String bodyWeight,
                String bodySim, String displayType, String displaySize, String displayResolution, String featuresSensors, String platformOs) {
        this.oem = oem;
        this.model = model;
        this.launchAnnounced = parseYear(launchAnnounced);
        this.launchStatus = parseYearOrString(launchStatus);
        this.bodyDimensions = parseString(bodyDimensions);
        this.bodyWeight = parseWeight(bodyWeight);
        this.bodySim = parseSim(bodySim);
        this.displayType = parseString(displayType);
        this.displaySize = parseFloat(displaySize);
        this.displayResolution = parseString(displayResolution);
        this.featuresSensors = parseString(featuresSensors);
        this.platformOs = parsePlatformOs(platformOs);
    }

    private int parseYear(String value) {
        try {
            return Integer.parseInt(value.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Object parseYearOrString(String value) {
        if (value.matches("\\d{4}")) {
            return parseYear(value);
        } else {
            return value;
        }
    }

    private String parseString(String value) {
        return value.equals("-") ? null : value;
    }

    private Float parseWeight(String value) {
        Matcher matcher = Pattern.compile("(\\d+\\.?\\d*)").matcher(value);
        if (matcher.find()) {
            return Float.parseFloat(matcher.group());
        } else {
            return null;
        }
    }

    private String parseSim(String value) {
        return value.equalsIgnoreCase("no") ? null : value;
    }

    private Float parseFloat(String value) {
        Matcher matcher = Pattern.compile("(\\d+\\.?\\d*)").matcher(value);
        if (matcher.find()) {
            return Float.parseFloat(matcher.group());
        } else {
            return null;
        }
    }

    private String parsePlatformOs(String value) {
        return value.split(",")[0].trim();
    }

    @Override
    public String toString() {
        return String.format("%-15s | %-20s | %-15d | %-15s | %-20s | %-15s | %-15s | %-20s | %-15s | %-20s | %-20s | %-20s",
                oem, model, launchAnnounced, launchStatus, bodyDimensions, bodyWeight, bodySim, displayType, displaySize,
                displayResolution, featuresSensors, platformOs);
    }

    public Float getBodyWeight() {
        return bodyWeight;
    }

    public String getOem() {
        return oem;
    }

    public int getLaunchAnnounced() {
        return launchAnnounced;
    }

    public Object getLaunchStatus() {
        return launchStatus;
    }

    public String getFeaturesSensors() {
        return featuresSensors;
    }

    public String getModel() {
        return model;
    }
}

public class Main {
    public static List<Cell> readCsv(String filename) throws IOException {
        List<Cell> cells = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 12) { // Check if data has at least 12 elements
                    cells.add(createCellFromData(data));
                } else {
                    System.err.println("Skipping line: " + line + " - Insufficient data columns.");
                }
            }
        }
        return cells;
    }

    private static Cell createCellFromData(String[] data) {
        return new Cell(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10], data[11]);
    }

    public static void main(String[] args) {
        try {
            String filename = "src/main/java/cells.csv";
            List<Cell> cells = readCsv(filename);
            // Output data
            System.out.println("OEM            | Model               | Launch Announced | Launch Status   | Body Dimensions    | Body Weight | Body SIM      | Display Type       | Display Size | Display Resolution | Features Sensors   | Platform OS        ");
            for (Cell cell : cells) {
                System.out.println(cell);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
