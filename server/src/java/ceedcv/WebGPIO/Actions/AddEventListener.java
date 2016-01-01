/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO.Actions;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import ceedcv.WebGPIO.wgpioHandleGpioPinDigitalStateChangeEvent;
import ceedcv.WebGPIO.wgpioUtils;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinMode;
import javax.websocket.Session;

/**
 * Acción: ADD_EVENT_LISTENER<br/><br/>
 * 
 * Establece un pin para que dispare eventos cuando su estado cambie. Esto es útil sobretodo para pines de entrada.
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class AddEventListener extends wgpioAction
{
    public AddEventListener(){ super("ADD_EVENT_LISTENER"); }
    private static final wgpioHandleGpioPinDigitalStateChangeEvent _Handler = new wgpioHandleGpioPinDigitalStateChangeEvent();
    
    
    /**
     * Si el pin ya está monitoreando los cambios de estado, esta acción no tendrá ningún efecto.<br/>
     * Es obligatorio especificar el Pin al que afecta.
     * 
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     * @throws Exception En caso de que el tipo de pin no sea válido (Entrada de datos) se lanza una excepción
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, final Session Session) throws Exception
    {
        final GpioPin Pin = wgpioUtils.getProvisionedPinByAddress(Frame.getPin());
        if(!(Pin.getMode() == PinMode.ANALOG_INPUT || Pin.getMode() == PinMode.DIGITAL_INPUT))
            throw new Exception("Tipo de pin inválido");
        
        if(!((GpioPinDigitalInput)Pin).hasListener(AddEventListener._Handler))
            ((GpioPinDigitalInput)Pin).addListener(AddEventListener._Handler);
    }
}
