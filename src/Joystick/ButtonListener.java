package Joystick;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ButtonListener implements ActionListener {

	private InterfaceGraphique gui;
	
	public ButtonListener(InterfaceGraphique guiIn) {
		this.gui = guiIn;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JPanel[] panels = this.gui.getPanels();
		int l = panels.length;
		int[][] infos = new int[4][l];
		Component[] components;
		
		
		for(int i=0 ; i<l ; i++) {
			//Components du panel
			components = panels[i].getComponents();
			//Infos commande Joystick
			JComboBox infoCommande = (JComboBox) components[1];
			infos[0][i] = infoCommande.getSelectedIndex();
			//Infos Type signal MIDI
			JComboBox infoType = (JComboBox) components[2];
			infos[1][i] = infoType.getSelectedIndex();
			//Infos Valeur signal MIDI
			JTextField infoValeur = (JTextField) components[3];
			infos[2][i] = Integer.parseInt(infoValeur.getText());
			//Infos Intensitée signal MIDI
			JTextField infoIntensity = (JTextField) components[4];
			infos[3][i] = Integer.parseInt(infoIntensity.getText());
		}
		
		//Port MIDI Virtuel
		String portMidi = ((JTextField) this.gui.getPort().getComponents()[1]).getText();
		
		if(e.getActionCommand().equals("Save")) {
			SaveFile file = new SaveFile(null);
			file.write(infos,portMidi);
		}
		else if(e.getActionCommand().equals("Update")) {
			//Mise a jour des infos
			this.gui.getMidi().setMappingCommande(infos[0]);
			this.gui.getMidi().setMappingType(infos[1]);
			this.gui.getMidi().setMappingValeur(infos[2]);
			this.gui.getMidi().setMappingIntensity(infos[3]);
			this.gui.getMidi().setPortMidi(portMidi);
			//Reconstruction du dictionaire
			this.gui.getMidi().constructDictionary();
		}
		
	}

}
