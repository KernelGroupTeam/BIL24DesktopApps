package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.12.15
 */
interface NetPoolStateListenerOver extends NetPoolStateListener {
  NetPoolStateListener getOverridden();
}
