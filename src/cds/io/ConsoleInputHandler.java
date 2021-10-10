package cds.io;

import java.util.Scanner;

public class ConsoleInputHandler implements IInputHandler {

	Scanner scanner;

	public ConsoleInputHandler() {
		scanner = new Scanner(System.in);
	}

	public int selectResponse() {
		return scanner.nextInt();
	}

	public String[] selectSaveFiles() {
		System.out.println("Select save file [write file number; -1 if new game]");
		int fileNum = scanner.nextInt();
		String[] filenames = null;
		if (fileNum >= 0) {
			filenames = new String[2];
			filenames[0] = "playerSave"+fileNum+".json";
			filenames[1] = "npcsSave"+fileNum+".json";
		}

		scanner.nextLine(); // to move scanner to the next line, or else will skip next input
		return filenames;
	}

	public String selectStartingBook() {
		System.out.println("Select startingBook [lowercase]");
		return scanner.nextLine();
	}
}
