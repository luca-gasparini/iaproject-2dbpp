import gui.MainWindow;
import gui.ProblemConfigurer;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import logic.BinConfiguration;
import logic.PacketConfiguration;
import logic.ProblemConfiguration;


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
