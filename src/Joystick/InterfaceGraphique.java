package Joystick;

import java.awt.GridLayout;
import java.util.Dictionary;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class InterfaceGraphique {
	
	private JFrame f;
	private JPanel[] panels;
	private JPanel portMidi;
	private JPanel buttons;
	private Joystick joystick;
	private MIDI midi;
	
	public InterfaceGraphique(Joystick joystickIn, MIDI midiIn) {
		this.joystick = joystickIn;
		this.midi = midiIn;
		
		//Initialisation Fenêtre
		this.f = new JFrame("Joystick Barco");
		this.f.setVisible(true);
		this.f.setSize(700, 800);
		this.f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initPanels();		
	}
	
	private void initPanels() {
		if (this.joystick == null) {
			JPanel manette = new JPanel();
			manette.add(new JLabel("Pas de manette connectée ..."));
			this.f.setLayout(new GridLayout(1,1,5,5));
			this.f.add(manette);
		}
		else {
			
			int l = this.joystick.getComponents().length;
			this.panels = new JPanel[l];
			this.f.setLayout(new GridLayout(l+4,1,5,5));
			
			//Manette
			JPanel manette = new JPanel();
			manette.add(new JLabel(this.joystick.getControllerName()));
			this.f.add(manette);
			
			//Nom port midi virtuel
			this.portMidi = new JPanel();
			this.portMidi.setLayout(new GridLayout(1,2,5,5));
			this.portMidi.add(new JLabel("Nom Port MIDI Virtuel"));
			this.portMidi.add(new JTextField(this.midi.getNamePort()));
			this.f.add(this.portMidi);
			
			if(this.midi.hasDevice()) {
			
				//Légende
				JPanel infos = new JPanel();
				infos.setLayout(new GridLayout(1,5,5,5));
				infos.add(new JLabel("Nom Composant"));
				infos.add(new JLabel("Type Controlle Joystick"));
				infos.add(new JLabel("Type signal MIDI"));
				infos.add(new JLabel("Valeur signal MIDI"));
				infos.add(new JLabel("Intensité signal MIDI"));
				this.f.add(infos);
				
				//Commandes du joystick
				Dictionary<String, int[]> dictionary = this.midi.getDictionary();
				for (int i=0 ; i<l ; i++) {
					String componentName = this.joystick.getComponents()[i].getName();
					int[] data = dictionary.get(componentName);
					
					this.panels[i] = new JPanel();
					this.panels[i].setLayout(new GridLayout(1,5,5,5));
					
					//Nom commande
					JLabel label = new JLabel(componentName);
					this.panels[i].add(label);
					
					//Type Commande
					String[] choixControlle = {"Boutton","Joystick","Gachette","Direction"};
					JComboBox control = new JComboBox(choixControlle);
					control.setSelectedIndex(data[0]);
					this.panels[i].add(control);
					
					//Type signal MIDI
					String[] choixMidi = {"Note","Control Change","Pitch Bend"};
					JComboBox midiType = new JComboBox(choixMidi);
					midiType.setSelectedIndex(data[1]);
					this.panels[i].add(midiType);
					
					//Valeur signal MIDI
					JTextField midiValeur = new JTextField(Integer.toString(data[2]));
					this.panels[i].add(midiValeur);
					
					//Intensitée signal MIDI
					JTextField midiIntensity = new JTextField(Integer.toString(data[3]));
					this.panels[i].add(midiIntensity);
					
					
					this.f.add(this.panels[i]);
				}
			}
			else {
				JPanel midiOff = new JPanel();
				midiOff.add(new JLabel("Port Midi Virtuel Introuvable..."));
				this.f.add(midiOff);
			}
			
			//Boutons update et save
			this.buttons = new JPanel();
			JButton save = new JButton("Save");
			save.addActionListener(new ButtonListener(this));
			JButton update = new JButton("Update");
			update.addActionListener(new ButtonListener(this));
			this.buttons.add(save);
			this.buttons.add(update);
			this.f.add(this.buttons);
			
		}
	}
	
	public JPanel[] getPanels() {
		return(this.panels);
	}
	
	public JPanel getPort() {
		return(this.portMidi);
	}
	
	public MIDI getMidi() {
		return(this.midi);
	}
}
