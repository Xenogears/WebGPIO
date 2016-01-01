/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO.Actions;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import ceedcv.WebGPIO.MainController;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinInput;
import com.pi4j.io.gpio.PinMode;
import javax.websocket.Session;

/**
 * Acción: REMOVE_ALL_EVENT_LISTENERS<br/><br/>
 * 
 * Acción para eliminar todos los eventos a la escucha sobre los pines.<br/>
 * Por problemas relacionados a la librería PI4J, en caso de que se vuelva a desplegar la librería sin reiniciar el servicio Apache Tomcat, dichos eventos no funcionaran correctamente.
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class RemoveAllEventListeners extends wgpioAction
{
    public RemoveAllEventListeners(){ super("REMOVE_ALL_EVENT_LISTENERS"); }

    /**
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, final Session Session)
    {        
        for(GpioPin p : MainController.GPIO.getProvisionedPins())
        {
            if(p.getMode() == PinMode.ANALOG_INPUT || p.getMode() == PinMode.DIGITAL_INPUT)
            {
                GpioPinInput input = ((GpioPinInput)p);
                input.removeAllListeners();
            }
        }
    }
}
