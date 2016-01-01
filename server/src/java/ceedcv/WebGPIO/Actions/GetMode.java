/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO.Actions;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import ceedcv.WebGPIO.wgpioSoftPwmPin;
import ceedcv.WebGPIO.wgpioUtils;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinMode;
import javax.websocket.Session;

/**
 * Acción: GET_MODE<br/><br/>
 * 
 * Acción para obtener el tipo del pin especificado. Los valores permitidos son:<br/>
 * Entrada : "in"<br/>
 * Salida : "out"<br/>
 * PWM : "pwm"
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class GetMode extends wgpioAction
{
    public GetMode(){ super("GET_MODE"); }

    /**
     * Para más información, revisar el método wgpioUtils.GetDistance.
     * 
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     * @throws Exception En caso de que el pin no haya sido establecido todavía o el tipo sea desconocido
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, Session Session) throws Exception
    {
        GpioPin p = wgpioUtils.getProvisionedPinByAddress(Frame.getPin());

        if(p != null)
        {
            if(p.getMode() == PinMode.DIGITAL_INPUT || p.getMode() == PinMode.ANALOG_INPUT)
                Frame.setMode("in");
            else if(p.getMode() == PinMode.DIGITAL_OUTPUT || p.getMode() == PinMode.ANALOG_OUTPUT)
                Frame.setMode("out");
            else if(p.getMode() == PinMode.PWM_OUTPUT || wgpioSoftPwmPin.isPinProvisioned(Frame.getPin()))
                Frame.setMode("pwm");
            else
                throw new Exception("Tipo de pin desconocido");
        }  
        else throw new Exception("Tipo de pin todavía no establecido");       
        
        Frame.setMessage("OK");
        
        wgpioUtils.SendAsync(Session, Frame); 
    }
}
