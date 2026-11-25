import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a Smart Plug that monitors energy consumption based on ampere values.
 */
public class SmartPlug extends SmartDevice {
    private double ampere;
    private boolean isPluggedIn;
    private double totalEnergyConsumed;
    private LocalDateTime lastSwitchOnTime;
    private static final double VOLTAGE = 220.0;

    /**
     * Constructor for a SmartPlug without an initial item plugged in.
     *
     * @param name           Unique name of the plug.
     * @param isSwitchedOn   Initial status.
     * @param insertionOrder Creation order.
     */
    public SmartPlug(String name, boolean isSwitchedOn, int insertionOrder) {
        super(name, isSwitchedOn, insertionOrder);
        this.totalEnergyConsumed = 0.0;
        this.ampere = 0.0;
        this.isPluggedIn = false;
    }

    /**
     * Constructor for a SmartPlug with an item plugged in.
     *
     * @param name           Unique name of the plug.
     * @param isSwitchedOn   Initial status.
     * @param insertionOrder Creation order.
     * @param ampere         Ampere draw of the plugged item.
     */
    public SmartPlug(String name, boolean isSwitchedOn, int insertionOrder, double ampere) {
        super(name, isSwitchedOn, insertionOrder);
        this.totalEnergyConsumed = 0.0;
        this.ampere = ampere;
        this.isPluggedIn = true;
    }

    /**
     * Calculates and updates the total consumed energy based on active time.
     *
     * @param currentTime Current system time.
     */
    private void updateConsumption(LocalDateTime currentTime) {
        if (isSwitchedOn() && isPluggedIn && lastSwitchOnTime != null) {
            long minutes = ChronoUnit.MINUTES.between(lastSwitchOnTime, currentTime);
            if (minutes > 0) {
                totalEnergyConsumed += (VOLTAGE * ampere) * (minutes / 60.0);
            }
        }
    }

    @Override
    public void switchStatus(boolean newStatus, LocalDateTime currentTime) {
        updateConsumption(currentTime);
        super.switchStatus(newStatus, currentTime);
        lastSwitchOnTime = (newStatus && isPluggedIn) ? currentTime : null;
    }

    /**
     * Attaches an item to the plug.
     *
     * @param ampere      Ampere draw of the new item.
     * @param currentTime Current system time.
     * @throws Exception If an item is already plugged in or ampere is invalid.
     */
    public void plugIn(double ampere, LocalDateTime currentTime) throws Exception {
        if (this.isPluggedIn) throw new ItemAlreadyPluggedInException();
        if (ampere <= 0) throw new InvalidAmpereValueException();
        
        updateConsumption(currentTime);
        this.ampere = ampere;
        this.isPluggedIn = true;
        if (isSwitchedOn()) lastSwitchOnTime = currentTime;
    }

    /**
     * Removes the current item from the plug.
     *
     * @param currentTime Current system time.
     * @throws Exception If no item is plugged in.
     */
    public void plugOut(LocalDateTime currentTime) throws Exception {
        if (!this.isPluggedIn) throw new NoItemToPlugOutException();
        updateConsumption(currentTime);
        this.isPluggedIn = false;
        this.lastSwitchOnTime = null;
    }

    @Override
    public String getDeviceInfo() {
        String timeStr = getSwitchTime() == null ? "null" : TimeController.formatter.format(getSwitchTime());
        return String.format("Smart Plug %s is %s and consumed %.2fW so far (excluding present usage), and its time to switch its status is %s.",
                getName(), isSwitchedOn() ? "on" : "off", totalEnergyConsumed, timeStr);
    }
}