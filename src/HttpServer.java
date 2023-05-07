import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 主进程
 * httpServer
 * 接收http连接，并开启新线程处理
 *
 *
 */
public class HttpServer {

    public static void main(String[] args) {



        int port = 80;

        try (ServerSocket serverSocket = new ServerSocket(port)) {


            System.out.println("服务器正在等待……");

            while (true) {


                Socket clientSocket = serverSocket.accept();

                HttpHandler httpHandler = new HttpHandler(clientSocket);

                Thread thread = new Thread(httpHandler);

                thread.start();


            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("HttpServer出错！: " + e.getMessage());
        }
    }
}
