/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import com.google.gson.Gson;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import javax.websocket.Session;

/**
 * Se encarga de manejar todos los eventos de cambio de estado en los pines de entrada.<br/>
 * Para que un pin esté a la escucha de dichos eventos, es necesario establecerlo mediante la acción ADD_EVENT_LISTENER.<br/><br/>
 * 
 * Una vez añadida la escucha de eventos sobre un Pin, este notificará sus cambios a todas las sesiones abiertas (hayan solicitado la escucha de eventos o no).
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class wgpioHandleGpioPinDigitalStateChangeEvent implements GpioPinListenerDigital
{
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
    {        
        wgpioGenericFrame newFrame = new wgpioErrorFrame();
        newFrame.setAction("EVENT_DISPATCH");
        newFrame.setValue(event.getState() == PinState.HIGH ? 1 : 0);
        newFrame.setPin(event.getPin().getPin().getAddress());

        Gson g = new Gson();
        String serializedFrame = g.toJson(newFrame);
                    
        for(Session S : MainController.WSSessions)
            if(S.isOpen())
                wgpioUtils.SendAsync(S, serializedFrame);
    }
}
