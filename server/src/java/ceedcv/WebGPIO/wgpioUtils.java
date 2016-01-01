package ceedcv.WebGPIO;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.lang.reflect.Field;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import javax.websocket.Session;

/**
 * Aquí se definen métodos utiles variados para facilitar el uso de la librería
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/   
 */
public class wgpioUtils
{
    private static final Semaphore _WSSema = new Semaphore(1);
    
    /**
    * Envía de forma asíncrona texto a una sesión. Para evitar posibles errores con sesiones concerruentes, el propio método gestiona las distintas llamadas
    * para que nunca se envíen varias simultáneas.
    * @param S Sesión donde enviar el mensaje
    * @param T Texto a enviar
    * @return 
    * 
    * @author AlvaroGracia
    * @version 1.00
    * @see http://www.ceedcv.es/ 
    */
    public static boolean SendAsync(final Session S, String T)
    {
        try
        {
            _WSSema.acquire();            
            S.getAsyncRemote().sendText(T);
            
            return true;
        }
        catch(InterruptedException ex){ return false; }
        finally { _WSSema.release(); }
    }
    
    /**
    * Alias para enviar una trama de tipo wgpioGenericFrame. El método se encarga de la serialización automáticamente.
    * @param S Sesión a donde enviar el mensaje
    * @param F La trama que se desea enviar
    * @return true en caso de que todo haya funcionado correctamente. false en caso contrario.
    * 
    * @author AlvaroGracia
    * @version 1.00
    * @see http://www.ceedcv.es/ 
    */
    public static boolean SendAsync(final Session S, wgpioGenericFrame F)
    {
        return wgpioUtils.SendAsync(S, F.Serialize());
    }
    
    /**
    * Permite obtener la instancia de un Pin que previamente ya ha sido establecido y utilizado.
    * @param address Nº de Pin deseado
    * @return Una instancia del tipo GpioPin del pin solicitado. NULL en caso de que no exista.
    * 
    * @author AlvaroGracia
    * @version 1.00
    * @see http://www.ceedcv.es/ 
    */
    public static GpioPin getProvisionedPinByAddress(int address)
    {
        if(MainController.GPIO == null)
            return null;            
        
        for(GpioPin p : MainController.GPIO.getProvisionedPins())
        {
            if(p.getPin().getAddress() == address)
                return p;
        }
        
        return null;
    }
    
    /**
    * Permite saber si un pin ya ha sido previamente ha sido establecido y utilizado.
    * @param pin Pin deseado
    * @return true en caso de que el pin ya haya sido establecido. false en caso contrario
    * 
    * @author AlvaroGracia
    * @version 1.00
    * @see http://www.ceedcv.es/ 
    */
    public static boolean provisionedPinsContainsPin(Pin pin)
    {
        for(GpioPin p : MainController.GPIO.getProvisionedPins())
        {
            if(p.getPin().equals(pin))
                return true;
        }
        
        return false;
    }
    
    /**
    * Permite saber si un pin ya ha sido previamente ha sido establecido y utilizado.
    * @param address Nº de pin deseado
    * @return true en caso de que el pin ya haya sido establecido. false en caso contrario
    * 
    * 
    * @author AlvaroGracia
    * @version 1.00
    * @see http://www.ceedcv.es/ 
    */
    public static boolean provisionedPinsContainsPin(int address)
    {
        for(GpioPin p : MainController.GPIO.getProvisionedPins())
        {
            if(p.getPin().getAddress() == address)
                return true;
        }
        
        return false;
    }
    
    /**
    * Permite obtener una instancia de la clase Pin mediante su nº de pin
    * @param address Nº de pin deseado
    * 
    * @author AlvaroGracia
    * @version 1.00
    * @see http://www.ceedcv.es/ 
    * 
    * @return El pin deseado en caso de que exista. NULL en caso contrario.
    */
    public static Pin getPinByAddress(int address)
    {
        if(address < 0 || address > 20)
            return null;
           
        try
        {
            Field pinField = RaspiPin.class.getField(String.format("GPIO_%02d", address));
            return (Pin)pinField.get(null);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex)
        {
            Logger logger = Logger.getLogger("ceedcv.WebGPIO.wgpioUtils");        
            logger.severe(ex.toString());
        
            return null;
        }
    } 
    
    private static final Semaphore SensorSema = new Semaphore(1);
    
    /**
    * Mide la distancia utilizando un sensor de proximidad HC-SR04 o equivalente.
    * @param EchoPin Nº de pin conectado al Echo
    * @param TrgPin Nº de pin conectado al Trigger
    * 
    * @author AlvaroGracia
    * @version 1.00
    * @see http://www.ceedcv.es/ 
    * @see http://www.micropik.com/PDF/HCSR04.pdf
    * 
    * @return La distancia medida en cm o un número negativo en caso de error.
    */
    public static double getDistance(final Pin EchoPin, final Pin TrgPin)
    {
        GpioPinDigitalInput Echo;
        GpioPinDigitalOutput Trg;
        
        try
        {
            if(provisionedPinsContainsPin(EchoPin))
            {
                Echo = (GpioPinDigitalInput)getProvisionedPinByAddress(EchoPin.getAddress());
                if(Echo.getMode() != PinMode.DIGITAL_INPUT)
                    Echo.setMode(PinMode.DIGITAL_INPUT);
            }
            else
            {
                Echo = MainController.GPIO.provisionDigitalInputPin(EchoPin);
                Echo.setShutdownOptions(true);
                
                Thread.sleep(500);
            }

            if(provisionedPinsContainsPin(TrgPin))
            {
                Trg = (GpioPinDigitalOutput)getProvisionedPinByAddress(TrgPin.getAddress());
                if(Trg.getMode() != PinMode.DIGITAL_OUTPUT)
                    Trg.setMode(PinMode.DIGITAL_OUTPUT);
            }
            else
            {
                Trg = MainController.GPIO.provisionDigitalOutputPin(TrgPin, PinState.LOW);
                Trg.setShutdownOptions(true, PinState.LOW);
                
                Thread.sleep(500);
            }
             
            wgpioUtils.SensorSema.acquire();
            
            Trg.setState(PinState.HIGH);
            Thread.sleep(0, 1);
            Trg.setState(PinState.LOW);
            
            long start = System.nanoTime();
            long stop;
            double distance = -1;
                        
            while(Echo.getState() == PinState.LOW)
                if((System.nanoTime() - start) > 1000 * 1000) //MAX 1000MS
                    return -1;
            
            start = System.nanoTime();
            
            while(Echo.getState() == PinState.HIGH)
            {
                stop = System.nanoTime();
                distance = (((stop-start) * 0.0343) / 2);
                
                if(distance > 400 * 1000)
                    return -1;
            }
            
            return distance;
        }
        catch(InterruptedException ex)
        {
            final Logger logger = Logger.getLogger("ceedcv.WebGPIO.wgpioUtils");       
            logger.severe(ex.toString());
            
            return -1;
        }
        finally
        {
            wgpioUtils.SensorSema.release();
        }
    }
}
