import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataLog {
    private static final String LOG_FILE_PATH = "data_log.txt";

    public static synchronized void logData(String data) {
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write("[" + formattedTime + "] " + data);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
