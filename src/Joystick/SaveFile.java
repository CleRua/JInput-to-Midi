package Joystick;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class SaveFile {
	private File file;
	private MIDI midi;
	
	public SaveFile(MIDI midiIn) {
		this.file = new File("src/Joystick/save_file.txt");
		if(midiIn != null) { //Lorsqu'on utilise cette classe pour sauvegarder des parametres on initialise par new SaveFile(null)
			this.midi = midiIn;
		}
	}
	
	//Initialise des paramètres cruciaux de l'objet MIDI midi.
	public void read() {
		try{
			InputStream ips=new FileInputStream(this.file); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			int[] array;
			int l = midi.getNumberComands();
			for(int i=0 ; i<=4 ; i++) {
				line = br.readLine();
				switch(i) {
				case 0: //Type de commande
					if(line != null) {
						array = Arrays.stream(line.substring(1, line.length()-1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
						if(array.length == l) {
							midi.setMappingCommande(array);
						}
						else {
							midi.setMappingCommande(new int[l]);
						}
					}
					else {
						midi.setMappingCommande(new int[l]);
					}
					break;
				case 1: //Type de signal MIDI
					if(line != null) {
						array = Arrays.stream(line.substring(1, line.length()-1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
						if(array.length == l) {
							midi.setMappingType(array);
						}
						else {
							midi.setMappingType(new int[l]);
						}
					}
					else {
						midi.setMappingType(new int[l]);
					}
					break;
				case 2: //Valeur du signal MIDI
					if(line != null) {
						array = Arrays.stream(line.substring(1, line.length()-1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
						if(array.length == l) {
							midi.setMappingValeur(array);
						}
						else {
							midi.setMappingValeur(new int[l]);
						}
					}
					else {
						midi.setMappingValeur(new int[l]);
					}
					break;
				case 3: //Intensitée signal MIDI
					if(line != null) {
						array = Arrays.stream(line.substring(1, line.length()-1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
						if(array.length == l) {
							midi.setMappingIntensity(array);
						}
						else {
							midi.setMappingIntensity(new int[l]);
						}
					}
					else {
						midi.setMappingIntensity(new int[l]);
					}
					break;
				case 4: //Port MIDI Virtuel
					midi.setPortMidi(line);
					break;
				default:
					
				}
			}
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
	}
	
	
	//Permet de sauvegarder les paramètres de la fenêtre.
	public void write(int[][] infos, String portMidi) {
		int l = infos[0].length;
		FileWriter fr;
		try {
			
			fr = new FileWriter(this.file, false);
			for(int i=0 ; i<4 ; i++) {
				fr.write("[");
				for(int j=0 ; j<l ; j++) {
					if(j == (l-1)) {
						fr.write(infos[i][j]+"");
					}
					else {
						fr.write(infos[i][j]+",");
					}
				}
				fr.write("]");
				fr.write("\r\n");
			}
			fr.write(portMidi);
			
			fr.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
