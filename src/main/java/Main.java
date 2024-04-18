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
