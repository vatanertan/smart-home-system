import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a Smart Camera that calculates storage usage based on time elapsed.
 */
public class SmartCamera extends SmartDevice {
    private double megabytesPerMinute;
    private double totalStorageUsed;
    private LocalDateTime lastSwitchOnTime;

    /**
     * Constructs a SmartCamera.
     *
     * @param name               Unique name of the camera.
     * @param isSwitchedOn       Initial status.
     * @param insertionOrder     Creation order.
     * @param megabytesPerMinute Storage consumption rate per minute.
     */
    public SmartCamera(String name, boolean isSwitchedOn, int insertionOrder, double megabytesPerMinute) {
        super(name, isSwitchedOn, insertionOrder);
        this.megabytesPerMinute = megabytesPerMinute;
        this.totalStorageUsed = 0.0;
    }

    /**
     * Updates storage usage based on active time.
     *
     * @param currentTime Current system time.
     */
    private void updateStorage(LocalDateTime currentTime) {
        if (isSwitchedOn() && lastSwitchOnTime != null) {
            long minutes = ChronoUnit.MINUTES.between(lastSwitchOnTime, currentTime);
            if (minutes > 0) {
                totalStorageUsed += megabytesPerMinute * minutes;
            }
        }
    }

    @Override
    public void switchStatus(boolean newStatus, LocalDateTime currentTime) {
        updateStorage(currentTime);
        super.switchStatus(newStatus, currentTime);
        lastSwitchOnTime = newStatus ? currentTime : null;
    }

    @Override
    public String getDeviceInfo() {
        String timeStr = getSwitchTime() == null ? "null" : TimeController.formatter.format(getSwitchTime());
        return String.format("Smart Camera %s is %s and used %.2f MB of storage so far (excluding present usage), and its time to switch its status is %s.",
                getName(), isSwitchedOn() ? "on" : "off", totalStorageUsed, timeStr);
    }
}