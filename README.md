# JInput-to-Midi
Transform a Gamepad signal into a MIDI signal using a JInput and a virtual midi device with LoopMidi

# Before running the code #

If you are planning to use a PS3 controller you have to use Better DS3 (Programs Needed/DS3 Tool) and configure the controller as a XBox controller.

Be sure to install LoopMidi (Programs Needed/LoopMidi) to create a virtual midi device on your computer. 
By default, the code connects itself to a virtual midi device named "MIDI_Java".


# Understanding the save file #

## 1st line : type of comand (from 0 to 3) : ##
  - 0 -> Button type of command : Sends a MIDI signal when the button is pressed
  - 1 -> Joystick type of command : Sends a MIDI signal each time the state of the joystick changes and sends an OFF message when the joystick signal is around 0.7
  - 2 -> Trigger type of command : Just like a Joystick command but the intensity of the message increases/decreases depending on the time a direction is hold on the axis.
  - 3 -> Directional Cross type of command : Each direction of the cross sends a different note (the 4 notes are consecutive)
  
## 2nd line : type of the MIDI signal (from 0 to 2) : ##
  - 0 -> Note message
  - 1 -> Control Change message
  - 2 -> Pitch Bend message
  
## 3rd line : value of the MIDI signal (from 0 to 127) : ##
  - note of the message
  
## 4th line : intensity of the MIDI signal (from 0 to 127) : ##
  - intensity of the message
  
## 5th line : Name of the virtual MIDI device ##

## Example (joystick with 16 components) : ##
[0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0]

[0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0]

[78,79,80,81,82,89,76,79,81,82,83,84,86,88,91,93]

[120,120,120,120,120,120,120,120,120,120,120,120,120,120,120,120]

MIDI_Java


# More Info #

## int tempsRafraichissement ##
Sets the interval (in milliseconds) between 2 pollings of the controller 
(JInput is a library based on polling, I'm still looking for an asynchronous method for reading the controller signals).

