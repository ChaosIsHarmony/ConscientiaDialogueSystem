package cds.parsers;

import cds.io.FileIO;

public class ParserFactory {
	
	public static Parser createParser() {
		// TODO load from settings file
		return new OriginalFormatParser();
	}

}