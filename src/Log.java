import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class Log {
    public static void LogDate(String filename, String input) {
        long timestamp = System.currentTimeMillis();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(input +"     " + timestamp + "\n");
            writer.newLine();
            writer.flush();

        } catch (IOException e) {

            System.out.println("日志保存失败: " + e.getMessage());
        }

    }
}