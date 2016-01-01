window.addEventListener("beforeunload", function()
{
    /*window.webGPIO.removeRemotePinListeners(function()
    {*/
        window.webGPIO.DisconnectWS();
    //});
}, false);

window.webGPIOMenu = 
{ 
	mainContainer: null,
	
	setPage: function(p)
	{
		var iframe = document.querySelector('iframe');
		iframe.src = p;	
	},
	
	getPage: function()
	{
		var iframe = document.querySelector('iframe');
		return iframe.src;	
	}
};

Ext.onReady(function()
{
    window.setTimeout(function()
    {
	window.webGPIOMenu.mainContainer = new Ext.Viewport(
        {
            layout: 'border',
            items: 
            [	
                {
                    region: 'west',
                    width: 250,
                    margins: '0 5 0 5',
                    layout: 'accordion',
                    collapsible: true,
                    title: 'Menú',  
                    items: 
                    [
                        {
                        title: 'Ejemplos',
                        iconCls: 'wgpio-icon-examples',
                        items: [
                            {
                                xtype: 'button',
                                html: '<span class="SampleTitle">Piano</span><span class="SampleDesc">Entrada, Eventos, Audio HTML5</span>',
                                cls: 'wgpio-example-button',
                                height: 50,
                                iconCls: 'wgpio-icon-piano',
                                listeners: {
                                    click: function()
                                    {
                                        ﻿window.webGPIOMenu.setPage('examples/piano.html');
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                html: '<span class="SampleTitle">Radar</span><span class="SampleDesc">E/S, Asincronía, Gráficos Ext JS</span>',
                                cls: 'wgpio-example-button',
                                height: 50,
                                iconCls: 'wgpio-icon-radar',
                                listeners: {
                                    click: function()
                                    {
                                        ﻿window.webGPIOMenu.setPage('examples/radar.html');
                                    }                                                            
                                }
                            },
                            {
                                xtype: 'button',
                                html: '<span class="SampleTitle">PWM</span><span class="SampleDesc">Salida, Control PWM, Canvas</span>',
                                cls: 'wgpio-example-button',
                                height: 50,
                                iconCls: 'wgpio-icon-led',
                                listeners: {
                                    click: function()
                                    {
                                        ﻿window.webGPIOMenu.setPage('examples/pwm.html');
                                    }                                                            
                                }
                            }]  
                    }, 
                    {
                        title: 'Documentación',
                        html: '',
                        iconCls: 'wgpio-icon-api',
                        items: [
                            {
                                xtype: 'button',
                                html: '<span class="SampleTitle">API Servidor</span><span class="SampleDesc">JavaDoc WebGPIO</span>',
                                cls: 'wgpio-example-button',
                                height: 50,
                                iconCls: 'wgpio-icon-code',
                                listeners: {
                                    click: function()
                                    {
                                        ﻿window.webGPIOMenu.setPage('javadoc/index.html');
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                html: '<span class="SampleTitle">API Cliente</span><span class="SampleDesc">Librería WebGPIO</span>',
                                cls: 'wgpio-example-button',
                                height: 50,
                                iconCls: 'wgpio-icon-code',
                                listeners: {
                                    click: function()
                                    {
                                        ﻿window.webGPIOMenu.setPage('api.html');
                                    }
                                }
                            }]
                    }, 
                    {
                        title: 'Hardware',
                        iconCls: 'wgpio-icon-settings',
                        items: 
                        [{
                                xtype: 'splitbutton',
                                title: 'Apache Tomcat',
                                html: 'Apache Tomcat',
                                cls: 'wgpio-button',
                                iconCls: 'wgpio-icon-tomcat',
                                width: '100%',
                                height: 30,
                                menu: [
                                        { text:'Reiniciar', listeners:{ click: window.webGPIO.restartTomcat } },
                                        { text:'Parar', listeners:{ click: window.webGPIO.stopTomcat } },					
                                ]
                        }, {
                                xtype: 'splitbutton',
                                title: 'Raspberry Pi',
                                html: 'Raspberry Pi',
                                cls: 'wgpio-button',
                                iconCls: 'wgpio-icon-raspberry',
                                width: '100%',
                                height: 30,
                                menu: [
                                        { text:'Reiniciar', listeners:{ click: window.webGPIO.rebootPi } },
                                        { text:'Apagar', listeners:{ click: window.webGPIO.shutdownPi } },							
                                ]
                        }]
                    }]
                },
                {
                    xtype: 'container',
                    region: 'north',
                    height: 40, 
                    cls: 'topBar',	
                    html: '<span class="title">WebGPIO - Menú</span>'
                },
                {
                    region: 'center',
                    //split: true,
                    collapsible: false,
                    collapsed: false,
                    //margins: '0 0 0 0',
                    cls: 'contentPanel',
                    items : [{
                            xtype : "component",
                            autoEl : {
                                    tag : "iframe",
                                    src : "about:blank"
                            }
                    }]
                },
                {
                    xtype: 'container',
                    id: 'status-panel',
                    region: 'south',
                    split: true,
                    collapsible: false,
                    collapsed: false,
                    cls: 'bottomBar',
                    html: '<span id="serverStatus" class="Disconnected">Desconectado</span>'
                }				
            ]
        });
        
        var statLabel = document.getElementById('serverStatus');
        statLabel.addEventListener('click', function()
        {
                if(window.webGPIO.isWSConnected)
                        window.webGPIO.DisconnectWS();
                else
                        window.webGPIO.ConnectWS();

        }, false);
        
        window.webGPIO.dispatchConnectionStatusChangeListener(window.webGPIO.isWSConnected);
    }, 500);
});

window.webGPIO.addResponseListener(function(f)
{
    Ext.Msg.alert('¡Error!', f.Message);
}, false, { Action: 'ERROR' });

window.webGPIO.addResponseListener(function(f)
{
    Ext.Msg.alert('Mensaje del servidor', f.Message);
}, false, { Action: 'SHOW_MESSAGE' });

window.webGPIO.addConnectionStatusChangeListener(function(stat)
{
    var statLabel = document.getElementById('serverStatus');
    if(statLabel !== null)
    {
        statLabel.innerHTML = (stat ? "Conectado" : "Desconectado");
        statLabel.className = (stat ? "Connected" : "Disconnected");
    }
});
