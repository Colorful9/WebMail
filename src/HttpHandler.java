import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 解析，处理http请求
 *
 */
public class HttpHandler implements Runnable {

    //负责解析http请求
    private final HttpParser httpParser;

    //负责保存邮件到本地
    private final EmaiSaver emaiSaver;

    public HttpHandler(Socket clientSocket) throws IOException {

        httpParser = new HttpParser(clientSocket);
        emaiSaver = new EmaiSaver(httpParser);

    }

    @Override
    public void run() {


            try {



            Socket socket = httpParser.getSocket();

            //获取请求ip地址
            InetAddress clientAddress = socket.getInetAddress();
            String clientIp = clientAddress.getHostAddress();


            //读取请求类型
            httpParser.readMethod();

            //不是post请求就忽略
            if (!httpParser.isPOSTMethod()) {
                return;
            }

            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String flag;

            System.out.println("连接成功！");
            String formattedTime = currentTime.format(formatter);
            System.out.println("当前时间: " + formattedTime);
            System.out.println("发送方ip地址为：" + clientIp);

            // 读取请求头
            httpParser.readHttpHeader();


            int contentLength = httpParser.getContentLength();
            System.out.println("收到的内容长度为：" + contentLength);


            //读取请求体
            httpParser.readHttpBody();

            String fromemail = httpParser.getFrom();
            String toemail = httpParser.getTo();
            String[] toEmails = EmailUtil.getEmaiAddress(toemail);
            Boolean check = HttpParser.emailCheck(fromemail);
            for (String toEmail : toEmails) {
                check = check & HttpParser.emailCheck(toEmail);
            }

            if(!check){
                System.out.println("邮箱格式错误，请检查您的输入！");
                return;
            }



            //保存邮件到本地
            emaiSaver.SaveEmail();

            //发送邮件
             HashMap<String,String> hashMap = new HashMap();
             hashMap.put("from",httpParser.getFrom());
             hashMap.put("to",httpParser.getTo());
             hashMap.put("password",httpParser.getPassword());
             hashMap.put("subject",httpParser.getSubject());
             hashMap.put("body",httpParser.getBody());

             EmailClient emailClient = new EmailClient(hashMap);
             System.out.println(httpParser.getSsl());
             flag = emailClient.sendEmail(httpParser.getSsl());







            // httpParser.show();


            //不响应的话浏览器会重复发送
            // 响应请求
            httpParser.response(flag);


        } catch (IOException e) {


            System.out.println("Http错误: " + e.getMessage());


        } finally {

                try {
                    httpParser.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }



    }
}
