import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.ldap.*;
import javax.net.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class MyTLSFileClient{
    public static void main(String args[]){
        try{
            SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            InetAddress IP = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);

            SSLSocket socket = (SSLSocket)factory.createSocket(IP, port);
            SSLParameters params = new SSLParameters();
            params.setEndpointIdentificationAlgorithm("HTTPS");
            socket.setSSLParameters(params);
            socket.startHandshake();

            SSLSession sesh = socket.getSession();
            X509Certificate cert = (X509Certificate) sesh.getPeerCertificates()[0];
            //System.out.println(getCommonName(cert));
            
            //reader to read incoming file
            BufferedInputStream reader = new BufferedInputStream( socket.getInputStream());
            //writer to send request
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
            //out to write file to disk
            BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream(new File("_" + args[2])));
            
            //send Request with arg[2] (filename)
            writer.write(args[2] + "\n");
            writer.flush();

            //recieve bytes
            byte[] byteArray = new byte[1000];
            int count;
            while((count = reader.read(byteArray)) != -1){
                out.write(byteArray, 0, count);
            }
            out.close();
            reader.close();
            socket.close();
        }
        catch(Exception e){
            System.err.println("Error: " + e);
        }
    }
    static String getCommonName(X509Certificate cert)
    {   
        try{
            String name = cert.getSubjectX500Principal().getName();
            LdapName ln = new LdapName(name);
            String cn = null;
            for(Rdn rdn : ln.getRdns()){
                if("CN".equalsIgnoreCase(rdn.getType())){
                    cn = rdn.getValue().toString();
                }
            }
            return cn;
        }
        catch(Exception e){
            System.err.println("Error: " + e);
        }
        return null;
    }
}