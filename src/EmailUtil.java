public class EmailUtil {





    //根据收件人邮箱地址获取服务器host
    public static String getHost(String to){
        String[] split = to.split("@");
        return "smtp."+split[1];
    }



}
