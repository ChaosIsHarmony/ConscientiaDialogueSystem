package cds.io;

public class FileIOFactory {

	public static FileIO createFileIO() {
		// TODO load from settings
		return new DesktopFileIO();
	}
}