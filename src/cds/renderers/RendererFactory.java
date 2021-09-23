package cds.renderers;

public class RendererFactory {

	public static IRenderer createRenderer() {
		return new ConsoleRenderer();
	}
}