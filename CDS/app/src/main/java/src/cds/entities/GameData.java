package cds.entities;

import cds.parsers.IParser;
import cds.parsers.ParserFactory;

public class GameData {

	private Area currentArea;
	
	public void setCurrentArea(Area newArea) { currentArea = newArea; }
	public Area getCurrentArea() { return currentArea; }
}