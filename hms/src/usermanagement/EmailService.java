package usermanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.Base64;

public class EmailService {

    public static void sendEmail(String recipient, String subject, String body) {
        String smtpServer = "smtp-mail.outlook.com";  // Correct SMTP server for Outlook
        int port = 587;  // Port 587 for secure connection (STARTTLS)
        String senderEmail = "hms-demo-2024@outlook.com";  // Your Outlook email address
        String senderPassword = "demo-hms-2024!";  // Your Outlook email password

        System.out.println("Current directory: " + System.getProperty("user.dir"));

        // Set system properties for keystore (optional)
        System.setProperty("javax.net.ssl.keyStore", "/hms/src/usermanagement/mykeystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "demo-hms-2024");
        System.setProperty("javax.net.ssl.trustStore", "/hms/src/usermanagement/mykeystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "demo-hms-2024");

        try {
            // Create custom SSL context to disable certificate verification
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // No-op
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // No-op (disables server certificate verification)
                        }
                    }
            }, null);

            // Set the SSL context to use our custom TrustManager
            SSLSocketFactory factory = sslContext.getSocketFactory();

            // Create a regular socket connection
            try (Socket socket = new Socket(smtpServer, port);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                // Read the server's response
                System.out.println("Server response: " + reader.readLine());

                // Send HELO command to the server
                writer.println("HELO localhost");
                System.out.println("Server response: " + reader.readLine());

                // Start TLS command (this is required for encryption)
                writer.println("STARTTLS");
                System.out.println("Server response: " + reader.readLine());

                // Upgrade the socket to SSL/TLS using the custom SSL context
                SSLSocket sslSocket = (SSLSocket) factory.createSocket(socket, smtpServer, port, true);
                sslSocket.startHandshake();

                // Re-initialize reader and writer with the SSL/TLS socket
                BufferedReader sslReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                PrintWriter sslWriter = new PrintWriter(sslSocket.getOutputStream(), true);

                // Continue communication with the encrypted connection
                sslWriter.println("EHLO localhost");
                System.out.println("Server response: " + sslReader.readLine());

                // Authenticate (some SMTP servers require authentication)
                sslWriter.println("AUTH LOGIN");
                System.out.println("Server response: " + sslReader.readLine());

                // Send the encoded username and password
                sslWriter.println(Base64.getEncoder().encodeToString(senderEmail.getBytes()));
                System.out.println("Server response: " + sslReader.readLine());

                sslWriter.println(Base64.getEncoder().encodeToString(senderPassword.getBytes()));
                System.out.println("Server response: " + sslReader.readLine());

                // Specify the sender's email
                sslWriter.println("MAIL FROM: <" + senderEmail + ">");
                System.out.println("Server response: " + sslReader.readLine());

                // Specify the recipient's email
                sslWriter.println("RCPT TO: <" + recipient + ">");
                System.out.println("Server response: " + sslReader.readLine());

                // Begin the data section
                sslWriter.println("DATA");
                System.out.println("Server response: " + sslReader.readLine());

                // Send the email content
                sslWriter.println("Subject: " + subject);
                sslWriter.println("From: " + senderEmail);
                sslWriter.println("To: " + recipient);
                sslWriter.println();
                sslWriter.println(body);
                sslWriter.println(".");
                System.out.println("Server response: " + sslReader.readLine());
                System.out.println("recipient: " + recipient);

                // Quit the session
                sslWriter.println("QUIT");
                System.out.println("Server response: " + sslReader.readLine());

            }

        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
