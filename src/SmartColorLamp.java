/**
 * Represents a Smart Lamp that supports both white and color modes.
 */
public class SmartColorLamp extends SmartLamp {
    private Integer colorCode;
    private boolean isColorMode;

    /**
     * Constructor for SmartColorLamp starting in white mode.
     *
     * @param name           Unique name.
     * @param isSwitchedOn   Initial status.
     * @param insertionOrder Creation order.
     */
    public SmartColorLamp(String name, boolean isSwitchedOn, int insertionOrder) {
        super(name, isSwitchedOn, insertionOrder);
        this.isColorMode = false;
        this.colorCode = null;
    }

    /**
     * Sets color and activates color mode.
     *
     * @param colorCode Hexadecimal color code.
     * @throws Exception If color code is invalid.
     */
    public void setColorCode(int colorCode) throws Exception {
        this.colorCode = colorCode;
        this.isColorMode = true;
    }

    /**
     * Sets color and brightness, activating color mode.
     *
     * @param colorCode Hex color.
     * @param brightness Brightness level.
     * @throws Exception If values are out of bounds.
     */
    public void setColor(int colorCode, int brightness) throws Exception {
        setBrightness(brightness);
        this.colorCode = colorCode;
        this.isColorMode = true;
    }

    @Override
    public void setWhite(int kelvin, int brightness) throws Exception {
        super.setWhite(kelvin, brightness);
        this.isColorMode = false;
    }

    @Override
    public void setKelvin(int kelvin) throws Exception {
        super.setKelvin(kelvin);
        this.isColorMode = false;
    }

    @Override
    public String getDeviceInfo() {
        String timeStr = getSwitchTime() == null ? "null" : TimeController.formatter.format(getSwitchTime());
        String valueStr = isColorMode ? String.format("0x%06X", colorCode) : getKelvin() + "K";
        return String.format("Smart Color Lamp %s is %s and its color value is %s with %d%% brightness, and its time to switch its status is %s.",
                getName(), isSwitchedOn() ? "on" : "off", valueStr, getBrightness(), timeStr);
    }
}