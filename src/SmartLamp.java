/**
 * Represents a Smart Lamp with adjustable color temperature (Kelvin) and brightness.
 */
public class SmartLamp extends SmartDevice {
    private int kelvin;
    private int brightness;

    /**
     * Constructor for SmartLamp with default values (4000K, 100%).
     *
     * @param name           Unique name of the lamp.
     * @param isSwitchedOn   Initial status.
     * @param insertionOrder Creation order.
     */
    public SmartLamp(String name, boolean isSwitchedOn, int insertionOrder) {
        super(name, isSwitchedOn, insertionOrder);
        this.kelvin = 4000;
        this.brightness = 100;
    }

    /** @return Current Kelvin value. */
    public int getKelvin() { return kelvin; }

    /** @return Current brightness level. */
    public int getBrightness() { return brightness; }

    /**
     * Sets the Kelvin value.
     *
     * @param kelvin Target Kelvin.
     * @throws Exception If Kelvin is out of range (2000-6500).
     */
    public void setKelvin(int kelvin) throws Exception {
        if (kelvin < 2000 || kelvin > 6500) throw new InvalidKelvinValueException();
        this.kelvin = kelvin;
    }

    /**
     * Sets the brightness level.
     *
     * @param brightness Target brightness.
     * @throws Exception If brightness is out of range (0-100).
     */
    public void setBrightness(int brightness) throws Exception {
        if (brightness < 0 || brightness > 100) throw new InvalidBrightnessValueException();
        this.brightness = brightness;
    }

    /**
     * Sets both white ambiance values.
     *
     * @param kelvin     Target Kelvin.
     * @param brightness Target brightness.
     * @throws Exception If values are out of range.
     */
    public void setWhite(int kelvin, int brightness) throws Exception {
        if (kelvin < 2000 || kelvin > 6500) throw new InvalidKelvinValueException();
        if (brightness < 0 || brightness > 100) throw new InvalidBrightnessValueException();
        this.kelvin = kelvin;
        this.brightness = brightness;
    }

    @Override
    public String getDeviceInfo() {
        String timeStr = getSwitchTime() == null ? "null" : TimeController.formatter.format(getSwitchTime());
        return String.format("Smart Lamp %s is %s and its kelvin value is %dK with %d%% brightness, and its time to switch its status is %s.",
                getName(), isSwitchedOn() ? "on" : "off", kelvin, brightness, timeStr);
    }
}