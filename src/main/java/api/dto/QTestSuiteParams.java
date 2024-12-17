package api.dto;

public class QTestSuiteParams {
    private final String suiteName;
    private final String startDate;
    private final String endDate;
    private final String environmentValue;
    private final String environmentName;
    private final String description;
    private final String deviceValue;
    private final String deviceName;
    private final String browserValue;
    private final String browserName;
    private final String appValue;
    private final String appName;
    public QTestSuiteParams(String suiteName, String startDate, String endDate, String environmentValue,
                                 String environmentName, String description, String deviceValue, String deviceName,
                                 String browserValue, String browserName, String appValue, String appName) {
        this.suiteName = suiteName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.environmentValue = environmentValue;
        this.environmentName = environmentName;
        this.description = description;
        this.deviceValue = deviceValue;
        this.deviceName = deviceName;
        this.browserValue = browserValue;
        this.browserName = browserName;
        this.appValue = appValue;
        this.appName = appName;
    }

    public String getSuiteName() {
        return this.suiteName;
    }
        public String getStartDate() {
        return this.startDate;
    }
        public String getEndDate() {
        return this.endDate;
    }
        public String getEnvironmentValue() {
        return this.environmentValue;
    }
        public String getEnvironmentName() {
        return this.environmentName;
    }
        public String getDescription() {
        return this.description;
    }
        public String getDeviceValue() {
        return this.deviceValue;
    }
        public String getDeviceName() {
        return this.deviceName;
    }
        public String getBrowserValue() {
        return this.browserValue;
    }
        public String getBrowserName() {
        return this.browserName;
    }
        public String getAppValue() {
        return this.appValue;
    }
        public String getAppName() {
        return this.appName;
    }

}
