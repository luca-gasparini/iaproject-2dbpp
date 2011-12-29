package BLFCore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import core.BLFTest.BLFTestCoreConfiguration;

import logic.Bin;
import logic.BinConfiguration;
import logic.ConfigurationManager;
import logic.Packet;
import logic.PacketConfiguration;
import logic.ProblemConfiguration;

public class PackingProcedures {

	// Placing.. pseudocodice preso dal paper con qualche modifica per i casi
	// particolari..
	public static ArrayList<CandidatePoint> Placing(double h,
			LinkedList<Point> Cp, LinkedList<Point> Dp) {

		ArrayList<CandidatePoint> E = new ArrayList<CandidatePoint>();

		if (Cp.size() == 0 || Dp.size() == 0)
			return E;

		int i = 0;
		int j = 0;

		ArrayList<Edge> C = new ArrayList<Edge>();
		ArrayList<Edge> D = new ArrayList<Edge>();
		C.add(null);
		D.add(null); // gli indici nel paper partono da 1
		// converto in liste di lati orizzontali
		// in realt� in casi particolari mi servono anche dei singoli punti..
		// ad esempio quando salgo e scendo nella stessa verticale

		for (i = 0; i < Cp.size() - 1; i++) {
			Edge app = new Edge(Cp.get(i), Cp.get(i + 1));
			if (app.isHorizontal())
				C.add(app);
			else if (i > 0) {
				Edge app2 = new Edge(Cp.get(i - 1), Cp.get(i));
				if (app2.isVertical())
					C.add(new Edge(app2.getLowerPoint(), app2.getLowerPoint()));
			}
		}

		for (i = 0; i < Dp.size() - 1; i++) {
			
			Edge app = new Edge(Dp.get(i), Dp.get(i + 1));
			if (app.isHorizontal())
				D.add(app);
			else if (i > 0) {
				Edge app2 = new Edge(Dp.get(i - 1), Dp.get(i));
				if (app2.isVertical())
					D.add(new Edge(app2.getUpperPoint(), app2.getUpperPoint()));
			}
		}

		i = 1;
		j = 1;
		int m = C.size() - 1;
		int p = D.size() - 1;

		E.add(new CandidatePoint(Cp.get(0), Dp.get(0).y - Cp.get(0).y >= h));

		while (i < m || j < p) {
			while (j < p
					&& D.get(j).getRightPoint().x <= C.get(i).getRightPoint().x) {
				j++;
				Point M = new Point(D.get(j).getLeftPoint().x, C.get(i)
						.getLeftPoint().y);
				E.add(new CandidatePoint(M, D.get(j).getLeftPoint().y
						- C.get(i).getLeftPoint().y >= h));
			}
			while (i < m
					&& C.get(i).getRightPoint().x <= D.get(j).getRightPoint().x) {
				i++;
				boolean test = D.get(j).getLeftPoint().y
						- C.get(i).getLeftPoint().y >= h;
						
				E.add(new CandidatePoint(C.get(i - 1).getRightPoint(), test));
				E.add(new CandidatePoint(C.get(i).getLeftPoint(), test));
			}
		}

		if (p > 0 && m > 0) {
			double xLimit = Cp.get(Cp.size() - 1).x;
			i = Cp.size() - 1;

			while (i > 0 && Cp.get(i).x >= xLimit) {
				j = Dp.size() - 1;
				while (j > 0 && Dp.get(j).x >= xLimit) {
					E.add(new CandidatePoint(Cp.get(i), Dp.get(j).y
							- Cp.get(i).y >= h));
					j--;
				}
				i--;
			}
		}
		
		//aggiungo alcuni casi particolari
		for(int i1 = 0; i1<Cp.size();i1++)
		{
			int j1;
			for(j1 =0; j1<Dp.size() && Dp.get(j1).x < Cp.get(i1).x;j1++);
			while(j1<Dp.size() && Cp.get(i1).x == Dp.get(j1).x)
			{
				if(Dp.get(j1).y - Cp.get(i1).y >= h)
					E.add(new CandidatePoint(Cp.get(i1),true));
				j1++;
			}
		}
		
		return E;
	}

	public static LinkedList<Point> Bottom(SubHole s, double length) {

		// converto FB nel formato (hi,di) che sono lati verticali con hi.y >
		// di.y
		ArrayList<Point> h = new ArrayList<Point>();
		ArrayList<Point> d = new ArrayList<Point>();
		ArrayList<Point> l = new ArrayList<Point>();
		ArrayList<Point> r = new ArrayList<Point>();

		int i = 0;
		while (i < s.FB.size() - 1) {
			Edge appEdge = new Edge(s.FB.get(i), s.FB.get(i + 1));
			if (appEdge.isVertical()) {
				h.add(appEdge.getUpperPoint());
				d.add(appEdge.getLowerPoint());
			} else if (appEdge.isHorizontal()) {
				r.add(appEdge.getRightPoint());
				l.add(appEdge.getLeftPoint());
			}
			i++;
		}

		LinkedList<Point> C = new LinkedList<Point>();
		LinkedList<Edge> Q = new LinkedList<Edge>();

		if (s.FB.size() > 4) {
			Edge b = new Edge(new Point(d.get(0).x - length, d.get(0).y),
					new Point(d.get(0)));
			C.add(b.p1);

			Edge lineb = new Edge(b.p1.y, false);
			Edge h1d1 = new Edge(h.get(1), d.get(1));
			Point support = Edge.Intersection(lineb, h1d1).isPoint();

			if (support == null) {
				throw new IllegalStateException("support is null");
			}

			slide(1, length, b, C, h, d, l, r, Q, support);
		} else {
			C.add(s.FB.get(1));
			C.add(s.FB.get(2));
		}

		while (C.size() > 0 && C.get(0).x < d.get(0).x) {
			C.remove(0);
		}
		if (C.size() > 0 && C.getFirst().x > d.get(0).x) {
			Point x = new Point(d.get(0).x, C.getFirst().y);
			C.addFirst(x);
		}

		double limit;
		if (s.Q != null)
			limit = Math.min(s.Q.x, s.FB.get(s.FB.size() - 1).x - length);
		else
			limit = s.FB.get(s.FB.size() - 1).x - length;

		while (C.size() > 0 && C.getLast().x > limit) {
			C.removeLast();
		}
		if (C.size() > 0 && C.getLast().x < limit) {
			Point x = new Point(limit, C.getLast().y);
			C.addLast(x);
		}
		return C;
	}

	private static void slide(int start, double length, Edge b,
			LinkedList<Point> C, ArrayList<Point> h, ArrayList<Point> d,
			ArrayList<Point> l, ArrayList<Point> r, LinkedList<Edge> Q,
			Point support) {
		int m = h.size() - 1;
		while (start <= m) {
			int i = start;
			while (h.get(i).x < support.x + length) {
				// slide on support
				if (h.get(i).y > b.p2.y) {
					// Hit h[i]d[i]
					b.p1 = new Point(h.get(i).x - length, h.get(i).y);
					b.p2 = new Point(h.get(i));
					Point u = new Point(b.p1.x, b.p1.y
							- (h.get(i).y - support.y));
					if (C.isEmpty() || !Point.equals(C.getLast(), u))
						C.add(u);
					if (!Point.equals(b.p1, u))
						C.add(b.p1);
					Q.clear();
					if (i == m) {
						return;
					}
					support = Edge.Intersection(
							new Edge(h.get(i + 1), d.get(i + 1)),
							new Edge(b.p1.y, false)).isPoint();

					slide(i + 1, length, b, C, h, d, l, r, Q, support);
					return;
				} else if (i == m) {
					return;
				} else {
					// keep sliding
					i++;
				}
			}
			// ready to fall
			b.p1 = new Point(support);
			b.p2 = new Point(b.p1.x + length, b.p1.y);
			C.add(b.p1);
			LinkedList<Edge> Q1 = setup(start, i, l, r);
			Q = merge(Q, Q1);
			if (!Q.isEmpty()) {
				Edge a = Q.removeFirst();// TODO noSuchElement
				b.p1 = new Point(b.p1.x, a.getLeftPoint().y);
				b.p2 = new Point(b.p2.x, a.getLeftPoint().y);
				if (C.isEmpty() || !Point.equals(C.getLast(), b.p1))
					C.add(b.p1);

				support = a.getRightPoint();
				start = i;
			} else
				return;
		}
	}

	private static LinkedList<Edge> setup(int start, int end,
			ArrayList<Point> l, ArrayList<Point> r) {
		LinkedList<Edge> Q = new LinkedList<Edge>();
		int i = end - 1;
		while (i >= start) {
			if (Q.isEmpty() || Q.getFirst().getLeftPoint().y <= l.get(i).y) {
				Q.addFirst(new Edge(l.get(i), r.get(i)));
			}
			i--;
		}
		return Q;
	}

	private static LinkedList<Edge> merge(LinkedList<Edge> Q,
			LinkedList<Edge> Q1) {
		if (!Q.isEmpty() && !Q1.isEmpty()) {
			Edge s = Q1.getFirst();
			while (Q.size() > 0 && Q.getLast().p1.y <= s.p1.y)
				Q.removeLast();
		}

		while (!Q1.isEmpty())
			Q.add(Q1.removeFirst());

		return Q;
	}

	public static LinkedList<Point> Top(SubHole s, double length) {
		LinkedList<Point> D = new LinkedList<Point>();
		boolean fallingC = false;

		// converto FT nel formato (hi,di) che sono lati verticali con hi.y >
		// di.y
		ArrayList<Point> h = new ArrayList<Point>();
		ArrayList<Point> d = new ArrayList<Point>();

		int i = 0;
		while (i < s.FT.size() - 1) {
			Edge appEdge = new Edge(s.FT.get(i), s.FT.get(i + 1));
			if (appEdge.isVertical()) {
				h.add(appEdge.getUpperPoint());
				d.add(appEdge.getLowerPoint());
				if (appEdge.p1.y > appEdge.p2.y && i != s.FT.size() - 2) {
					fallingC = true;
				}
			}
			i++;
		}

		if (!fallingC) {
			for (i = 1; i < s.FT.size(); i++)
				D.add(s.FT.get(i));
		} else {
			int p = h.size() - 1;

			D.add(new Point(h.get(0).x - length, h.get(0).y));
			i = 1;
			while (i < p) {
				if (d.get(p - 1).y <= d.get(i).y
						&& d.get(p - 1).x <= d.get(i).x + length) {
					Point u = new Point(d.get(p - 1).x - length, d.get(i).y);
					Point v = new Point(d.get(p - 1).x - length, d.get(p - 1).y);
					Point z = new Point(h.get(p).x - length, h.get(p).y);
					D.add(u);
					D.add(v);
					D.add(z);
					break;
				}
				D.add(d.get(i));
				if (d.get(i).y <= d.get(p - 1).y
						&& d.get(p - 1).y <= h.get(i).y
						&& d.get(p - 1).x <= d.get(i).x + length) {
					Point u = new Point(d.get(i).x, d.get(p - 1).y);
					Point v = new Point(h.get(p).x - length, h.get(p).y);
					D.add(d.get(i));
					D.add(u);
					if (v.x > D.getLast().x)
						D.add(v);
					break;
				}
				D.add(d.get(i));
				D.add(h.get(i));
				i++;
			}
		}

		double xLimit = s.FT.get(0).x;
		while (D.size() > 0 && D.getFirst().x < xLimit) {
			D.removeFirst();
		}
		if (D.size() > 0) {
			Point p = D.getFirst();
			if (p.x > xLimit) {
				p = new Point(xLimit, p.y);
				D.addFirst(p);
			}
		}

		xLimit = s.Q == null ? s.FT.get(s.FT.size() - 1).x - length : Math.min(
				s.Q.x, s.FT.get(s.FT.size() - 1).x - length);
		while (D.size() > 0 && D.getLast().x > xLimit) {
			D.removeLast();
		}
		if (D.size() > 0) {
			Point p = D.getLast();
			if (p.x < xLimit) {
				p = new Point(xLimit, p.y);
				D.addLast(p);
			}
		}

		return D;
	}

	private static void saveErrorLog(RuntimeException e, List<Packet> packets,
			BinConfiguration bins) {

		boolean stopCondition = false;
		String pathFile = null;
		File err = null;
		int errorN = 0;
		try {
			while (!stopCondition) {
				errorN++;
				pathFile = "BLF_" + e.getClass().getSimpleName() + errorN
						+ ".log";
				err = new File(pathFile);
				stopCondition = err.createNewFile();
			}

			ConfigurationManager confManager = new ConfigurationManager();
			// prepare a dummy ProblemConf
			ProblemConfiguration problemConf = new ProblemConfiguration(bins,
					Collections.<PacketConfiguration> emptyList());

			confManager.setCoreConfiguration(new BLFTestCoreConfiguration(
					packets));
			confManager.setProblemConfiguration(problemConf);
			confManager.setCoreName("BLFTest");
			confManager.saveToFile(err);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		throw new BlfErrorException("Errore " + err.getName()
				+ " saved.. invialo ad urban...");
	}

	// metodo da utilizzare da fuori...
	public static BlfLayout getLayout(List<Packet> packets,
			BinConfiguration bins) {
		ArrayList<CoreBin> coreBins = new ArrayList<CoreBin>();

		for (int i = 0; i < packets.size(); i++) {
			int j = 0;
			boolean inserito = false;
			//if(i==43){break;}
			while (!inserito && j < coreBins.size()) {
				try {
					inserito = coreBins.get(j).insertPacket(packets.get(i));

				} catch (RuntimeException e) {
					saveErrorLog(e, packets, bins);
					e.printStackTrace();
				}
				j++;
			}
			if (!inserito) {
				coreBins.add(new CoreBin(bins.getWidth(), bins.getHeight()));
				try {
					if (!coreBins.get(j).insertPacket(packets.get(i)))
						return null;
				} catch (RuntimeException e) {
					saveErrorLog(e, packets, bins);
					e.printStackTrace();
				}
			}
		}

		ArrayList<Bin> resultBins = new ArrayList<Bin>();
		double fitness = 0;
		for (int i = 0; i < coreBins.size(); i++) {
			Bin appBin = new Bin(bins, i);
			for (int j = 0; j < coreBins.get(i).packets.size(); j++) {
				appBin.addPacket(coreBins.get(i).packets.get(j));
			}
			fitness += coreBins.get(i).getFitness();
			resultBins.add(appBin);
		}

		fitness = fitness / coreBins.size();
		fitness = fitness + 100 * coreBins.size();

		return new BlfLayout(resultBins, (int) fitness);
	}

}
