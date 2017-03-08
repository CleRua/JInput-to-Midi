package Joystick;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.sound.midi.*;

import net.java.games.input.Component;

public class MIDI {
	private MidiDevice device = null;
	private Receiver receiver;
	private String[] mappingJoystick = null;
	private int[][] mappingData; // Contient le type de commande ainsi que les spécificité du signal MIDI pour chaque composant de la manette.
	private Dictionary<String, int[]> dictionary;
	private String portMidi;
	
	
	public MIDI() {
		mappingData = new int[4][];
		dictionary = new Hashtable<String, int[]>();
	}
	
	public void initMIDI() {
		this.device = findMIDIDevice(this.portMidi);
		
		if (this.device == null) {
			System.out.println("Le port midi est introuvable...");
		}
		else {
			//Mise en marche MidiDevice
			try {
				this.receiver = this.device.getReceiver();
				device.open();
				
			} catch (MidiUnavailableException e) {
				System.out.println("Erreur lors de l'adquisition du receiver");
				e.printStackTrace();
			}
			
			//Construction du dictionnaire de mapping
			this.constructDictionary();
		}
	}
	
	private MidiDevice findMIDIDevice(String virtualMidiDeviceName) {
		MidiDevice returnDevice = null;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].getName().equals(virtualMidiDeviceName) && infos[i].getDescription().equals("External MIDI Port")) { //getDescription().equals permet de palier a un problème de doublons de l'entrée virtuelle
            	try {
					returnDevice = MidiSystem.getMidiDevice(infos[i]);
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
            }
        }
        return(returnDevice);
	}
	
	public void constructDictionary() {
		if(this.mappingData != null && this.mappingJoystick != null) {
			for(int i=0; i < this.mappingJoystick.length; i++) {
				int[] data = new int[4];
				for(int j=0 ; j<4 ; j++) {
					data[j] = this.mappingData[j][i]; // 0 -> Type Commande, 1 -> Type signal MIDI, 2 -> Valeur signal MIDI, 3 -> Intensitée signal MIDI
				}
				this.dictionary.put(mappingJoystick[i], data);
			}
		} 
		else {
			System.out.println("Veuillez initialiser le mapping du joystick et des messages data avant de construire le dictionaire...");
		}
	}
	
	//Getters
	public Dictionary<String, int[]> getDictionary() {
		return(this.dictionary);
	}
		
	public String getNamePort() {
		return(this.portMidi);
	}
		
	public int getNumberComands() {
		return(this.mappingJoystick.length);
	}
	
	//Setters
	public void setMappingJoystick(String[] mappingIn) {
		this.mappingJoystick = mappingIn;
	}
	
	public void setMappingCommande(int[] mappingIn) {
		this.mappingData[0] = mappingIn;
	}
	
	public void setMappingType(int[] mappingIn) {
		this.mappingData[1] = mappingIn;
	}
	
	public void setMappingValeur(int[] mappingIn) {
		this.mappingData[2] = mappingIn;
	}
	
	public void setMappingIntensity(int[] mappingIn) {
		this.mappingData[3] = mappingIn;
	}
	
	public void setPortMidi(String virtualMidiPort) {
		this.portMidi = virtualMidiPort;
	}
	
	
	//Test port Midi
	public Boolean hasDevice() {
		return(this.device != null);
	}
	
	private int intToShortMessageType(int val) {
		switch(val) {
		case 0:
			return(ShortMessage.NOTE_ON);
		case 1:
			return(ShortMessage.CONTROL_CHANGE);
		case 2:
			return(ShortMessage.PITCH_BEND);
		default:
			return(ShortMessage.NOTE_ON);
		}
	}
	
	
	//Traitement des infos envoyées par le joystick.
	public void fromJoystick(Component component, float componentValue) {
		String componentName = component.getName();
	       
		//Affichage
	    System.out.println(componentName);
	    System.out.println(componentValue);
	    if (component.isAnalog()) {
	    	System.out.println("Analog");
	    } else {
	        System.out.println("Digital");
	    }
	        
	    //Conversion data
	    this.dataToMidi(componentName,componentValue);
	}
	
	
	private void dataToMidi(String componentName, float componentValue) {
		int[] data = this.dictionary.get(componentName);
		int intensityMessage;
		int messageType = intToShortMessageType(data[1]);
		int sensibilite = 3; //Sensibilité du controlle de type gachette
		
		try {
			ShortMessage message = new ShortMessage();
			
			switch(data[0]) {
			case 0: //Commande de type Bouton
				intensityMessage = Math.round(data[3]*componentValue);
				if(componentValue < 0.1) {
					message.setMessage(ShortMessage.NOTE_OFF, 0, data[2], 0);
				}
				else {
					message.setMessage(intToShortMessageType(data[1]), 0, data[2], intensityMessage);
				}
				break;
			case 1: //Commande de type Joystick : l'intensité du message est calculée par rapport au componentValue
				intensityMessage = Math.round(data[3]*(componentValue+1)/2);
				if(componentValue < 0.1 && componentValue > -0.1) {
					message.setMessage(ShortMessage.NOTE_OFF, 0, data[2], 0);
				}
				else {
					message.setMessage(messageType, 0, data[2], intensityMessage);
				}
				break;
			case 2: //Commande de type Gachette : l'intensité du message est incrémenté ou décrementé 
				if(componentValue < -0.1 && data[3] > sensibilite) {
					data[3] = data[3] + Math.round(sensibilite*componentValue);
					this.dictionary.put(componentName, data);
				}
				else if(componentValue > 0.1 && data[3] < 127-sensibilite) {
					data[3] = data[3] + Math.round(sensibilite*componentValue);
					this.dictionary.put(componentName, data);
				}
				intensityMessage = Math.round(data[3]);
				if(componentValue < 0.1 && componentValue > -0.1) {
					message.setMessage(ShortMessage.NOTE_OFF, 0, data[2], 0);
				}
				else {
					System.out.println(data[3]);
					message.setMessage(intToShortMessageType(data[1]), 0, data[2], intensityMessage);
				}
				break;
			case 3: //Commande de type Croix Directionnelle : comme la commande Bouton mais avec 4 notes consécutives
				intensityMessage = Math.round(data[3]);
				int value = Math.round(componentValue*10);
				if(componentValue < 0.1) {
					//On éteind tous les messages de la croix directionnelle
					ShortMessage messageOff = new ShortMessage();
					messageOff.setMessage(ShortMessage.NOTE_OFF, 0, data[2], 0);
					this.receiver.send(messageOff, -1);
					messageOff.setMessage(ShortMessage.NOTE_OFF, 0, data[2]+1, 0);
					this.receiver.send(messageOff, -1);
					messageOff.setMessage(ShortMessage.NOTE_OFF, 0, data[2]+2, 0);
					this.receiver.send(messageOff, -1);
					messageOff.setMessage(ShortMessage.NOTE_OFF, 0, data[2]+3, 0);
					this.receiver.send(messageOff, -1);
					//On défini le message pricipal sous note off
					message.setMessage(ShortMessage.NOTE_OFF, 0, data[2], 0);
				}
				else {
					switch(value) {
					case 3: //Haut
						message.setMessage(intToShortMessageType(data[1]), 0, data[2], intensityMessage);
						break;
					case 4: //Haut et Droite
						message.setMessage(intToShortMessageType(data[1]), 0, data[2], intensityMessage);
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+1, intensityMessage);
						break;
					case 5: //Droite
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+1, intensityMessage);
						break;
					case 6: //Droite et Bas
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+1, intensityMessage);
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+2, intensityMessage);
						break;
					case 8://Bas
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+2, intensityMessage);
						break;
					case 9: //Bas et Gauche
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+2, intensityMessage);
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+3, intensityMessage);
						break;
					case 10: //Gauche
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+3, intensityMessage);
						break;
					case 1: //Gauche et Haut
						message.setMessage(intToShortMessageType(data[1]), 0, data[2]+3, intensityMessage);
						message.setMessage(intToShortMessageType(data[1]), 0, data[2], intensityMessage);
						break;
					}
				}
				break;
			}
			
			//Envoi du message midi
			this.receiver.send(message, -1);
			
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
	//Close MidiDevice
	public void close() {
		this.device.close();
	}
}
