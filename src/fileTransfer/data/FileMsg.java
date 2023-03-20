package fileTransfer.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileMsg implements Serializable {
	private static final long serialVersionUID = 2646643676731771670L;
	private byte[] content;

	private String name;
	private boolean response;

	/*
	 * mode 0 -> response, mode 1 -> raw file, mode 2 -> compressed file
	 */
	public FileMsg(File file, String name, int mode) throws IOException {
		if (file != null) {
			if (mode == 2) {
				this.setContent(zip(file));
			} else if (mode == 1) {
				this.setContent(raw(file));
			}
		}
		this.name = "zipFile.zip";
	}

	private byte[] zip(File fileToZip) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zipOut = new ZipOutputStream(baos);
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		zipOut.closeEntry();
		zipOut.close();
		byte[] result = baos.toByteArray();
		fis.close();
		zipOut.close();
		baos.close();
		return result;

	}

	private byte[] raw(File fileToZip) throws IOException {
		try (FileInputStream fis = new FileInputStream(fileToZip)) {
			return fis.readAllBytes();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean success() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
