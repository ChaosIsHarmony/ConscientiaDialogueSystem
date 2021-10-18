package cds.io;

public interface IInputHandler {

	int selectResponse();

	boolean finishCombat();

	String[] selectSaveFiles();

	String selectStartingBook();

}
