import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for managing system time and processing scheduled switch events.
 */
public class TimeController {
    public static LocalDateTime currentTime = null;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    /**
     * Sets the initial system time.
     *
     * @param timeString String representation of time.
     * @throws Exception If format is wrong or time is already set.
     */
    public static void setInitialTime(String timeString) throws Exception {
        if (currentTime != null) throw new InitialTimeAlreadySetException();
        try { 
            currentTime = LocalDateTime.parse(timeString, formatter); 
        } catch (Exception e) { 
            throw new InitialDateFormatException();
        }
    }

    /**
     * Validates that time is not jumping backwards.
     *
     * @param target The target jump time.
     * @throws Exception If time is reversed.
     */
    public static void checkTimeReverse(LocalDateTime target) throws Exception {
        if (target.isBefore(currentTime)) throw new TimeCannotBeReversedException();
    }

    /**
     * Advances the system clock and triggers scheduled events in order.
     *
     * @param targetTime The time to reach.
     * @param devices    List of active devices.
     */
    public static void advanceTime(LocalDateTime targetTime, List<SmartDevice> devices) {
        Collections.sort(devices);
        while (true) {
            SmartDevice earliestDevice = null;
            for (SmartDevice device : devices) {
                if (device.getSwitchTime() != null && !device.getSwitchTime().isAfter(targetTime)) {
                    earliestDevice = device;
                    break;
                }
            }
            if (earliestDevice == null) break;

            currentTime = earliestDevice.getSwitchTime();
            earliestDevice.switchStatus(!earliestDevice.isSwitchedOn(), currentTime);
            earliestDevice.markAutoSwitchTime(currentTime);
            Collections.sort(devices);
        }
        currentTime = targetTime;
    }
}