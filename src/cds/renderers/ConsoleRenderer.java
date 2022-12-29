package cds.renderers;

public class ConsoleRenderer implements IRenderer {

	public ConsoleRenderer() {}

	public void show(String text) {
		System.out.println("\n" + text + "\n");
	}
}
