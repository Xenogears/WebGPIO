package ceedcv.WebGPIO.Frames;

import com.google.gson.Gson;

public class wgpioGenericFrame
{
    private String Action = null;
    private String Mode = null;
    private int Pin = -1;
    private int Value = 0;
    private int TrgPin = -1;
    private int EchoPin = -1;
    private String Message = null;
    private double Distance = -1;
    
    
    public void setAction(String Action){ this.Action = Action; }    
    public void setMode(String Mode){ this.Mode = Mode; }
    public void setPin(int Pin){ this.Pin = Pin;  }
    public void setValue(int Value){ this.Value = Value; }
    public void setTrgPin(int TrgPin){ this.TrgPin = TrgPin; }
    public void setEchoPin(int EchoPin){ this.EchoPin = EchoPin; }
    public void setMessage(String Message){ this.Message = Message; }
    public void setDistance(double Distance){ this.Distance = Distance; }
    
    public String getAction(){ return this.Action; }
    public String getMode(){ return this.Mode; }
    public int getPin(){ return this.Pin; }
    public int getValue(){ return this.Value; }
    public int getTrgPin(){ return this.TrgPin; }
    public int getEchoPin(){ return this.EchoPin; }
    public String getMessage(){ return this.Message; }
    public double getDistance(){ return this.Distance; }

    public String Serialize()
    {
        Gson g = new Gson();
        return g.toJson(this, wgpioGenericFrame.class);
    }
}
