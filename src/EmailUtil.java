public class EmailUtil {





    public static String getHost(String to){
        String[] split = to.split("@");
        return "smtp."+split[1];
    }



}
