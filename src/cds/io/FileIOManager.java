package cds.io;

public class FileIOManager {

	public static IFileIO createFileIO(String fileioType) {
		if (fileioType.equals("desktop"))
			return new DesktopFileIO();
		else
			return null;
	}
}
