import gui.MainWindow;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Main {

	/* TODO si potrebbe fare che accetti come parametro un file di
	 * configurazione: il programma parte già settato con quello
	 * e basta fare solo "START" per avviare il tutto
	 */
	public static void main(String[] args) {
		final File confFile;
		if (args.length == 1) {
			confFile = new File(args[0]);
		} else {
			confFile = null;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				MainWindow window = new MainWindow(confFile);
				window.setVisible(true);
				window.setExtendedState(JFrame.MAXIMIZED_BOTH);
				
//				BinConfiguration bc = new BinConfiguration(10, 12);
//				PacketConfiguration pkt1 = new PacketConfiguration(1, 1, 10, Color.RED);
//				PacketConfiguration pkt2 = new PacketConfiguration(100, 100, 9, Color.YELLOW);
//				
//				List<PacketConfiguration> pkts = new LinkedList<PacketConfiguration>();
//				Collections.addAll(pkts, pkt1, pkt2);
//				
//				ProblemConfiguration problemConf = new ProblemConfiguration(bc, pkts);
//				
//				ProblemConfigurer pc = new ProblemConfigurer(null, problemConf);
//				pc.askUser();
			}
			
		});

	}

}
