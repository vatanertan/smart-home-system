import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Core class that handles command parsing, validation, and system management.
 */
public class SmartSystemManager {
    private List<SmartDevice> devices = new ArrayList<>();
    private int deviceCounter = 0;
    private BufferedWriter writer;
    private boolean terminated = false;

    /**
     * Processes the input file and manages system execution.
     *
     * @param inputPath  Path to input commands.
     * @param outputPath Path to output file.
     */
    public void run(String inputPath, String outputPath) {
        Locale.setDefault(Locale.US);
        try {
            List<String> lines = Files.readAllLines(Paths.get(inputPath));
            writer = new BufferedWriter(new FileWriter(outputPath));
            boolean isFirstCommand = true;
            boolean lastCommandWasZReport = false;

            for (String line : lines) {
                if (line.trim().isEmpty() || terminated) continue;
                String[] commandParts = line.split("\t");
                String command = commandParts[0];

                if (isFirstCommand) {
                    isFirstCommand = false;
                    writer.write("COMMAND: " + line + "\n");
                    if (!command.equals("SetInitialTime") || commandParts.length != 2) {
                        writer.write("ERROR: First command must be set initial time! Program is going to terminate!\n");
                        terminated = true; continue;
                    }
                    try { 
                        TimeController.setInitialTime(commandParts[1]); 
                        writer.write("SUCCESS: Time has been set to " + commandParts[1] + "!\n"); 
                    } catch (Exception e) { 
                        writer.write(e.getMessage() + "\n"); 
                        terminated = true; 
                    }
                    continue;
                }

                lastCommandWasZReport = command.equals("ZReport");
                try {
                    if (!command.equals("ZReport")) writer.write("COMMAND: " + line + "\n");
                    processCommand(command, commandParts, line);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    writer.write("ERROR: Erroneous command!\n");
                } catch (Exception e) {
                    writer.write(e.getMessage() + "\n");
                }
            }
            if (!terminated && !isFirstCommand && !lastCommandWasZReport) printZReport(true, "");
            writer.close();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Dispatches commands to their respective handler methods.
     *
     * @param command The extracted command type.
     * @param parts   The full array of command tokens.
     * @param line    The raw line string.
     * @throws Exception If command structure is invalid or execution fails.
     */
    private void processCommand(String command, String[] parts, String line) throws Exception {
        switch (command) {
            case "SetTime": case "SkipMinutes": case "Nop": handleTime(parts, command); break;
            case "Add": handleAdd(parts); break;
            case "Remove": handleRemove(parts); break;
            case "SetSwitchTime": handleSetSwitch(parts); break;
            case "Switch": handleSwitch(parts); break;
            case "ChangeName": handleName(parts); break;
            case "PlugIn": handlePlugIn(parts); break;
            case "PlugOut": handlePlugOut(parts); break;
            case "SetKelvin": case "SetBrightness": case "SetWhite":
            case "SetColorCode": case "SetColor": handleSettings(parts, command); break;
            case "ZReport": printZReport(false, line); break;
            default: throw new ErroneousCommandException();
        }
    }

    /**
     * Manages time-related commands.
     *
     * @param parts   Command tokens.
     * @param command Specific time command type.
     * @throws Exception On invalid time formats or illogical chronological actions.
     */
    private void handleTime(String[] parts, String command) throws Exception {
        if (command.equals("SetTime")) {
            if (parts.length != 2) throw new ErroneousCommandException();
            LocalDateTime targetTime;
            try {
                targetTime = LocalDateTime.parse(parts[1], TimeController.formatter);
            } catch (Exception e) {
                throw new TimeFormatNotCorrectException();
            }
            TimeController.checkTimeReverse(targetTime);
            if (targetTime.equals(TimeController.currentTime)) throw new NothingToChangeException();
            TimeController.advanceTime(targetTime, devices);
        } else if (command.equals("SkipMinutes")) {
            if (parts.length != 2) throw new ErroneousCommandException();
            int minutes = Integer.parseInt(parts[1]);
            if (minutes < 0) throw new TimeCannotBeReversedException();
            if (minutes == 0) throw new NothingToSkipException();
            TimeController.advanceTime(TimeController.currentTime.plusMinutes(minutes), devices);
        } else {
            LocalDateTime nextEvent = null;
            for (SmartDevice d : devices) {
                if (d.getSwitchTime() != null) {
                    if (nextEvent == null || d.getSwitchTime().isBefore(nextEvent)) nextEvent = d.getSwitchTime();
                }
            }
            if (nextEvent == null) throw new NothingToSwitchException();
            TimeController.advanceTime(nextEvent, devices);
        }
    }

    /**
     * Handles adding new smart devices to the system.
     * Performs strict validation before registering the object or incrementing the ID counter.
     *
     * @param parts Command tokens.
     * @throws Exception On duplicate names, invalid properties, or wrong structure.
     */
    private void handleAdd(String[] parts) throws Exception {
        if (parts.length < 3) throw new ErroneousCommandException();
        String type = parts[1], name = parts[2];

        switch (type) {
            case "SmartPlug":
                if (parts.length < 3 || parts.length > 5) throw new ErroneousCommandException();
                if (getDevice(name) != null) throw new DuplicateDeviceNameException();
                boolean plugOn = parts.length >= 4 ? parseStatus(parts[3]) : false;
                if (parts.length == 5) {
                    double ampere = Double.parseDouble(parts[4]);
                    if (ampere <= 0) throw new InvalidAmpereValueException();
                    SmartPlug sp = new SmartPlug(name, plugOn, deviceCounter, ampere);
                    devices.add(sp);
                    deviceCounter++; // Incremented ONLY if added successfully
                    if(plugOn) sp.switchStatus(true, TimeController.currentTime);
                } else {
                    SmartPlug sp = new SmartPlug(name, plugOn, deviceCounter);
                    devices.add(sp);
                    deviceCounter++;
                    if(plugOn) sp.switchStatus(true, TimeController.currentTime);
                }
                break;
                
            case "SmartCamera":
                if (parts.length < 4 || parts.length > 5) throw new ErroneousCommandException();
                if (getDevice(name) != null) throw new DuplicateDeviceNameException();
                double megabyte = Double.parseDouble(parts[3]);
                if (megabyte <= 0) throw new InvalidMegabyteValueException();
                boolean cameraOn = parts.length == 5 ? parseStatus(parts[4]) : false;
                SmartCamera sc = new SmartCamera(name, cameraOn, deviceCounter, megabyte);
                devices.add(sc);
                deviceCounter++;
                if(cameraOn) sc.switchStatus(true, TimeController.currentTime);
                break;
                
            case "SmartLamp":
                if (parts.length != 3 && parts.length != 4 && parts.length != 6) throw new ErroneousCommandException();
                if (getDevice(name) != null) throw new DuplicateDeviceNameException();
                boolean lampOn = parts.length >= 4 ? parseStatus(parts[3]) : false;
                SmartLamp sl = new SmartLamp(name, lampOn, deviceCounter);
                if (parts.length == 6) {
                    sl.setWhite(Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                }
                devices.add(sl);
                deviceCounter++;
                break;
                
            case "SmartColorLamp":
                if (parts.length != 3 && parts.length != 4 && parts.length != 6) throw new ErroneousCommandException();
                if (getDevice(name) != null) throw new DuplicateDeviceNameException();
                boolean colorLampOn = parts.length >= 4 ? parseStatus(parts[3]) : false;
                SmartColorLamp scl = new SmartColorLamp(name, colorLampOn, deviceCounter);
                if (parts.length == 6) {
                    if (parts[4].startsWith("0x")) {
                        // Use Long to prevent NumberFormatException on huge hex inputs
                        long code = Long.decode(parts[4]);
                        if (code < 0 || code > 0xFFFFFF) throw new InvalidColorCodeValueException();
                        scl.setColor((int)code, Integer.parseInt(parts[5]));
                    } else {
                        scl.setWhite(Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                    }
                }
                devices.add(scl);
                deviceCounter++;
                break;
                
            default: 
                throw new ErroneousCommandException();
        }
    }

    /**
     * Parses the on/off string into a boolean.
     *
     * @param status The string representing status.
     * @return true if "On", false if "Off".
     * @throws Exception If string matches neither.
     */
    private boolean parseStatus(String status) throws Exception {
        if (status.equals("On")) return true; 
        if (status.equals("Off")) return false; 
        throw new ErroneousCommandException();
    }

    /**
     * Removes a device from the system.
     *
     * @param parts Command tokens.
     * @throws Exception If device is not found.
     */
    private void handleRemove(String[] parts) throws Exception {
        if (parts.length != 2) throw new ErroneousCommandException();
        SmartDevice device = getDevice(parts[1]);
        if (device == null) throw new DeviceNotFoundException();
        device.switchStatus(false, TimeController.currentTime);
        writer.write("SUCCESS: Information about removed smart device is as follows:\n" + device.getDeviceInfo() + "\n");
        devices.remove(device);
    }

    /**
     * Sets a scheduled switch time for a device.
     *
     * @param parts Command tokens.
     * @throws Exception If time is invalid or in the past.
     */
    private void handleSetSwitch(String[] parts) throws Exception {
        if (parts.length != 3) throw new ErroneousCommandException();
        SmartDevice device = getDevice(parts[1]);
        if (device == null) throw new DeviceNotFoundException();
        LocalDateTime targetTime = LocalDateTime.parse(parts[2], TimeController.formatter);
        if (targetTime.isBefore(TimeController.currentTime)) throw new SwitchTimeInPastException();
        if (targetTime.equals(TimeController.currentTime)) {
            device.switchStatus(!device.isSwitchedOn(), TimeController.currentTime);
            device.markAutoSwitchTime(TimeController.currentTime);
        } else {
            device.setSwitchTime(targetTime);
        }
    }

    /**
     * Instantly switches a device's status.
     *
     * @param parts Command tokens.
     * @throws Exception If target status equals current status.
     */
    private void handleSwitch(String[] parts) throws Exception {
        if (parts.length != 3) throw new ErroneousCommandException();
        SmartDevice device = getDevice(parts[1]);
        if (device == null) throw new DeviceNotFoundException();
        boolean status = parseStatus(parts[2]);
        if (device.isSwitchedOn() == status) throw status ? new AlreadySwitchedOnException() : new AlreadySwitchedOffException();
        device.switchStatus(status, TimeController.currentTime);
    }

    /**
     * Renames an existing device.
     *
     * @param parts Command tokens.
     * @throws Exception On duplicate names or if original is missing.
     */
    private void handleName(String[] parts) throws Exception {
        if (parts.length != 3) throw new ErroneousCommandException();
        if (parts[1].equals(parts[2])) throw new SameNameNoChangeException();
        SmartDevice device = getDevice(parts[1]);
        if (device == null) throw new DeviceNotFoundException();
        if (getDevice(parts[2]) != null) throw new DuplicateDeviceNameException();
        device.setName(parts[2]);
    }

    /**
     * Plugs an item into a Smart Plug.
     *
     * @param parts Command tokens.
     * @throws Exception If device is not a Smart Plug.
     */
    private void handlePlugIn(String[] parts) throws Exception {
        if (parts.length != 3) throw new ErroneousCommandException();
        SmartDevice device = getDevice(parts[1]);
        if (device == null) throw new DeviceNotFoundException();
        if (!(device instanceof SmartPlug)) throw new NotSmartPlugException();
        ((SmartPlug) device).plugIn(Double.parseDouble(parts[2]), TimeController.currentTime);
    }

    /**
     * Unplugs an item from a Smart Plug.
     *
     * @param parts Command tokens.
     * @throws Exception If device is not a Smart Plug.
     */
    private void handlePlugOut(String[] parts) throws Exception {
        if (parts.length != 2) throw new ErroneousCommandException();
        SmartDevice device = getDevice(parts[1]);
        if (device == null) throw new DeviceNotFoundException();
        if (!(device instanceof SmartPlug)) throw new NotSmartPlugException();
        ((SmartPlug) device).plugOut(TimeController.currentTime);
    }

    /**
     * Modifies settings like Kelvin, Brightness, or Color Code for Lamps.
     *
     * @param parts   Command tokens.
     * @param command Specific setting command.
     * @throws Exception If values are out of bounds or type is wrong.
     */
    private void handleSettings(String[] parts, String command) throws Exception {
        if (parts.length < 3) throw new ErroneousCommandException();
        SmartDevice device = getDevice(parts[1]);
        if (device == null) throw new DeviceNotFoundException();
        
        if (command.startsWith("SetColor")) {
            if (!(device instanceof SmartColorLamp)) throw new NotSmartColorLampException();
            SmartColorLamp colorLamp = (SmartColorLamp) device;
            if (command.equals("SetColorCode")) {
                if (parts.length != 3 || !parts[2].startsWith("0x")) throw new ErroneousCommandException();
                long code = Long.decode(parts[2]);
                if (code < 0 || code > 0xFFFFFF) throw new InvalidColorCodeValueException();
                colorLamp.setColorCode((int)code);
            } else {
                if (parts.length != 4 || !parts[2].startsWith("0x")) throw new ErroneousCommandException();
                long code = Long.decode(parts[2]);
                if (code < 0 || code > 0xFFFFFF) throw new InvalidColorCodeValueException();
                colorLamp.setColor((int)code, Integer.parseInt(parts[3]));
            }
        } else {
            if (!(device instanceof SmartLamp)) throw new NotSmartLampException();
            SmartLamp lamp = (SmartLamp) device;
            if (command.equals("SetKelvin")) {
                if (parts.length != 3) throw new ErroneousCommandException();
                lamp.setKelvin(Integer.parseInt(parts[2]));
            }
            else if (command.equals("SetBrightness")) {
                if (parts.length != 3) throw new ErroneousCommandException();
                lamp.setBrightness(Integer.parseInt(parts[2]));
            }
            else { 
                if (parts.length != 4) throw new ErroneousCommandException(); 
                lamp.setWhite(Integer.parseInt(parts[2]), Integer.parseInt(parts[3])); 
            }
        }
    }

    /**
     * Finds a device in the system by its name.
     *
     * @param name Name of the device.
     * @return The SmartDevice object if found, null otherwise.
     */
    private SmartDevice getDevice(String name) {
        for (SmartDevice d : devices) if (d.getName().equals(name)) return d;
        return null;
    }

    /**
     * Prints the chronological ZReport.
     *
     * @param implicit Indicates if the report was auto-generated.
     * @param line     Original command string.
     * @throws Exception On write errors.
     */
    private void printZReport(boolean implicit, String line) throws Exception {
        if (!implicit) writer.write("COMMAND: " + line + "\n"); else writer.write("ZReport:\n");
        writer.write("Time is:\t" + TimeController.formatter.format(TimeController.currentTime) + "\n");
        Collections.sort(devices); 
        for (SmartDevice d : devices) writer.write(d.getDeviceInfo() + "\n");
    }
}