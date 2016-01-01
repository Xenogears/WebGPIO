window.webGPIO = (typeof parent.webGPIO !== 'undefined' ? parent.webGPIO : new function()
{	
        /**
         * Este objeto tiene los parámetros necesarios para la conexión al servidor (Raspberry Pi)
         * WSHost: El la dirección IP y el puerto que está a la escucha. En caso de especificar NULL se usará la misma dirección que aparece en el objeto window.location.host
         * WSPath: El path donde se encuentra el recurso WebSocket a la escucha
         * WSAutoConnect: En caso de que sea true, la librería procederá a conectarse automáticamente cuando se cargue. 
         */
	this.Parameters = 
	{
            WSHost: null, 
            WSPath: '/WebGPIO/WS',
            WSAutoConnect: true	
	};
	
    
	var WSConnection = null;
	var WSConnectionStatus = false;
	
	/*FRAME*/
	var SendFrame = function(frame)
	{
            WSConnection.send(JSON.stringify(frame));
	};
	
	/*LISTENERS*/
	var WSConnectionStatusListeners = [];
        
        /**
         * Añade un nuevo listener que recibirá los cambios que ocurran sobre la conexión WebSockets.
         * Dicho listener recibe un parámetro "stat" que es "true" en caso de que la conexión esté establecida o false en caso contrario.
         * 
         * @param Function newListener El listener que recibirá los cambios
         */
	this.addConnectionStatusChangeListener = function(newListener)
	{
            WSConnectionStatusListeners.push(newListener);
	};
	
        /**
         * Quita un listener añadido previamente mediante el método "addConnectionStatusChangeListener"
         * 
         * @param Function listener El listener que se quiere quitar
         */
	this.removeConnectionStatusChangeListener = function(listener)
	{
            var index = WSConnectionStatusListeners.indexOf(listener);
            if(index < 0)
                return false;

            WSConnectionStatusListeners = WSConnectionStatusListeners.splice(index, 1);
            return true;
	};
	
        /**
         * Dispara todos los listeners a la escucha de los cambios en el estado de la conexión.
         * 
         * @param Boolean stat El parámetro de estado que se pasa como parámetro a todos los listeners.
         */
	this.dispatchConnectionStatusChangeListener = function(stat)
	{
            for(var i = 0; i < WSConnectionStatusListeners.length; i++)
                WSConnectionStatusListeners[i](stat);
	};
	
	/*PIN EVENTS*/
	var responseListeners = [];
        
        /**
         * Añade un listener se disparará cuando se reciba un mensaje del servidor y cuyo formato coincida con lo especificado en el parámetro where.
         * Ejemplo: Where: { Action: "GET_MODE", Pin: 1}
         * 
         * Cuando se reciba un mensaje del servidor que incluya la propiedad "Action" y equivalga "GET_MODE", además de la propiedad "Pin" con el valor "1" se disparará el método indicado en el parámetro callback.
         * 
         * Esto es útil pues permite añadir listeners para cualquier combinación y cualquier dato que se desee, desde para todos los mensajes (where = {}) o en casos muy específicos.
         * 
         * @param Function callback Método que recibirá el mensaje enviado por el servidor
         * @param Boolean removeCallback En caso de que este parámetro sea true, este listener se borrará automáticamente tras la primera respuesta. En caso de que sea false el listener continuará indefinidamente o hasta que se elimine manualmente
         * @param Object where
         */
	this.addResponseListener = function(callback, removeCallback, where)
	{
            var R = { Callback: callback, RemoveCallback: removeCallback, Where: where };
            responseListeners.push(R);
	};
        
        /**
         * Devuelve todos los listeners de mensajes del servidor que coincidan con al filtro especificado en el parámetro where
         * 
         * @param Object where Filtro. Ejemplo: { Action: "GET_MODE", Pin: 1 }
         * @returns Array<Object> Un vector con todos los listeners encontrados.
         */
        this.getResponseListeners = function(where)
	{
            var listeners = [];

            for(var i = 0; i < responseListeners.length; i++)
            {
                var found = true;			
                for(var tup in responseListeners[i].Where) 
                {
                    if(responseListeners[i].Where[tup] !== where[tup])
                    {
                        found = false;
                        break;
                    }
                }

                if(found)
                    listeners.push(responseListeners[i]);
            }

            return listeners;
	};
	
        /**
         * Elimina un listener específico dedicado a manejar los mensajes recibidos del servidor.
         * 
         * @param Function listener El listener a eliminar
         * @returns {Boolean} true en caso de que se haya encontrado y eliminado. False en caso contrario.
         */
	this.removeResponseListener = function(listener)
	{
            if(typeof listener === 'undefined')
                return false;

            for(var i = 0; i < responseListeners.length; i++)
            {
                if(responseListeners[i] === listener)
                {
                    reponseListeners = responseListeners.splice(i, 1);
                    return true;
                }
            }

            return false;
	};
	
        /**
         * Dispara los listeners que coincidan con el mensaje que se especifique en el parámetro response.
         * Este parámetro sólo se debería usar por la librería de forma interna. El hecho de que resida como un parámetro público es para facilitar las tareas de depuración.
         * 
         * @param Object response El mensaje del servidor
         */
	this.dispatchResponseListener = function(response)
	{
            var listeners = this.getResponseListeners(response);

            for(var i = 0; i < listeners.length; i++)
            {
                if(typeof listeners[i].Callback !== 'undefined')
                    listeners[i].Callback(response);

                if(listeners[i].RemoveCallback === true)
                    this.removeResponseListener(listeners[i]);
            }
	};
	
	/*PIN MODE*/
        /**
         * Establece el modo del pin indicado. Los modos válidos son:
         * "in": Entrada de datos
         * "out": Salida de datos
         * "pwm": Control de potencia. (Unicamente el Pin nº1 soporta este modo de forma nativa. El resto de pines realizan el control mediante emulación por software, lo que implica un gasto de CPU y no es tan preciso que su equivalente nativo.
         * 
         * @param Integer pin El nº de pin deseado
         * @param String mode El modo deseado
         * @param Function callback OPCIONAL - El método que recibirá la confirmación del servidor
         * @returns Boolean True en caso de que el mensaje se haya enviado correctamente. False en caso contrario.
         */
	this.setPinMode = function(pin, mode, callback)
	{
            if(['in', 'out', 'pwm'].indexOf(mode.toLowerCase()) < 0 || typeof pin === 'undefined')
                return false;

            if(typeof callback !== 'undefined')
                this.addResponseListener(callback, true, { Pin: pin, Mode: mode, Action: 'SET_MODE' });

            SendFrame({
                Action: 'SET_MODE',
                Pin: pin,
                Mode: mode
            });

            return true;
	};
	
        /**
         * Consulta al servidor el modo del pin indicado en el parámetro Pin
         * 
         * @param Integer pin El pin deseado
         * @param Function callback El método que recibirá la respuesta del servidor
         * @returns Boolean True en caso de que el mensaje se haya enviado correctamente. False en caso contrario.
         */
	this.getPinMode = function(pin, callback)
	{
            if(typeof pin === 'undefined')
                return false;

            this.addResponseListener(callback, true, { Pin: pin, Action: 'GET_MODE' });

            SendFrame({
                Action: 'GET_MODE',
                Pin: pin
            });

            return true;
	};
	
	/*PIN VALUE*/
        /**
         * Establece un valor en el pin deseado. Según el modo en que esté configurado el pin, el rango válido para los valores es:
         * "out": 1 o 0 (también válido true o false). 0 equivale a 0v y cualquier otro valor equivale a 3.3v.
         * "in" : Modo no válido
         * "pwm" : de 0 a 100. 0 se asume como 0v y 100 como 3.3v. Cualquier valor intermedio se aplica porcentualmente (Ej: 50 equivale a 1.55v). 
         * 
         * @param Integer pin El pin deseado
         * @param [Integer, Boolean] value El valor deseado
         * @param Function callback OPCIONAL - El método que recibirá la confirmación del servidor
         * @returns Boolean True en caso de que el mensaje se haya enviado correctamente. False en caso contrario.
         */
	this.setPinValue = function(pin, value, callback)
	{
            if(typeof pin === 'undefined')
                return false;

            if(typeof callback !== 'undefined')
                this.addResponseListener(callback, true, { Pin: pin, Action: 'SET_VALUE' });

            if(value === true)
                value = 100;
            else if(value === false)
                value = 0;		

            SendFrame({
                Action: 'SET_VALUE',
                Pin: pin,
                Value: value
            });

            return true;
	};
	
        /**
         * Obtiene el valor actual del pin solicitado directamente desde el servidor.
         * 
         * @param Integer El pin deseado
         * @param Function  El método que recibirá el valor desde el servidor
         * @returns Boolean True en caso de que el mensaje se haya enviado correctamente. False en caso contrario.
         */
	this.getPinValue = function(pin, callback)
	{
            if(typeof pin === 'undefined')
                return false;

            this.addResponseListener(callback, true, { Pin: pin, Action: 'GET_VALUE' });

            SendFrame({
                Action: 'GET_VALUE',
                Pin: pin
            });

            return true;
	};
	
	/*REMOTE PIN LISTENER*/
        /**
         * Añade un listener en el servidor para que cuando cambie el valor de un pin de entrada, este notifique a todos los clientes que esten a la escucha.
         * 
         * @param Integer pin El pin deseado
         * @param Function callback El método que recibira los cambios que hayan en el pin de entrada
         * @returns Boolean True en caso de que el listener se haya añadido y el mensaje se haya enviado correctamente. False en caso contrario.
         */
	this.addRemotePinListener = function(pin, callback)
	{
            if(typeof pin === 'undefined' || typeof callback === 'undefined')
                return false;

            this.addResponseListener(callback, false, { Pin: pin, Action: 'EVENT_DISPATCH' });

            SendFrame({
                Action: 'ADD_EVENT_LISTENER',
                Pin: pin
            });

            return true;
	};
        
        /**
         * Elimina un listener añadido previamente con el método addRemotePinListener
         * 
         * @param Function callback El método añadido previamente
         * @returns Boolean True en caso de que el listener se haya eliminado correctamente. False en caso contrario.
         */
        this.removeRemotePinListeners = function(callback)
        {
            if(typeof callback !== 'undefined')
                this.addResponseListener(callback, true, { Action: 'REMOVE_ALL_EVENT_LISTENERS' });
            
            SendFrame({
                Action: 'REMOVE_ALL_EVENT_LISTENERS'
            });

            return true;
        };        
        
	
	/*HCSR04*/
        /**
         * Mide y obtiene la distancia desde un sensor HC-SR04.
         * 
         * @param Integer echoPin El nº de pin conectado al Echo del sensor
         * @param Integer trgPin El nº de pin conectado al Trigger del sensor
         * @param Function callback OPCIONAL - El método que recibirá la distancia medida desde el servidor
         * @returns Double La distancia en centímetros
         */
	this.getDistanceFromSensor = function(echoPin, trgPin, callback)
	{
            if(typeof echoPin === 'undefined' || typeof trgPin === 'undefined')
                return false;

            if(typeof callback !== 'undefined')
                this.addResponseListener(callback, false, { EchoPin: echoPin, TrgPin: trgPin, Action: 'GET_DISTANCE' });

            SendFrame({
                Action: 'GET_DISTANCE',
                TrgPin: trgPin,
                EchoPin: echoPin
            });

            return true;	
	};
	
	/**
         * Procede a conectarse al servidor (Raspberry Pi). En caso de que ya esté conectado, primero se desconecta y después procede a reconectar.
         */
	this.ConnectWS = function()
	{
            if(WSConnection !== null)
                WSConnection.close();

            var Host = (this.Parameters.WSHost === null ? window.location.host : this.Parameters.WSHost);		
            WSConnection = new WebSocket("ws://" + Host + this.Parameters.WSPath);
            WSConnection.onopen = function()
            {
                WSConnectionStatus = true;
                this.dispatchConnectionStatusChangeListener(WSConnectionStatus);
            }.bind(this);

            WSConnection.onclose = function()
            {
                WSConnection = null;
                WSConnectionStatus = false;

                this.dispatchConnectionStatusChangeListener(WSConnectionStatus);				
            }.bind(this);

            WSConnection.onmessage = function(ev)
            {
                var Frame = JSON.parse(ev.data);
                this.dispatchResponseListener(Frame);
            }.bind(this);
	};
	
        /**
         * Desconecta el WebSocket con el servidor.
         * 
         * @returns Boolean True en caso de que la desconexión se haya realizado con éxito, False en caso contrario (Ej: no está conectado).
         */
	this.DisconnectWS = function()
	{
            if(WSConnection !== null)
            {
                WSConnection.close();
                return true;
            }
            else 
                return false;
	};
	
        /**
         * Define la propiedad isWSConnected, que devuelve True en caso de que esté conectado al servidor y False en caso contrario.
         */
	Object.defineProperty(this, "isWSConnected", 
	{
            get: function()
            {
                if(WSConnection === null || WSConnectionStatus === false)
                    return false;
                else		
                    return true;
            }
	});	
	
        /**
         * Envía al servidor el comando para apagar la Raspberry Pi.
         */
	this.shutdownPi = function()
	{
            SendFrame({ Action: 'SHUTDOWN_PI' });
	};
	
        /**
         * Envía al servidor el comando para reiniciar la Raspberry Pi.
         */
	this.rebootPi = function()
	{	
            SendFrame({ Action: 'REBOOT_PI' });
	};
	
        /**
         * Envía al servidor el comando para parar el servicio Apache Tomcat 8.
         */
	this.stopTomcat = function()
	{
            SendFrame({ Action: 'STOP_TOMCAT' });
	};
	
        /**
         * Envía al servidor el comando para reiniciar el servicio Apache Tomcat 8.
         */
	this.restartTomcat = function()
	{
            SendFrame({ Action: 'RESTART_TOMCAT' });
	};
	
	
	//CONSTRUCTOR	
	if(this.Parameters.WSAutoConnect === true)
            this.ConnectWS();
		
});
