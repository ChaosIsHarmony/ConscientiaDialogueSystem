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
}
