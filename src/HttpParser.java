import jdk.jfr.DataAmount;
import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Data
public class HttpParser implements AutoCloseable{
    private Socket socket;

    private final BufferedReader in;
    private final PrintWriter out;

    private String httpHeader;
    private String httpLine;
    private String httpBody;

    private String method;

    private int contentLength;

    private String from;
    private String to;
    private String password;
    private String subject;
    private String body;

    private Boolean ssl = false;


    public HttpParser(Socket socket) throws IOException {
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                StandardCharsets.UTF_8), true);

    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public boolean isPOSTMethod() throws IOException {

        return "POST".equals(method);

    }

    public void readMethod() throws IOException {

        // 解析请求行，获取请求方法
        String inputLine = in.readLine();
        //记录请求行
        httpLine = inputLine;

        String[] tokens = inputLine.split("\\s+");
        //记录方法
        method = tokens[0];

    }

    public void readHttpHeader() throws IOException {

        // 读取请求头
        StringBuilder requestHeader = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {

            requestHeader.append(line).append("\r\n");
            if (line.isEmpty()) {
                break;
            }
        }

        //记录请求头
        httpHeader = requestHeader.toString();

        //记录字段contentLength
        int contentLength = 0;
        for (String header : requestHeader.toString().split("\r\n")) {
            if (header.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(header.substring("Content-Length:".length()).trim());
                this.contentLength = contentLength;
                break;
            }
        }




    }

    public void readHttpBody() throws IOException{
        // 读取消息体
        StringBuilder requestBody = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[1024];
            int bytesRead = 0;
            while (bytesRead < contentLength && (bytesRead = in.read(buffer, 0, Math.min(buffer.length, contentLength - bytesRead))) != -1) {
                requestBody.append(buffer, 0, bytesRead);
            }
        }

        //记录消息体
        httpBody = requestBody.toString();

        //解析出几个字段

        // 解析消息体，读取出4个邮件信息字符串
        String[] parts = requestBody.toString().split("&");

        for (String part : parts) {
            String[] pair = part.split("=", 2);
            if (pair.length == 2) {
                String name = pair[0];
                String value = pair[1];
                if (name.equals("from")) {
                    from = URLDecoder.decode(value, StandardCharsets.UTF_8);
                } else if (name.equals("to")) {
                    to = URLDecoder.decode(value, StandardCharsets.UTF_8);
                } else if (name.equals("subject")) {
                    subject = URLDecoder.decode(value, StandardCharsets.UTF_8);
                } else if (name.equals("body")) {
                    body = URLDecoder.decode(value, StandardCharsets.UTF_8);
                } else if(name.equals("password")){
                    password = value;
                } else if (name.equals("ssl")) {
                    if(value.equals("yes"))
                    {
                        ssl = true;
                    }
                }
            }
        }

    }


    public void show(){

        System.out.println(httpLine);
        System.out.println(httpHeader);
        System.out.println(httpBody);



    }

    public void response(){

        String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<html><body><h1>Succeed!</h1></body></html>";
        out.print(response);
        out.flush();
    }

    public static boolean emailCheck(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }







}
