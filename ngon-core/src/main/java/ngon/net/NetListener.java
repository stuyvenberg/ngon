package ngon.net;

import ngon.game.action.Action;

public interface NetListener
{
	public void connected(ActionTransceiver tr);
	public void actionReceived(ActionTransceiver tr, Action act);
	public void disconnected(ActionTransceiver tr);
}
