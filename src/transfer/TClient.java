package transfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import fileTransfer.data.FileMsg;

public class TClient {
	private Socket clientSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Logger logger = Logger.getLogger(TClient.class.getName());

	public void startConnection(String ip, int port) throws IOException {
		logger.log(Level.INFO, "connecting...");
		clientSocket = new Socket(ip, port);
		logger.log(Level.INFO, "connected!!");
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		in = new ObjectInputStream(clientSocket.getInputStream());
	}

	public File receiveFile() throws IOException, ClassNotFoundException {
		logger.log(Level.INFO, "receiving the file...");
		FileMsg msg = (FileMsg) in.readObject();
		File result = new File(msg.getName());
		FileMsg response = new FileMsg(null, "response", 0);
		logger.log(Level.INFO, "creating new file");
		if (!result.createNewFile()) {
			response.setResponse(false);
			out.writeObject(response);
			out.close();
			logger.log(Level.INFO, "this file exists already, move it before the file transfer");
			return null;
		}

		if (writeToFile(result, msg.getContent())) {
			response.setResponse(true);
			out.writeObject(response);
			out.close();
			logger.log(Level.INFO, "completed!! :D");
			return result;
		} else {
			response.setResponse(false);
			out.writeObject(response);
			out.close();
			logger.log(Level.SEVERE, "couldn't write into the file");
			return null;
		}

	}

	private boolean writeToFile(File file, byte[] content) {
		try (FileOutputStream writer = new FileOutputStream(file)) {
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		TClient client = new TClient();
		Scanner scan = new Scanner(System.in);
		System.out.println("Provide port (only number)");
		int port = Integer.parseInt(scan.nextLine());
		System.out.println("Provide IPV4");
		String ip = scan.nextLine();
		client.startConnection(ip, port);
		scan.close();
		long start = System.currentTimeMillis();
		client.receiveFile();
		long end = System.currentTimeMillis();
		float sec = (end - start) / 1000F;
		System.out.println("elapsed time: " + sec);
		client.stopConnection();
	}

}
