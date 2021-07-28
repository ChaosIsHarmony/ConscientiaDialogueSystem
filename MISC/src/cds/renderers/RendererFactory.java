package cds.renderers;

public class RendererFactory {

	public static Renderer createRenderer() {
		return new ConsoleRenderer();
	}
}