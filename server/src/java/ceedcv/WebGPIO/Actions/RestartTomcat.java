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
 * Acción: RESTART_TOMCAT<br/><br/>
 * 
 * Acción para reiniciar el servidor Apache Tomcat. Es necesario que el servicio haya sido iniciado por un usuario con permisos de administración (root).
 * 
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
public class RestartTomcat extends wgpioAction
{
    public RestartTomcat(){ super("RESTART_TOMCAT"); }

    /**
     * @param Frame Trama con la solicitud
     * @param Session Sesión que realiza la solicitud
     * @throws Exception En caso de que el comando no se haya podido ejecutar
     */
    @Override
    public void onAction(wgpioGenericFrame Frame, Session Session) throws Exception
    {
        String[] result = com.pi4j.util.ExecUtil.execute("sudo /etc/init.d/tomcat8 restart");
        StringBuilder builder = new StringBuilder();
        
        for(int i = 0; i < result.length; i++)
            builder.append(String.format("%s\r\n", result[i]));
           
        if(builder.length() <= 0)
            builder.append("Reiniciando Apache Tomcat...");
        
        Frame.setMessage(builder.toString());
        Frame.setAction("SHOW_MESSAGE");

        wgpioUtils.SendAsync(Session, Frame);
    }
}
