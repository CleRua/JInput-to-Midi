package Joystick;

import net.java.games.input.Controller;

public class Main {

	public static void main(String[] args) {
		//Initialisation
		int tempsRafraichissement = 10;
		Joystick joystick = new Joystick(Controller.Type.GAMEPAD); //Controller.Type.STICK pour certaines manettes
		MIDI midi = new MIDI();
		midi.setMappingJoystick(joystick.getMapping()); //Attention: initialiser l'objet MIDI ET son mapping joystick avant de faire appel à SaveFile et sa méthode read() qui initialise MIDI.
		
		//Fichier sauvegarde
		SaveFile file = new SaveFile(midi);
		file.read(); //Initialisation mappingData et portMidi de l'objet MIDI midi d'après la sauvegarde.
		
		//Initialisation MidiDevice et construction dictionaire Joystick/Data
		midi.initMIDI();
		
		//Initialisation interface graphique
		InterfaceGraphique gui = new InterfaceGraphique(joystick, midi);
		
        
		//Boucle
        while(true){
        	joystick.poll();
        	joystick.joystickBufferToMIDI(midi);
        	
        	
        	//Temps de rafraichissement de la boucle.
        	try {
        		Thread.sleep(tempsRafraichissement);
        	} catch (InterruptedException e) {
            	 e.printStackTrace();
            }
        }
	}            
        	
        
}
