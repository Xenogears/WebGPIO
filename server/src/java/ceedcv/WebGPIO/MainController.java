/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO;

import com.pi4j.concurrent.ExecutorServiceFactory;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.RaspiGpioProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Este es el punto de entrada principal del WebSockets. Esta clase se encarga de gestionar y manejar las conexiones del WebSocket.
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
@ServerEndpoint("/WS")
public class MainController
{
    //WS
    public static final List<Session> WSSessions = new ArrayList<>();
    
    //PI4J
    public static RaspiGpioProvider Provider = null;
    public static GpioController GPIO = null;
    public static ExecutorServiceFactory Executor = null;
        
    
    @OnClose
    public void CloseConnection(Session SessionToClose, CloseReason Reason)
    {
        WSSessions.remove(SessionToClose);
    }
    
    @OnOpen
    public void CreateConnection(final Session NewSession)
    {        
        WSSessions.add(NewSession);
        NewSession.addMessageHandler(new MainControllerMessageHandler(NewSession));
    }
    
    @OnError
    public void onError(Throwable t)
    {
        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, t.toString());
    }    
}
