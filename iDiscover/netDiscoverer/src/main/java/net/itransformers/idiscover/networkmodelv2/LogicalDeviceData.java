package net.itransformers.idiscover.networkmodelv2;

import java.util.List;

/**
 * Created by niau on 8/25/16.
 */
public class LogicalDeviceData {
    private String id;
    private List<DeviceNeighbour> deviceNeighbourList;

    public LogicalDeviceData(List<DeviceNeighbour> deviceNeighbourList) {
        this.deviceNeighbourList = deviceNeighbourList;
    }

    public List<DeviceNeighbour> getDeviceNeighbourList() {
        return deviceNeighbourList;
    }

    public void setDeviceNeighbourList(List<DeviceNeighbour> deviceNeighbourList) {
        this.deviceNeighbourList = deviceNeighbourList;
    }

}
