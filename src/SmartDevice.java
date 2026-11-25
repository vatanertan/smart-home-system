import java.time.LocalDateTime;

/**
 * Abstract base class representing a generic smart device in the system.
 * It implements Comparable to allow sorting based on scheduled switch times.
 */
public abstract class SmartDevice implements Comparable<SmartDevice> {
    private String name;
    private boolean isSwitchedOn;
    private LocalDateTime switchTime;
    private LocalDateTime lastStatusChangeTime;
    private LocalDateTime lastAutoSwitchTime;
    private int insertionOrder;

    /**
     * Constructs a SmartDevice with basic properties.
     *
     * @param name           The unique name of the device.
     * @param isSwitchedOn   Initial power status (true for on, false for off).
     * @param insertionOrder The sequence number indicating when the device was added.
     */
    public SmartDevice(String name, boolean isSwitchedOn, int insertionOrder) {
        this.name = name;
        this.isSwitchedOn = isSwitchedOn;
        this.insertionOrder = insertionOrder;
        this.switchTime = null;
        this.lastStatusChangeTime = null;
        this.lastAutoSwitchTime = null;
    }

    /** @return The name of the smart device. */
    public String getName() { return name; }

    /** @param name The new name to set for the device. */
    public void setName(String name) { this.name = name; }

    /** @return true if the device is on, false otherwise. */
    public boolean isSwitchedOn() { return isSwitchedOn; }

    /** @return The scheduled switch time, or null if not set. */
    public LocalDateTime getSwitchTime() { return switchTime; }

    /** @param switchTime The time at which the device status should automatically change. */
    public void setSwitchTime(LocalDateTime switchTime) { this.switchTime = switchTime; }

    /** @return The internal order in which the device was created. */
    public int getInsertionOrder() { return insertionOrder; }

    /**
     * Toggles the power status and clears the scheduled switch time.
     *
     * @param newStatus   The target power status.
     * @param currentTime The current system time.
     */
    public void switchStatus(boolean newStatus, LocalDateTime currentTime) {
        this.isSwitchedOn = newStatus;
        this.switchTime = null;
        this.lastStatusChangeTime = currentTime;
    }

    /** Records the last automatic switch time for report ordering. */
    public void markAutoSwitchTime(LocalDateTime currentTime) {
        this.lastAutoSwitchTime = currentTime;
        this.lastStatusChangeTime = currentTime;
    }

    /**
     * Generates a descriptive string for the ZReport.
     *
     * @return Formatted device information.
     */
    public abstract String getDeviceInfo();

    /**
     * Compares this device with another for chronological sorting.
     *
     * @param other The other SmartDevice to compare against.
     * @return A negative integer, zero, or a positive integer as this device 
     * is earlier, equal, or later than the other.
     */
 @Override
    public int compareTo(SmartDevice other) {
        if (this.switchTime != null && other.switchTime != null) {
            int timeCompare = this.switchTime.compareTo(other.switchTime);
            if (timeCompare != 0) return timeCompare;
            return Integer.compare(this.insertionOrder, other.insertionOrder);
        }
        if (this.switchTime != null) return -1;
        if (other.switchTime != null) return 1;

        if (this.lastAutoSwitchTime != null && other.lastAutoSwitchTime != null) {
            int timeCompare = other.lastAutoSwitchTime.compareTo(this.lastAutoSwitchTime);
            if (timeCompare != 0) return timeCompare;
            return Integer.compare(this.insertionOrder, other.insertionOrder);
        }
        if (this.lastAutoSwitchTime != null) return -1;
        if (other.lastAutoSwitchTime != null) return 1;
        return Integer.compare(this.insertionOrder, other.insertionOrder);
    }
}
