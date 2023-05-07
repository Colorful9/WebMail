import java.io.FileWriter;
import java.io.IOException;

public class EmaiSaver {

    private final HttpParser httpParser;
    public EmaiSaver(HttpParser httpParser){
        this.httpParser = httpParser;
    }

    public void SaveEmail(){

        String from = httpParser.getFrom();
        String to = httpParser.getTo();
        String password = httpParser.getPassword();
        String subject = httpParser.getSubject();
        String body = httpParser.getBody();


        //文件名设为 主题+时间戳
        //保存到本地同一目录下
        long timestamp = System.currentTimeMillis();
        String filename = subject + "_" + timestamp + ".txt";

        try (FileWriter writer = new FileWriter(filename)) {


            writer.write("From: " + from + "\n");
            writer.write("To: " + to + "\n");
            writer.write("Password: " + password + "\n");
            writer.write("Subject: " + subject + "\n\n");
            writer.write(body);

            System.out.println("邮件已保存到: " + filename);


        } catch (IOException e) {

            System.out.println("邮件保存失败: " + e.getMessage());

        }

    }





}
