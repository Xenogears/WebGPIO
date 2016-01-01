/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO.Actions;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import ceedcv.WebGPIO.wgpioSoftPwmPin;
import ceedcv.WebGPIO.wgpioUtils;
import static ceedcv.WebGPIO.wgpioUtils.getProvisionedPinByAddress;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import javax.websocket.Session;

/**
 * Acción: SET_VALUE<br/><br/>
 * 
 * Acción para establecer el valor del pin especificado. Los valores permitidos dependen del tipo que tenga definido:<br/>
 * Salida: Valor 0, false. En caso contrario, true.<br/>
 * PWM: Valor 0 equivale a 0v. Valor 100 equivale a 3.3v. Son válidos y proporcionales todos los valores intermedios.<br/><br/>
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class SetValue extends wgpioAction
{
    public SetValue(){ super("SET_VALUE"); }

     /**
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     * @throws Exception En caso de que el tipo del pin sea desconocido o no se pueda establecer el valor para dicho pin
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, Session Session) throws Exception
    {
        GpioPin p = getProvisionedPinByAddress(Frame.getPin());
        if(p != null)
        {
            if(p.getMode() == PinMode.ANALOG_OUTPUT || p.getMode() == PinMode.DIGITAL_OUTPUT)
                ((GpioPinDigitalOutput)p).setState((Frame.getValue() == 0 ? PinState.LOW : PinState.HIGH));
            else if(p.getMode() == PinMode.PWM_OUTPUT)
                ((GpioPinPwmOutput)p).setPwm((Frame.getValue() * 1024) / 100);
        }
        else if(wgpioSoftPwmPin.isPinProvisioned(Frame.getPin()))
        {
            wgpioSoftPwmPin pwm = wgpioSoftPwmPin.getPinByAddress(Frame.getPin());
            pwm.setValue(Frame.getValue());
        }
        else throw new Exception("No se pudo establecer el valor para este pin");        
         
        Frame.setMessage("OK");
        
        wgpioUtils.SendAsync(Session, Frame);
    }
}
