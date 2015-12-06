
/**
 * 
 * @author <a href="mailto:zhangyu.xzy@aliyun.com">Ñ¦Óğ</a>
 * @version 1.1 2015Äê12ÔÂ6ÈÕ
 * @since 1.1
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java MyHttpServer rootDir port");
        } else {
            try {
                String rootDir = args[0];
                if (rootDir.endsWith("/")) {
                    rootDir = rootDir.substring(0, rootDir.length() - 1);
                }
                Integer port = Integer.valueOf(args[1]);
                Thread t = new MyHttpServer(rootDir, port);
                t.start();
            } catch (Exception e) {
                System.out.println("Error: port must be a number");
            }
        }
    }
}
