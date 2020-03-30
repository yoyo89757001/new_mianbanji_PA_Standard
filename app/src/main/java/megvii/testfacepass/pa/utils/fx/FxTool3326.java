package megvii.testfacepass.pa.utils.fx;

class FxTool3326 implements FxTool.IFxTool { 
	public FxTool3326() { 
	}

	private static final int MODE_UNKOWN = 0;
	private static final int MODE_FAILED = -1;
	private static final int MODE_ENABLED = 1;
	private static final int MODE_INPUT = 2;
	private static final int MODE_OUTPUT = 3;
	
	private static final int GPIO_DOOR_GPIO1 = 116;
	private static final int GPIO_DOOR_GPIO2 = 115; 
	private static final int GPIO_DOOR_CONTROL = 118; 
	private static final int GPIO_FLASH_LIGHT = 109;
	private static final int GPIO_CPU_FAN = 108;
	private static final int GPIO_LED1_CONTROL = 105;
	private static final int GPIO_LED2_CONTROL = 106;
	private static final int GPIO_LED3_CONTROL = 107;
	private static final int GPIO_LIGHT_SENSOR = 17; 

	private int StateDoorGPIO1 = MODE_UNKOWN;
	private int StateDoorGPIO2 = MODE_UNKOWN;
	private int StateDoorControl = MODE_UNKOWN;
	private int StateDoorControlEx = MODE_UNKOWN;
	private int StateFlashLight = MODE_UNKOWN;
	private int StateCpuFan = MODE_UNKOWN;
	private int StateLed1Control = MODE_UNKOWN;
	private int StateLed2Control = MODE_UNKOWN;
	private int StateLed3Control = MODE_UNKOWN;
	private int StateLightSensor = MODE_UNKOWN; 
	
    @Override
    public int fxDoorGPIO1() { 
    	if (StateDoorGPIO1 <= 0) {
    		StateDoorGPIO1 = (GPIO.enable(GPIO_DOOR_GPIO1)? MODE_ENABLED : MODE_FAILED);
    	}
		return StateDoorGPIO1 > 0? GPIO.readValue(GPIO_DOOR_GPIO1) : -1;
	} 
    
    @Override
    public int fxDoorGPIO2() { 
    	if (StateDoorGPIO2 <= 0) {
    		StateDoorGPIO2 = (GPIO.enable(GPIO_DOOR_GPIO2)? MODE_ENABLED : MODE_FAILED);
    	}
		return StateDoorGPIO2 > 0? GPIO.readValue(GPIO_DOOR_GPIO2) : -1;
	} 
    
    @Override
    public int fxLightSensor() { 
    	if (StateLightSensor <= 0) {
    		StateLightSensor = (GPIO.enable(GPIO_LIGHT_SENSOR)? MODE_ENABLED : MODE_FAILED);
    	}
		if (MODE_ENABLED == StateLightSensor && GPIO.setInputMode(GPIO_LIGHT_SENSOR)) { 
			StateLightSensor = MODE_INPUT;
		}
    	
		return StateLightSensor == MODE_INPUT? GPIO.value(GPIO_LIGHT_SENSOR) : -1;
	} 
    


    @Override
    public  void fxDoorGPIO1(boolean onoff) { 
    	if (StateDoorGPIO1 <= 0) {
    		StateDoorGPIO1 = (GPIO.enable(GPIO_DOOR_GPIO1)? MODE_ENABLED : MODE_FAILED);
    	}
    	if (StateDoorGPIO1 > 0) {
    		GPIO.writeValue(GPIO_DOOR_GPIO1, onoff? 1 : 0);  
    	}
	} 
    
    @Override
    public  void fxDoorGPIO2(boolean onoff) { 
    	if (StateDoorGPIO2 <= 0) {
    		StateDoorGPIO2 = (GPIO.enable(GPIO_DOOR_GPIO2)? MODE_ENABLED : MODE_FAILED);
    	}
    	if (StateDoorGPIO2 > 0) { 
    		GPIO.writeValue(GPIO_DOOR_GPIO2, onoff? 1 : 0); 
    	}
	} 
    
    @Override
    public  void fxDoorControl(boolean onoff) {  
    	if (StateDoorControl <= 0) {
    		StateDoorControl = (GPIO.enable(GPIO_DOOR_CONTROL)? MODE_ENABLED : MODE_FAILED);
    	}

    	if (StateDoorControl > 0) { 
    		GPIO.writeValue(GPIO_DOOR_CONTROL, onoff? 1 : 0); 
    	}
	}
    @Override
    public  void fxDoorControlEx(boolean onoff) {  
	}
	
    @Override
    public  void fxFlashLight(boolean onoff) { 
    	if (StateFlashLight <= 0) { 
            StateFlashLight = (GPIO.enable(GPIO_FLASH_LIGHT)? MODE_ENABLED : MODE_FAILED);
    	}
    	if (StateFlashLight > 0) {
    		GPIO.writeValue(GPIO_FLASH_LIGHT, onoff? 1 : 0); 
    	}
	}
	
    @Override
    public  void fxCpuFan(boolean onoff) { 
    	if (StateCpuFan <= 0) {
    		StateCpuFan = (GPIO.enable(GPIO_CPU_FAN)? MODE_ENABLED : MODE_FAILED);
    	}
    	if (StateCpuFan > 0) {
    		GPIO.writeValue(GPIO_CPU_FAN, onoff? 1 : 0); 
    	}
	}
	
    @Override
    public  void fxLED1Control(boolean onoff) { 
    	if (StateLed1Control <= 0) {
    		StateLed1Control = (GPIO.enable(GPIO_LED1_CONTROL)? MODE_ENABLED : MODE_FAILED);
    	}
    	if (StateLed1Control > 0) {
    		GPIO.writeValue(GPIO_LED1_CONTROL, onoff? 1 : 0); 
    	}
	}
	
    @Override
    public  void fxLED2Control(boolean onoff) { 
    	if (StateLed2Control <= 0) {
    		StateLed2Control = (GPIO.enable(GPIO_LED2_CONTROL)? MODE_ENABLED : MODE_FAILED);
    	}
    	if (StateLed2Control > 0) { 
    		GPIO.writeValue(GPIO_LED2_CONTROL, onoff? 1 : 0); 
    	}
	}
	
    @Override
    public  void fxLED3Control(boolean onoff) { 
    	if (StateLed3Control <= 0) {
    		StateLed3Control = (GPIO.enable(GPIO_LED3_CONTROL)? MODE_ENABLED : MODE_FAILED);
    	} 
    	if (StateLed3Control > 0) { 
    		GPIO.writeValue(GPIO_LED3_CONTROL, onoff? 1 : 0); 
    	}
	} 
}
