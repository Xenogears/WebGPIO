package ceedcv.WebGPIO;

import com.pi4j.io.gpio.Pin;
import java.util.HashMap;

/**
 * PWM (Pulse Width Modulation) es una tecnología que mediante pulsos eléctricos a unos intervalos de tiempo permite controlar la cantidad de energía
 * que circula por los pines GPIO. La Raspberry Pi solamente tiene soporte nativo para dicho control en el pin nº 1.<br/><br/>
 * Para conseguir usar esta característica en el resto de pines existe la posibilidad de crear esos intervalos de pulsos mediante software.<br/>
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 * @see https://projects.drogon.net/raspberry-pi/wiringpi/pins/
 */
public class wgpioSoftPwmPin
{
    private static final HashMap<Integer, wgpioSoftPwmPin> _InitializedPins = new HashMap<>();
    
    /**
     * Devuelve el Pin PWM por software en caso de que ya haya sido creado
     * @param addr Nº de pin
     * @return El pin PWM por software
     */
    public static wgpioSoftPwmPin getPinByAddress(int addr)
    { 
        if(wgpioSoftPwmPin._InitializedPins.containsKey(addr))
            return wgpioSoftPwmPin._InitializedPins.get(addr);
        else
            return null;
    }
    
    /**
     * @param addr Nº de pin
     * @return true si el pin ya ha sido creado y false en caso contrario
     */
    public static boolean isPinProvisioned(int addr)
    {
        return (wgpioSoftPwmPin._InitializedPins.containsKey(addr));
    }
    
    private int _Address;
    private int _CurrentValue = 0;
    
    /**
     * @param pin Pin deseado para establecer PWM por software
     */
    public wgpioSoftPwmPin(Pin pin)
    {
        if(!_InitializedPins.containsKey(pin.getAddress()))
        {
            com.pi4j.wiringpi.SoftPwm.softPwmCreate(pin.getAddress(), 0, 100);
            _InitializedPins.put(pin.getAddress(), this);
        }
        
        this._Address = pin.getAddress();
    }
    
    /**
     * 
     * @param pinAddr Nº de pin deseado para establecer PWM por software
     */
    public wgpioSoftPwmPin(int pinAddr)
    {
        if(!_InitializedPins.containsKey(pinAddr))
        {
            com.pi4j.wiringpi.SoftPwm.softPwmCreate(pinAddr, 0, 100);
            _InitializedPins.put(pinAddr, this);
        }
        
        this._Address = pinAddr;
    }
    
    public int getAddress(){ return this._Address; }
    public int getValue(){ return this._CurrentValue; }
    
    /**
     * @param value Valor deseado para establecer al pin. El rango va desde 0 (0v) a 100 (3.3v) 
     */
    public void setValue(int value)
    {
        this._CurrentValue = value; 
         com.pi4j.wiringpi.SoftPwm.softPwmWrite(this._Address, this._CurrentValue);
    }
    
}
