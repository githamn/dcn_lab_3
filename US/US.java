package US;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class US {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/fibonacci", new FibonacciHandler());
        server.start();
    }

    static class FibonacciHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParams = query.split("&");
            String hostname = null, fs_port = null, number = null;
            for (String param : queryParams) {
                if (param.startsWith("hostname=")) {
                    hostname = param.substring("hostname=".length());
                } else if (param.startsWith("fs_port=")) {
                    fs_port = param.substring("fs_port=".length());
                } else if (param.startsWith("number=")) {
                    number = param.substring("number=".length());
                }
            }

            if (hostname == null || fs_port == null || number == null) {
                String response = "Missing parameters";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String ip = queryAsForIp(hostname);
            String fsResponse = getFsResponse(ip, fs_port, number);

            exchange.sendResponseHeaders(200, fsResponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(fsResponse.getBytes());
            os.close();
        }

        private String queryAsForIp(String hostname) {
            try (DatagramSocket socket = new DatagramSocket()) {
                String msg = "TYPE=A\nNAME=" + hostname + "\n";
                byte[] buffer = msg.getBytes(StandardCharsets.UTF_8);
                InetAddress address = InetAddress.getByName("127.0.0.1");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 53533);
                socket.send(packet);

                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                return receivedData.split("VALUE=")[1].split(" ")[0];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private String getFsResponse(String ip, String port, String number) {
            try {
                URL url = new URL("http://" + ip + ":" + port + "/fibonacci?number=" + number);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = conn.getInputStream()) {
                        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    }
                } else {
                    return "{\"error\": \"FS server error\"}";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "{\"error\": \"Error connecting to FS server\"}";
            }
        }
    }
}
