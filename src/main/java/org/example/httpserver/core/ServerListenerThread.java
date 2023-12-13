package org.example.httpserver.core;

import org.example.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This Class is used to accept connections
 */
public class ServerListenerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);
    private int port;
    private String webroot;
    ServerSocket serverSocket;

    public ServerListenerThread(int port, String webroot) throws IOException {
        this.port = port;
        this.webroot = webroot;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run() {
        try {
            /**
             * While server socket is good condition(successfully bound to an address)
             * and it is not closed we can keep accepting
             * connections from the web, without the while loop we can accept only one request
             * and the program ends
             */
            while ( serverSocket.isBound() && !serverSocket.isClosed()) {
                /**
                 * We are getting particular socket and we are giving to the HttpConnectionWorkerThread
                 * so this class will take care of the actions in different thread
                 */
                Socket socket = serverSocket.accept();
                LOGGER.info(" * Connection accepted: " + socket.getInetAddress());

                /**
                 * With this class - HttpConnectionWorkerThread we can accept more than one
                 * http request and to process them in the same time, without that if we have
                 * more than one http request we have to wait the first request to get executed
                 * then the next http request will be taken into actions
                 */
                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket);
                workerThread.start();
            }

        } catch (IOException e) {
            LOGGER.error("Problem with setting socket", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }
    }
}
