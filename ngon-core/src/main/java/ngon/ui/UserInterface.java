package ngon.ui;

import ngon.game.action.ActionListener;

public interface UserInterface extends ActionListener
{
	/**
	 * Show the user a message and require their acknowledgment. This should function like Windows' MessageBox() or
	 * JavaScript's alert(). This will be used for things the player absolutely must know. (No spam; we promise.)  
	 * 
	 * @param text The message to display to the user until they say OK.
	 */
	public void showMessageBox(String text);
	
	/**
	 * Add a line (or several) to some persistent display. Usually this can be safely intermixed with i.e. an
	 * inter-player chat box. This will be used to log any events that don't crop up as actions (otherwise transparent
	 * host transfers (if they ever exist) are an example).
	 * 
	 * @param text The message to keep displaying to the user.
	 */
	public void logText(String text);
	
	/**
	 * Show the user a temporary message that won't necessarily need their acknowledgment. On Android, this might be
	 * a toast; elsewhere, well, you'll have to get creative. The user should be able to read these, but never be
	 * <i>required</i> to.
	 * 
	 * @param text The message to hope the user will read.
	 */
	public void displayMessage(String text);
}
