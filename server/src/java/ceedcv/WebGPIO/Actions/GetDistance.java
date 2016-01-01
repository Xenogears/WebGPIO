/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO.Actions;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import ceedcv.WebGPIO.wgpioUtils;
import com.pi4j.io.gpio.Pin;
import javax.websocket.Session;

/**
 * Acción: GET_DISTANCE<br/><br/>
 * 
 * Acción para medir la distancia de un sensor ultrasónico HC-SR04 o similares.<br/>
 * Es necesario especificar el pin conectado al Echo y el pin conectado al Trigger del dispositivo.
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class GetDistance extends wgpioAction
{
    public GetDistance(){ super("GET_DISTANCE"); }

    /**
     * Para más información, revisar el método wgpioUtils.GetDistance.
     * 
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, Session Session)
    {
        Pin EchoPin = wgpioUtils.getPinByAddress(Frame.getEchoPin());
        Pin TrgPin = wgpioUtils.getPinByAddress(Frame.getTrgPin());

        double distance = wgpioUtils.getDistance(EchoPin, TrgPin);
        Frame.setDistance(distance);
        Frame.setMessage("OK");
        
        wgpioUtils.SendAsync(Session, Frame);
    }
}
