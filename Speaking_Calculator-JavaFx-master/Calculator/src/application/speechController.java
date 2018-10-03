package application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class speechController {

	private int calculationResult;

	EnglishNumberToString numberToString = new EnglishNumberToString();
	EnglishStringToNumber stringToNumber = new EnglishStringToNumber();
	TextToSpeech textToSpeech = new TextToSpeech();

	@FXML
	private Text output;
	
	@FXML
	private Button close;

	// Logger
	private Logger logger = Logger.getLogger(getClass().getName());

	// Variables
	private String result;

	// Threads
	Thread speechThread;
	Thread resourcesThread;

	// LiveRecognizer
	private LiveSpeechRecognizer recognizer;

	protected void start() {
		// Loading Message
		logger.log(Level.INFO, "Loading..\n");

		// Configuration
		Configuration configuration = new Configuration();

		// Load model from the jar
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

		// if you want to use LanguageModelPath disable the 3 lines after which
		// are setting a custom grammar->

		// configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")

		// Grammar
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);

		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}

		// Start recognition process pruning previously cached data.
		recognizer.startRecognition(true);

		// Start the Thread
		startSpeechThread();
		startResourcesThread();
	}

	/**
	 * Starting the main Thread of speech recognition
	 */
	protected void startSpeechThread() {

		// alive?
		if (speechThread != null && speechThread.isAlive())
			return;

		// initialise
		speechThread = new Thread(() -> {
			logger.log(Level.INFO, "You can start to speak...\n");
			try {
				while (true) {
					/*
					 * This method will return when the end of speech is
					 * reached. Note that the end pointer will determine the end
					 * of speech.
					 */
					SpeechResult speechResult = recognizer.getResult();
					if (speechResult != null) {

						result = speechResult.getHypothesis();
						System.out.println("You said: [" + result + "]\n");
						makeDecision(result);
						// logger.log(Level.INFO, "You said: " + result + "\n")

					} else
						logger.log(Level.INFO, "I can't understand what you said.\n");

				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, null, ex);
			}

			logger.log(Level.INFO, "SpeechThread has exited...");
		});

		// Start
		speechThread.start();

	}

	/**
	 * Starting a Thread that checks if the resources needed to the
	 * SpeechRecognition library are available
	 */
	protected void startResourcesThread() {

		// alive?
		if (resourcesThread != null && resourcesThread.isAlive())
			return;

		resourcesThread = new Thread(() -> {
			try {

				// Detect if the microphone is available
				while (true) {
					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
						// logger.log(Level.INFO, "Microphone is available.\n")
					} else {
						// logger.log(Level.INFO, "Microphone is not
						// available.\n")

					}

					// Sleep some period
					Thread.sleep(350);
				}

			} catch (InterruptedException ex) {
				logger.log(Level.WARNING, null, ex);
				resourcesThread.interrupt();
			}
		});

		// Start
		resourcesThread.start();
	}

	/**
	 * Takes a decision based on the given result
	 */
	public void makeDecision(String speech) {

		if ("fuck you mary".equalsIgnoreCase(speech)) {
			textToSpeech.speak("You too motherfucker", 1.5f, false, true);
			return;
		} else if ("how are you".equalsIgnoreCase(speech)) {
			textToSpeech.speak("Good thank you", 1.5f, false, true);
			return;
		}

		// Split the sentence
		String[] array = speech.split(" ");

		// return if user said only one number
		if (array.length != 3)
			return;

		// Find the two numbers
		int number1 = stringToNumber.convert(array[0]);
		int number2 = stringToNumber.convert(array[2]);

		// Calculation result in int representation

		String symbol = "?";

		// Find the mathematical symbol
		if ("plus".equals(array[1])) {
			calculationResult = number1 + number2;
			symbol = "+";
		} else if ("minus".equals(array[1])) {
			calculationResult = number1 - number2;
			symbol = "-";
		} else if ("multiply".equals(array[1])) {
			calculationResult = number1 * number2;
			symbol = "*";
		} else if ("division".equals(array[1])) {
			calculationResult = number1 / number2;
			symbol = "/";
		} else if ("power".equals(array[1])) {
			calculationResult = (int) Math.pow(number1, number2);
			symbol = "^";
		}

		String res = numberToString.convert(Math.abs(calculationResult));
		
		output.setText(String.valueOf(calculationResult));

		// With words
		System.out.println("Said:[ " + speech + " ]\n\t\t which after calculation is:[ "
				+ (calculationResult >= 0 ? "" : "minus ") + res + " ] \n");

		// With numbers and math
		System.out.println("Said:[ " + number1 + " " + symbol + " " + number2 + "]\n\t\t which after calculation is:[ "
				+ calculationResult + " ]");

		// Speak Mary Speak
		textToSpeech.speak((calculationResult >= 0 ? "" : "minus ") + res, 1.5f, false, true);
	}

	@FXML
	public void speachRes() {
		start();
		output.setText(String.valueOf(calculationResult));
	}
	
	public void closeButton(ActionEvent event){
		Stage stage = (Stage) close.getScene().getWindow();
		stage.close();
	}

}
