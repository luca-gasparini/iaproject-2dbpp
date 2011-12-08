import gui.MainWindow;

import javax.swing.SwingUtilities;


public class Main {

	/* TODO si potrebbe fare che accetti come parametro un file di
	 * configurazione: il programma parte già settato con quello
	 * e basta fare solo "START" per avviare il tutto
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				MainWindow window = new MainWindow();
				window.setVisible(true);
			}
			
		});

	}

}
