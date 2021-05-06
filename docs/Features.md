---
layout: default
title: Features
nav_order: 2
permalink: /
---
Here you can find a short description on the different features of each screen.

## Home
The Home screen is designed to allow the user to form a message in a multitude of ways. The top portion of the Home screen contains the grid which is used to encode haptograms by drawing a pattern. In addition to single strokes, the newer version of the software now provides the capability of drawing multiple single-touch strokes, e.g. it is now possible to draw a cross or any patterns that require more than one stroke. Along with drawing patterns, the user is now able to type messages via an input field which autocompletes words or phrases that are known to the local library of word-haptogram pairings. Alternatively, the user can press a button next to the input field, which activates speech-to-text, enabling the user to speak a word or phrase. When using the input field or speech-to-text, the system will draw the corresponding haptogram pattern on the grid if the pattern is known. 
Below the grid are two buttons: The Clear button is used to erase the current pattern formed by any of the above three mentioned methods. This is particularly useful if the user makes a mistake when drawing a pattern consisting of multiple strokes. The Complete button must be pressed to confirm any drawn pattern. Upon doing so, the software checks if the pattern is known. If the pattern can be found in the local library, the word or phrase appears below the two buttons and is spoken via text-to-speech, also added in the new version. The spoken phrase can be repeated by pressing the icon next to the text. Additionally, a new button appears in the bottom right corner. Pressing this will publish a message via the message broker to the vest wearer or the ontology. Once sent, the screen resets to its default state, allowing a further message to be constructed. Conversely, if the pattern is not known, the system informs the user by displaying a message accompanied by text-to-speech output. 


## Create
The Create screen enables the user to add a new haptogram-word pairing to the local library. This is accomplished by first drawing a pattern (single stroke or multiple strokes), followed by entering a text via the input field. Pressing the Add Entry button will validate the user’s entry and add the word-haptogram pairing to the local library if a series of tests are successfully passed. If any part of the validation fails, a relevant error message is displayed to the user (e.g. empty entry, a haptogram already exists etc.). 

## Library
The Library screen displays a list of words known to the system. Keeping the Delete All button pressed removes all words from the library. 

## Settings
The settings screen enables the user to configure various features:

|Setting|Description|
|-------|-----------|
| Grid Size | Modifies the size of the grid to make it uniform (e.g., 3x3, 4x4, 5x5 etc.). The default value is 4. |
| Enable Reference Frame | A toggle which, when enabled, lengthens the first vibration. In this instance, it sets the first vibration to a length of 500ms. When disabled, this defaults to the time set by *Actuator Frame Duration.* |
| Actuator Frame Duration | This setting determines the duration for which a single vibration motor is activated. Defaults to 300ms with a maximum duration of 400ms. |
| Actuator Overlap Time | Determines the duration of activation overlap of two consecutive vibration motors. This overlap is intended to create the illusion of a continuous motion. This is a value between 0ms (no overlap) and 200ms. Defaults to 50ms. |
| Message Destination | A toggle determining the destination of the message. When enabled, the message is sent to the ontology in the form of a query. For example, a deafblind user could ask “Where is my assistant?”. If this setting is disabled, messages are sent to the vest wearer, triggering the vibration pattern on the back where the matrix of vibration motors is located. |