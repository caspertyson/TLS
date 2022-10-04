import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.ldap.*;
import javax.net.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class MyTLSFileServer{
    static FileInputStream fis;
    static BufferedOutputStream writer;

    private static ServerSocketFactory getSSF(){
        try{
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");

                char[] passphrase = "casper".toCharArray();

                ks.load(new FileInputStream("server.jks"),passphrase);

                kmf.init(ks, passphrase);    
                ctx.init(kmf.getKeyManagers(), null, null);

                SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
                return ssf;
        }
        catch(Exception e){
            System.err.println("Error: " + e);
        }
        return null;
    }
    public static void main(String args[]){
        try{
            ServerSocketFactory ssf = getSSF();

            SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(0);
            System.out.println(ss);
            String EnabledProtocols[] = {"TLSv1.3", "TLSv1.2"};

            ss.setEnabledProtocols(EnabledProtocols);

            SSLSocket s = (SSLSocket)ss.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String request = reader.readLine();
            byte[] byteArray = new byte[1000];
            File file = new File(request);
            writer = new BufferedOutputStream(s.getOutputStream());

            if(file.exists()){
                fis = new FileInputStream(file);
                int read;
                while((read = fis.read(byteArray)) != -1){
                    writer.write(byteArray, 0, read);
                }
                writer.flush();
                writer.close();
                s.close();
                fis.close();
            }
            else{
                writer.flush();
                writer.close();
                s.close();
                fis.close();
                System.out.println("File Not Found");
            }

            System.out.println("this happened!");
        }
        catch(Exception e){
            System.err.println("Error Main: " + e);
        }
    }
}
