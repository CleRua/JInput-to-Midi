package Joystick;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class Joystick {
	private Controller controller;
	private Component[] components;
	private String[] mapping;
	private int queueSize = 50;
	
	public Joystick(Controller.Type controllerType) {
		this.controller = init_controller(controllerType);
		if(this.controller == null) {
			System.out.println("Joystick introuvable...");
		}
		
		this.components = this.controller.getComponents();
		this.mapping = init_mapping();
		this.controller.setEventQueueSize(queueSize);
	}

	private Controller init_controller(Controller.Type controllerType) {
		Controller controller = null;
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        
        for(int i=0; i < controllers.length && controller == null; i++) {
            if(controllers[i].getType() == controllerType) {
                controller = controllers[i];
                break;
            }
        }
        return(controller);
	}
	
	private String[] init_mapping() {
		String[] returnMapping = new String[this.components.length];
		for(int i=0; i < this.components.length; i++) {
			returnMapping[i] = this.components[i].getName();
		}
		return(returnMapping);
	}
	
	public Component[] getComponents() {
		return(this.components);
	}
	
	public String[] getMapping() {
		return(this.mapping);
	}
	
	public String getControllerName() {
		return(this.controller.getName());
	}
	
	public void poll() {
		this.controller.poll();
	}
	
	public void joystickBufferToMIDI(MIDI midi) {
		EventQueue queue = this.controller.getEventQueue();
    	Event event = new Event();
    	
    	while(queue.getNextEvent(event)) {
            Component component = event.getComponent();
            float componentValue = event.getValue();
            midi.fromJoystick(component, componentValue);
    	}
	}
	
}
