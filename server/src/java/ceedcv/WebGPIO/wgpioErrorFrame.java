package ceedcv.WebGPIO;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import com.google.gson.Gson;

/**
 * Implementación de mensaje de error para excepciones no controladas en servidor. Esta trama está diseñada para mostrar mensajes de error en el lado cliente.
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class wgpioErrorFrame extends wgpioGenericFrame
{
    public wgpioErrorFrame(){ this.setAction("ERROR"); }
    public wgpioErrorFrame(String Message)
    {
        this.setAction("ERROR");
        this.setMessage(Message);
    }
    
    @Override
    public String Serialize()
    {
        Gson g = new Gson();
        return g.toJson(this, wgpioErrorFrame.class);
    }
}
