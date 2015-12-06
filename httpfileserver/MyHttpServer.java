
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * a simple http server used to share file
 * 
 * @author <a href="mailto:zhangyu.xzy@aliyun.com">薛羽</a>
 * @version 1.1 2015年12月5日
 * @since 1.1
 */
public class MyHttpServer extends Thread {
    private String    respEncoding = "gbk";   // default response encoding
    private String    reqEncoding  = "utf-8"; // default request encoding
    private int       port         = 80;      // default port
    private String    rootDir      = "D:";    // default file root dir
    public static int pv           = 0;       // visit count

    public MyHttpServer(String rootDir, int port) {
        this.rootDir = rootDir;
        this.port = port;
    }

    public MyHttpServer() {

    }

    public void run() {
        ServerSocket ssc = null;
        try {
            ssc = new ServerSocket(this.port);
            System.out.println("Listen in port: " + port);
            System.out.println("Response encoding: " + respEncoding);
            System.out.println("Server rootDir: " + rootDir);
        } catch (IOException e1) {
            System.out.println("port: " + port + "has been used ,choose another port");
        }
        try {
            while (true) {
                System.out.println("prepared to accpet request...   pv" + pv++);
                Socket sc = ssc.accept();
                new DealThread(sc).start();

            }
        } catch (IOException e) {
            // hidden exception
        }
    }

    /**
     * Thread deal IO with client socket
     * 
     */
    class DealThread extends Thread {
        private Socket sc;      // connection with socket
        private byte[] header;  // http header bytes
        private byte[] content; // http body bytes

        public DealThread(Socket sc) {
            this.sc = sc;
        }

        public void run() {

            try {
                InputStream is = sc.getInputStream();
                OutputStream os = sc.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line1 = br.readLine(); // read request line(读取请求行)
                if (!validate(line1)) {
                    os.write(
                            "HTTP/1.1 200 OK\r\nServer:SherJamYu's Server \r\nContent-length:0\r\nContent-type:text/html\r\n".getBytes(respEncoding));
                    os.write(new byte[0]);
                    return;
                }
                System.out.println("------request line:  " + line1);
                String[] reqlines = line1.split("\\s");
                if (reqlines.length < 3) {
                    os.write(
                            "HTTP/1.1 400 OK\r\nServer:SherJamYu's Server \r\nContent-length:0\r\nContent-type:text/html\r\n".getBytes(respEncoding));
                    os.write("Bad request".getBytes(respEncoding));
                    return;
                }

                String url = reqlines[1]; // request path
                url = URLDecoder.decode(url, reqEncoding);
                System.out.println("request URL:" + url);
                String path = rootDir + url; // request file path

                String html = "<html><title>" + "文件浏览" + "</title><h1>" + url + "</h1><hr><ul>";
                // if request file is a directory
                if (path.endsWith("/")) {
                    File root = new File(path);
                    String h = "HTTP/1.1 200 OK\r\nServer:SherJamYu's Server \r\nContent-length:";
                    if (!root.exists() || !root.isDirectory()) {
                        // file path isn't exist
                        content = "File path resolve error".getBytes(respEncoding);
                        h += content.length + "\r\nContent-Type:text/html" + "\r\n\r\n";
                    } else {
                        File[] files = root.listFiles();
                        String href = "";
                        if (!"/".equals(url)) {
                            href = InetAddress.getLocalHost().getHostAddress() + ":" + port;
                            html += "<li><a href='http://" + href + getUpperPath(url) + "'>.." + "</a></li><br>";
                        }

                        for (int i = 0; i < files.length; i++) {
                            href = InetAddress.getLocalHost().getHostAddress() + ":" + port + url + files[i].getName()
                                    + (files[i].isDirectory() ? "/" : "");
                            html += "<li><a href='http://" + href + "'>" + files[i].getName() + (files[i].isDirectory() ? "/" : "") + "</a></li><br>";
                        }
                        html += "</ul></html>";
                        content = html.getBytes(respEncoding);
                        h += content.length + "\r\nContent-Type:text/html" + "\r\n\r\n";
                    }

                    header = h.getBytes(respEncoding);
                } else {
                    // request file is a file
                    File file = new File(path);
                    String h = "HTTP/1.1 200 OK\r\nServer:SherJamYu's Server \r\nContent-length:";
                    if (!file.exists()) {
                        content = "File does not exist".getBytes(respEncoding);
                        h += content.length + "\r\nContent-Type:text/html" + "\r\n\r\n";
                    } else {
                        FileInputStream fis = new FileInputStream(file);
                        String filename = file.getName();
                        int len = 0;
                        byte[] buf = new byte[1024 * 1024];
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        while ((len = fis.read(buf)) != -1) {
                            baos.write(buf, 0, len);
                        }
                        fis.close();
                        content = baos.toByteArray();
                        baos.close();
                        if (filename.endsWith(".jpg")) { // if it's a jpg file ,show it in browser
                            h += content.length + "\r\nContent-Type:image/jpg\r\nContent-disposition:inline;filename="
                                    + URLEncoder.encode(filename, reqEncoding) + "\r\n\r\n";
                        } else { // download it
                            h += content.length + "\r\nContent-Type:application/octet-stream\r\nContent-disposition:attachment;filename="
                                    + URLEncoder.encode(filename, reqEncoding) + "\r\n\r\n";
                        }

                    }
                    header = h.getBytes(respEncoding);
                }
                os.write(header);
                os.write(content);
            } catch (IOException e) {
                // hidden exception
            }
        }

        private boolean validate(String str) {
            if (str == null || str.length() == 0)
                return false;
            return true;
        }

        /**
         * get upperpath ,example : url /1/2/3/ , return /1/2/
         * 
         * @param url
         * @return
         */
        private String getUpperPath(String url) {
            String rev = new StringBuffer(url).reverse().toString();
            int index = rev.indexOf("/", rev.indexOf("/") + 1);
            rev = rev.substring(index);
            url = new StringBuffer(rev).reverse().toString();
            return url;
        }
    }

}
