package cds.io;

public class FileIOFactory {

	public static IFileIO createFileIO() {
		// TODO load from settings
		return new DesktopFileIO();
	}
}