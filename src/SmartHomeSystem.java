/**
 * Driver class to execute the Smart Home System application.
 */
public class SmartHomeSystem {
    /**
     * Application entry point.
     *
     * @param args Command line arguments: inputPath and outputPath.
     */
    public static void main(String[] args) {
        if (args.length != 2) return;
        new SmartSystemManager().run(args[0], args[1]);
    }
}