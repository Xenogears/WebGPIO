/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO.Actions;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import ceedcv.WebGPIO.MainController;
import ceedcv.WebGPIO.wgpioSoftPwmPin;
import ceedcv.WebGPIO.wgpioUtils;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import javax.websocket.Session;


/**
 * Acción: SET_MODE<br/><br/>
 * 
 * Acción para establecer el tipo del pin especificado. Los valores permitidos son:<br/>
 * Entrada : "in"<br/>
 * Salida : "out"<br/>
 * PWM : "pwm".<br/><br/>
 * 
 * En el caso de que el modo sea PWM y se utilice un pin distinto del nº 1 se empleará el sistema SoftPwm. Más información en la clase wgpioSoftPwmPin.
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class SetMode extends wgpioAction
{
    public SetMode(){ super("SET_MODE"); }

    /**
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     * @throws Exception En caso de que el tipo del pin sea desconocido
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, final Session Session) throws Exception
    {
        boolean AlreadyDefined = false;
        GpioPin newPin;
        Pin p = wgpioUtils.getPinByAddress(Frame.getPin());          
        
        if(wgpioUtils.provisionedPinsContainsPin(p))
        {   
            newPin = wgpioUtils.getProvisionedPinByAddress(Frame.getPin());
            if((newPin.getMode() == PinMode.ANALOG_INPUT || newPin.getMode() == PinMode.DIGITAL_INPUT) && Frame.getMode().equals("in") 
                    ||
                (newPin.getMode() == PinMode.ANALOG_OUTPUT || newPin.getMode() == PinMode.DIGITAL_OUTPUT) && Frame.getMode().equals("out")
                    ||
                (newPin.getMode() == PinMode.PWM_OUTPUT && Frame.getMode().equals("pwm")))
            {
                AlreadyDefined = true;
            }
            else    
            {
                newPin.unexport();

                if(newPin.getMode() == PinMode.ANALOG_INPUT || newPin.getMode() == PinMode.DIGITAL_INPUT)
                    ((GpioPinInput)newPin).removeAllListeners();

                MainController.GPIO.unprovisionPin(newPin);       
            }
        }                            

        if(!AlreadyDefined)
        {
            switch (Frame.getMode())
            {
                case "out":
                    newPin = MainController.GPIO.provisionDigitalOutputPin(p, PinState.LOW);
                    newPin.setShutdownOptions(true, PinState.LOW);
                    break;

                case "pwm":
                    if(p.getAddress() == 1) //NATIVE PWM
                    {
                        newPin = MainController.GPIO.provisionPwmOutputPin(p);
                        newPin.setShutdownOptions(true);
                    }
                    else if(!wgpioSoftPwmPin.isPinProvisioned(p.getAddress()))
                        new wgpioSoftPwmPin(p.getAddress());                    

                    break;

                case "in": case "digital-in":
                    newPin = MainController.GPIO.provisionDigitalInputPin(p, PinPullResistance.PULL_DOWN);
                    newPin.setShutdownOptions(true);
                    break;

                default: 
                    throw new Exception("Tipo de pin desconocido");
            }
        }
        
        Frame.setMessage("OK");
        
        wgpioUtils.SendAsync(Session, Frame);
    }
}
