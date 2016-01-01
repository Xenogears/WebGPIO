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
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.PinMode;
import javax.websocket.Session;


/**
 * Acción: GET_VALUE<br/><br/>
 * 
 * Acción para obtener el valor del pin especificado. Los valores permitidos son:<br/>
 * Salida convencional: 0 (0v) y 1 (3.3v)<br/>
 * Salida PWM : 0 (0v) y 100 (3.3v). Todos los valores intermedios son proporcionales.<br/>
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class GetValue extends wgpioAction
{
    public GetValue(){ super("GET_VALUE"); }

    /**
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     * @throws Exception En caso de que el tipo del pin sea desconocido
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, Session Session) throws Exception
    {
        GpioPin p = wgpioUtils.getProvisionedPinByAddress(Frame.getPin());
        
        if(p != null)
        {
            if(p.getMode() == PinMode.DIGITAL_INPUT || p.getMode() == PinMode.ANALOG_INPUT)
                Frame.setValue(((GpioPinDigitalInput)p).getState() == PinState.HIGH ? 1 : 0);
            else if(p.getMode() == PinMode.DIGITAL_OUTPUT || p.getMode() == PinMode.ANALOG_OUTPUT)
                Frame.setValue(((GpioPinDigitalOutput)p).getState() == PinState.HIGH ? 1 : 0);
            else if(p.getMode() == PinMode.PWM_OUTPUT)
                Frame.setValue((((GpioPinPwmOutput)p).getPwm() * 100) / 1024);
        }
        else if(wgpioSoftPwmPin.isPinProvisioned(Frame.getPin()))
        {
            wgpioSoftPwmPin pwm = wgpioSoftPwmPin.getPinByAddress(Frame.getPin());
            Frame.setValue(pwm.getValue());
        }     
        else throw new Exception("Tipo de pin desconocido");       
            
        Frame.setMessage("OK");
        
        wgpioUtils.SendAsync(Session, Frame);   
    }
}
