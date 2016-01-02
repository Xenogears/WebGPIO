/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ceedcv.WebGPIO.Actions;

import ceedcv.WebGPIO.Frames.wgpioGenericFrame;
import ceedcv.WebGPIO.wgpioUtils;
import javax.websocket.Session;

/**
 * Acción: SHUTDOWN_PI<br/><br/>
 * 
 * Acción para apagar la Raspberry Pi. Es necesario que el servicio Apache Tomcat haya sido iniciado por un usuario con permisos de administración (root).
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class ShutdownPi extends wgpioAction
{
    public ShutdownPi(){ super("SHUTDOWN_PI"); }

    /**
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     * @throws Exception En caso de que el comando no se haya podido ejecutar
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, Session Session) throws Exception
    {
        String[] result = com.pi4j.util.ExecUtil.execute("sudo halt");
        StringBuilder builder = new StringBuilder();
        
        for(int i = 0; i < result.length; i++)
            builder.append(String.format("%s\r\n", result[i]));
        
        if(builder.length() <= 0)
            builder.append("Apagando el sistema...");
        
        Frame.setMessage(builder.toString());
        Frame.setAction("SHOW_MESSAGE");
        
        wgpioUtils.SendAsync(Session, Frame);   
    }
}
