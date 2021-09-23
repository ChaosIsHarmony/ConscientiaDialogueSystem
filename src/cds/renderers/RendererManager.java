package cds.renderers;

public class RendererManager {

	public static IRenderer createRenderer() {
		// TODO: load from config
		return new ConsoleRenderer();
	}
}
