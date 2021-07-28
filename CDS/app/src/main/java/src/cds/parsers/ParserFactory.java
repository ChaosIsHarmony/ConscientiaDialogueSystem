package cds.parsers;

public class ParserFactory {
	
	public static IParser createParser() {
		// TODO load from settings file
		return new OriginalFormatParser();
	}

}