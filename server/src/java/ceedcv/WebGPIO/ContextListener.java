package ceedcv.WebGPIO;

import ceedcv.WebGPIO.Actions.wgpioAction;
import com.pi4j.concurrent.DefaultExecutorServiceFactory;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.impl.GpioControllerImpl;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Se encarga de preparar el sistema GPIO al arrancar el contexto en Apache Tomcat. A su vez, se encarga de liberar todos los recursos al finalizar el contexto.
 * @author AlvaroGracia
 * @version 1.00
 * @see http://www.ceedcv.es/ 
 */
@WebListener
public class ContextListener implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {                
        Logger logger = Logger.getLogger("ceedcv.WebGPIO.ContextListener");        
        logger.info("CONTEXT INITIALIZED");  
        
        System.setProperty("pi4j.debug", "1");                                       
                        
        MainController.Executor = new DefaultExecutorServiceFactory();
        GpioFactory.setExecutorServiceFactory(MainController.Executor);
        
        MainController.Provider = new wgpioProvider();
        GpioFactory.setDefaultProvider(MainController.Provider);
        
        MainController.GPIO = new GpioControllerImpl(MainController.Provider);
        
        wgpioAction.Setup();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {              
        Logger logger = Logger.getLogger("ceedcv.WebGPIO.ContextListener");        
 
        if(MainController.GPIO != null)
        {
            MainController.GPIO.shutdown();
            MainController.GPIO = null;   
        }
        
        MainController.Provider.removeAllListeners();
        MainController.Provider = null;        
        MainController.Executor.shutdown();                      
        
        logger.info("CONTEXT DESTROYED");   
    }
}