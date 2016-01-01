package ceedcv.WebGPIO;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import ceedcv.WebGPIO.Actions.wgpioAction;
import com.google.gson.Gson;
import java.util.logging.Logger;
import javax.websocket.MessageHandler;
import javax.websocket.Session;


/**
 * Es el punto de entrada de todos los mensajes que llegan al servidor mediante WebSockets.<br/>
 * 1º : Deserializa el mensaje y crea un objeto de tipo wgpioGenericFrame con la solicitud.<br/>
 * 2º : Busca la acción asociada a la solicitud (SET_MODE, SET_VALUE, etc...)<br/>
 * 3º : Le pasa la solicitud a la acción y le delega el control.<br/><br/>
 * 
 * En caso de que la acción no exista, lanzará una excepción y se notificará al cliente que realizó la solicitud.
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class MainControllerMessageHandler implements MessageHandler.Whole<String>
{
    private Session _CurrentSession = null;
    
    /**
     * @param s Sesión asociada al mensaje (quien realiza la solicitud)
     */
    public MainControllerMessageHandler(Session s)
    {
        this._CurrentSession = s;
    }
    
    @Override
    public void onMessage(String msg)
    {
        final Logger logger = Logger.getLogger("ceedcv.WebGPIO.MainControllerMessageHandler");        
                    
        try 
        {        
            final Gson g = new Gson();
            wgpioGenericFrame fr = g.fromJson(msg, wgpioGenericFrame.class);
            
            wgpioAction ac = wgpioAction.getByActionName(fr.getAction());  
            if(ac == null)
                throw new Exception("Acción desconocida : " + fr.getAction());
            
            ac.onAction(fr, _CurrentSession);
        } 
        catch (Exception ex)
        {
            logger.severe(ex.toString());
            wgpioUtils.SendAsync(_CurrentSession, new wgpioErrorFrame(ex.getLocalizedMessage()));
        }
    }    
}
