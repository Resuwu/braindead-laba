package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConnectionHandler {
    private static final String JSON_FILE_NAME = "payload.json";
    private static final String TXT_FILE_NAME = "file.txt";
    private static final Charset CHARSET = StandardCharsets.US_ASCII;

    private static final String HTTP_HEADERS_JSON = """
            HTTP/1.1 200 OK
            Date: Mon, 18 Sep 2023 14:08:55 +0200
            HttpServer: Simple Webserver
            Content-Type: application/json
            """;

    private static final String HTTP_HEADERS_TXT = """
            HTTP/1.1 200 OK
            Date: Mon, 18 Sep 2023 14:08:55 +0200
            HttpServer: Simple Webserver
            Content-Type: text/plain
            Content-Disposition: attachment; filename="Ya-upal.txt"
            """;

    private final HttpServer httpServer;

    public ConnectionHandler(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public void handleJson() {
        try (var serverSocket = new ServerSocket(httpServer.getTcpPort())) {
            var socket = serverSocket.accept();
            var inputStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
            var outputStreamWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET));

            Path pathJson = Path.of(ClassLoader.getSystemResource(JSON_FILE_NAME).toURI());

            parseRequest(inputStreamReader);
            writeResponse(outputStreamWriter, pathJson);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleTxt() {
        try (var serverSocket = new ServerSocket(httpServer.getTcpPort())) {
            var socket = serverSocket.accept();
            var inputStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
            var outputStreamWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET));

            Path pathTxt = Path.of(ClassLoader.getSystemResource(TXT_FILE_NAME).toURI());

            parseRequest(inputStreamReader);
            sendFile(outputStreamWriter, pathTxt);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseRequest(BufferedReader inputStreamReader) throws IOException {
        var request = inputStreamReader.readLine();

        while (request != null && !request.isEmpty()) {
            System.out.println(request);
            request = inputStreamReader.readLine();
        }
    }

    private void writeResponse(BufferedWriter outputStreamWriter, Path path) {
        try {
            outputStreamWriter.write(HTTP_HEADERS_JSON);
            outputStreamWriter.newLine();
            outputStreamWriter.write(Files.readString(path, CHARSET));
            outputStreamWriter.newLine();
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFile(BufferedWriter outputStreamWriter, Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            outputStreamWriter.write(HTTP_HEADERS_TXT);
            outputStreamWriter.newLine();

            int temp = is.read();
            while (temp != -1) {
                outputStreamWriter.write(temp);
                temp = is.read();
            }
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
