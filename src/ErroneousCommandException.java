/**
 * All project-specific exceptions are intentionally kept in this single file.
 *
 * Reason: Making it work while only compiling SmartHomeSystem.java. Keeping the first
 * referenced exception type in a same-named source file lets javac discover and
 * compile this file automatically, which also compiles the remaining exception
 * classes defined below.
 */
/**
 * Base exception type for the smart home project.
 */
abstract class SmartHomeException extends Exception {
    protected SmartHomeException(String message) {
        super(message);
    }
}

class ErroneousCommandException extends SmartHomeException {
    public ErroneousCommandException() { super("ERROR: Erroneous command!"); }
}

class InitialTimeAlreadySetException extends SmartHomeException {
    public InitialTimeAlreadySetException() { super("ERROR: Erroneous command!"); }
}

class InitialDateFormatException extends SmartHomeException {
    public InitialDateFormatException() { super("ERROR: Format of the initial date is wrong! Program is going to terminate!"); }
}

class TimeFormatNotCorrectException extends SmartHomeException {
    public TimeFormatNotCorrectException() { super("ERROR: Time format is not correct!"); }
}

class TimeCannotBeReversedException extends SmartHomeException {
    public TimeCannotBeReversedException() { super("ERROR: Time cannot be reversed!"); }
}

class NothingToChangeException extends SmartHomeException {
    public NothingToChangeException() { super("ERROR: There is nothing to change!"); }
}

class NothingToSkipException extends SmartHomeException {
    public NothingToSkipException() { super("ERROR: There is nothing to skip!"); }
}

class NothingToSwitchException extends SmartHomeException {
    public NothingToSwitchException() { super("ERROR: There is nothing to switch!"); }
}

class DuplicateDeviceNameException extends SmartHomeException {
    public DuplicateDeviceNameException() { super("ERROR: There is already a smart device with same name!"); }
}

class InvalidAmpereValueException extends SmartHomeException {
    public InvalidAmpereValueException() { super("ERROR: Ampere value must be a positive number!"); }
}

class InvalidMegabyteValueException extends SmartHomeException {
    public InvalidMegabyteValueException() { super("ERROR: Megabyte value must be a positive number!"); }
}

class InvalidKelvinValueException extends SmartHomeException {
    public InvalidKelvinValueException() { super("ERROR: Kelvin value must be in range of 2000K-6500K!"); }
}

class InvalidBrightnessValueException extends SmartHomeException {
    public InvalidBrightnessValueException() { super("ERROR: Brightness must be in range of 0%-100%!"); }
}

class InvalidColorCodeValueException extends SmartHomeException {
    public InvalidColorCodeValueException() { super("ERROR: Color code value must be in range of 0x0-0xFFFFFF!"); }
}

class DeviceNotFoundException extends SmartHomeException {
    public DeviceNotFoundException() { super("ERROR: There is not such a device!"); }
}

class SwitchTimeInPastException extends SmartHomeException {
    public SwitchTimeInPastException() { super("ERROR: Switch time cannot be in the past!"); }
}

class AlreadySwitchedOnException extends SmartHomeException {
    public AlreadySwitchedOnException() { super("ERROR: This device is already switched on!"); }
}

class AlreadySwitchedOffException extends SmartHomeException {
    public AlreadySwitchedOffException() { super("ERROR: This device is already switched off!"); }
}

class SameNameNoChangeException extends SmartHomeException {
    public SameNameNoChangeException() { super("ERROR: Both of the names are the same, nothing changed!"); }
}

class NotSmartPlugException extends SmartHomeException {
    public NotSmartPlugException() { super("ERROR: This device is not a smart plug!"); }
}

class NotSmartLampException extends SmartHomeException {
    public NotSmartLampException() { super("ERROR: This device is not a smart lamp!"); }
}

class NotSmartColorLampException extends SmartHomeException {
    public NotSmartColorLampException() { super("ERROR: This device is not a smart color lamp!"); }
}

class ItemAlreadyPluggedInException extends SmartHomeException {
    public ItemAlreadyPluggedInException() { super("ERROR: There is already an item plugged in to that plug!"); }
}

class NoItemToPlugOutException extends SmartHomeException {
    public NoItemToPlugOutException() { super("ERROR: This plug has no item to plug out from that plug!"); }
}