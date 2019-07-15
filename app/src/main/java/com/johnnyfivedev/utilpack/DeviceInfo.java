package com.johnnyfivedev.utilpack;


public class DeviceInfo {

    private String appVersion;

    private String deviceModel;

    private String osVersion;

    private String screenResolution;


    public DeviceInfo(String appVersion, String deviceModel,
                      String osVersion, String screenResolution) {
        this.appVersion = appVersion;
        this.osVersion = osVersion;
        this.screenResolution = screenResolution;
        this.deviceModel = deviceModel;
    }

    //region ===================== Getters ======================

    public String getAppVersion() {
        return appVersion;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getOsVesion() {
        return osVersion;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    //endregion
}
