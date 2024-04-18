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
            // Calculate answers to questions
            calculateHighestAvgBodyWeight(cells);
            findPhonesAnnouncedReleasedDifferentYear(cells);
            countPhonesWithSingleSensor(cells);
            findYearWithMostLaunches(cells);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculateHighestAvgBodyWeight(List<Cell> cells) {
        Map<String, Float> avgWeights = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (Cell cell : cells) {
            String oem = cell.getOem();
            Float weight = cell.getBodyWeight();
            if (weight != null) {
                avgWeights.put(oem, avgWeights.getOrDefault(oem, 0f) + weight);
                counts.put(oem, counts.getOrDefault(oem, 0) + 1);
            }
        }
        String highestAvgOem = "";
        float highestAvgWeight = 0;
        for (Map.Entry<String, Float> entry : avgWeights.entrySet()) {
            String oem = entry.getKey();
            float totalWeight = entry.getValue();
            int count = counts.get(oem);
            float avgWeight = totalWeight / count;
            if (avgWeight > highestAvgWeight) {
                highestAvgWeight = avgWeight;
                highestAvgOem = oem;
            }
        }
        System.out.println("OEM with highest average body weight: " + highestAvgOem);
    }

  private static void findPhonesAnnouncedReleasedDifferentYear(List<Cell> cells) {
      System.out.println("Phones announced in one year and released in another:");
      boolean found = false;
      for (Cell cell : cells) {
          int announceYear = cell.getLaunchAnnounced();
          Object releaseStatus = cell.getLaunchStatus();
          if (announceYear != 0 && releaseStatus instanceof Integer && !releaseStatus.equals("Discontinued") && !releaseStatus.equals("Cancelled")) {
              int releaseYear = (int) releaseStatus;
              if (announceYear != releaseYear) {
                  System.out.println("OEM: " + cell.getOem() + ", Model: " + cell.getModel());
                  found = true;
              }
          }
      }
      if (!found) {
          System.out.println("None");
      }
  }


    private static void countPhonesWithSingleSensor(List<Cell> cells) {
        int count = 0;
        for (Cell cell : cells) {
            String featuresSensors = cell.getFeaturesSensors();
            if (featuresSensors != null && featuresSensors.split("\\s*,\\s*").length == 1) {
                count++;
            }
        }
        System.out.println("Number of phones with only one feature sensor: " + count);
    }

    private static void findYearWithMostLaunches(List<Cell> cells) {
        Map<Integer, Integer> yearCounts = new HashMap<>();
        int maxYear = 0;
        int maxCount = 0;
        for (Cell cell : cells) {
            int launchYear = cell.getLaunchAnnounced();
            if (launchYear > 1999) {
                int count = yearCounts.getOrDefault(launchYear, 0) + 1;
                yearCounts.put(launchYear, count);
                if (count > maxCount) {
                    maxCount = count;
                    maxYear = launchYear;
                }
            }
        }
        System.out.println("Year with the most phone launches after 1999: " + maxYear);
    }
}
