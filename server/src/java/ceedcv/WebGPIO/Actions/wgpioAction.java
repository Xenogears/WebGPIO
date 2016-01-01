package ceedcv.WebGPIO.Actions;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import java.util.HashMap;
import javax.websocket.Session;

public abstract class wgpioAction
{
    private String ActionName = null;
    public String getActionName(){ return this.ActionName; }
      
    public wgpioAction(String ActionName)
    {
        this.ActionName = ActionName;     
        wgpioAction._Actions.put(ActionName, this);       
    }
    
    public abstract void onAction(wgpioGenericFrame Frame, Session Session) throws Exception;

    private static final HashMap<String, wgpioAction> _Actions = new HashMap<>();
    public static wgpioAction getByActionName(String ActionName)
    {
        if(!wgpioAction._Actions.containsKey(ActionName))
            return null;
        
        return wgpioAction._Actions.get(ActionName);
    }
    
    public static boolean Setup()
    {
        new SetMode();
        new GetMode();
        new SetValue();
        new GetValue();
        new GetDistance();
        new AddEventListener();
        new RemoveAllEventListeners();
        
        new ShutdownPi();
        new RebootPi();
        new StopTomcat();
        new RestartTomcat();
        
        return true;
    }
}
