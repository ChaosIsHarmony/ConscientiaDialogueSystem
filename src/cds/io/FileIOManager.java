package cds.io;

public class FileIOManager {

	public static IFileIO createFileIO() {
		return new DesktopFileIO();
	}
}
