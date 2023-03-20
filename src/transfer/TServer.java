package transfer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import fileTransfer.data.FileMsg;

public class TServer {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Logger logger = Logger.getLogger(TServer.class.getName());
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public void start(int port, String ip, String pathFile, int mode) {
		try {
			logger.log(Level.INFO, "waiting for connection...");
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName(ip));
			clientSocket = serverSocket.accept();
			logger.log(Level.INFO, "connected!");
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			logger.log(Level.INFO, "fetching the file");
			long start = System.currentTimeMillis();
			sendFile(pathFile, mode);
			long end = System.currentTimeMillis();
			float sec = (end - start) / 1000F;
			System.out.println("elapsed time: " + sec);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Something goes wrong during the initialization of the server");
			e.printStackTrace();
			stop();
		}
	}

	private void sendFile(String pathFile, int mode) {
		try {
			logger.log(Level.INFO, "transfering the file");
			File file = new File(pathFile);
			FileMsg msg = new FileMsg(file, file.getName(), mode);
			out.writeObject(msg);
			FileMsg response = (FileMsg) in.readObject();
			if (!response.success()) {
				logger.log(Level.SEVERE, "failed in transfering the file, there was an error in client side");
			} else {
				logger.log(Level.INFO, "completed!! :D");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "file error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Please don't modify the class FileMsg");
			e.printStackTrace();
		}
	}

	private void stop() {
		try {
			serverSocket.close();
			clientSocket.close();
			in.close();
			out.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error occurred during server shutdown");
		}
	}

	public static void main(String[] args) {
		TServer server = new TServer();
		Scanner scan = new Scanner(System.in);
		String port;
		String compress;
		int mode;
		System.out.println("Provide a port number");
		port = scan.nextLine();
		System.out.println("Do you want to compress the file: it will take less time for sending the file");
		System.out.println("Y/n");
		compress = scan.nextLine();
		mode = (compress.equals("Y") || compress.equals("")) ? 2 : 1;
		scan.close();
		server.start(Integer.parseInt(port), "0.0.0.0", args[0], mode);
		server.stop();
	}
}
