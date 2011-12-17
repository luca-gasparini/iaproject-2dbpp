package BLFCore;

import java.util.ArrayList;
import java.util.LinkedList;
import logic.*;
import java.util.List;

public class PackingProcedures {

	public static ArrayList<CandidatePoint> Placing(double h,
			LinkedList<Point> Cp, LinkedList<Point> Dp) {
		int i = 0;
		int j = 0;
		LinkedList<Edge> C = new LinkedList<Edge>();
		LinkedList<Edge> D = new LinkedList<Edge>();
		C.add(null);
		D.add(null); // gli indici nel paper partono da 1
		// converto in liste di lati orizzontali
		for (i = 0; i < Cp.size() - 1; i++) {
			Edge app = new Edge(Cp.get(i), Cp.get(i + 1));
			if (app.isHorizontal())
				C.add(app);
		}

		for (i = 0; i < Dp.size() - 1; i++) {
			Edge app = new Edge(Dp.get(i), Dp.get(i + 1));
			if (app.isHorizontal())
				D.add(app);
		}

		i = 1;
		j = 1;
		int m = C.size() - 1;
		int p = D.size() - 1;

		ArrayList<CandidatePoint> E = new ArrayList<CandidatePoint>();
		E.add(new CandidatePoint(Cp.get(0),Dp.get(0).y - Cp.get(0).y >= h));

		while (i < m || j < p) {
			while (D.get(j).getRightPoint().x <= C.get(i).getRightPoint().x
					&& j < p) {
				j++;
				Point M = new Point(D.get(j).getLeftPoint().x, C.get(i)
						.getLeftPoint().y);
				E.add(new CandidatePoint(M, D.get(j).getLeftPoint().y
						- C.get(i).getLeftPoint().y > h));
			}
			while (C.get(i).getRightPoint().x <= D.get(j).getRightPoint().x
					&& i < m) {
				i++;
				boolean test = D.get(j).getLeftPoint().y
						- C.get(i).getLeftPoint().y >= h;
				E.add(new CandidatePoint(C.get(i - 1).getRightPoint(), test));
				E.add(new CandidatePoint(C.get(i).getLeftPoint(), test));
			}
		}

		if(p>0 && m>0)
		E.add(new CandidatePoint(C.get(m).getRightPoint(), D.get(p)
				.getRightPoint().y - C.get(m).getRightPoint().y >= h));

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
			Point x = C.remove(0);
			if (C.size() == 0 || C.getFirst().x > d.get(0).x) {
				x = new Point(d.get(0).x, x.y);
				C.addFirst(x);
			}
		}

		if (s.Q != null)
			while (C.size() > 0 && C.getLast().x > s.Q.x) {
				Point x = C.removeLast();
				if (C.size() <= 1 || C.getLast().x >= s.Q.x) {
					x = new Point(s.Q.x - 1, x.y);
					C.addLast(x);
				}
			}
		else {
			double limit = s.FB.get(s.FB.size() - 1).x - length;
			while (C.size() > 0 && C.getLast().x > limit) {
				Point x = C.removeLast();
				if (C.size() <= 1 || C.getLast().x < s.Q.x) {
					x = new Point(limit, x.y);
					C.addLast(x);
				}
			}
		}

		return C;
	}

	private static void slide(int start, double length, Edge b,
			LinkedList<Point> C, ArrayList<Point> h, ArrayList<Point> d,
			ArrayList<Point> l, ArrayList<Point> r, LinkedList<Edge> Q,
			Point support) {
		int m = h.size() - 1;
		while (start <= m) {// TODO controlla
			int i = start;
			while (h.get(i).x <= support.x + length) {
				// slide on support
				if (h.get(i).y > b.p2.y) {
					// Hit h[i]d[i]
					b.p1 = new Point(h.get(i).x - length, h.get(i).y);
					b.p2 = new Point(h.get(i));
					Point u = new Point(b.p1.x, b.p1.y
							- (h.get(i).y - support.y));
					C.add(u);
					C.add(b.p1);
					Q.clear();
					if(i==m) return;
					support = Edge.Intersection(
							new Edge(h.get(i + 1), d.get(i + 1)),
							new Edge(b.p1.x, false)).isPoint();

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
			Edge a = Q.removeFirst();
			b.p1 = new Point(b.p1.x, a.getLeftPoint().y);
			b.p2 = new Point(b.p2.x, a.getLeftPoint().y);
			C.add(b.p1);
			start = i;
			support = a.getRightPoint();
		}
	}

	private static LinkedList<Edge> setup(int start, int end,
			ArrayList<Point> l, ArrayList<Point> r) {
		LinkedList<Edge> Q = new LinkedList<Edge>();
		int i = end - 1;
		while (i >= start) {
			if (Q.isEmpty() || Q.getFirst().p1.y <= l.get(i).y) {
				Q.addFirst(new Edge(l.get(i), r.get(i)));
				i--;
			}
		}
		return Q;
	}

	private static LinkedList<Edge> merge(LinkedList<Edge> Q,
			LinkedList<Edge> Q1) {
		if (!Q.isEmpty() && !Q1.isEmpty()) {
			Edge s = Q1.getFirst();
			while (Q.getLast().p1.y >= s.p1.y)
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
					return D;
				}
				D.add(d.get(i));
				if (d.get(i).y <= d.get(p - 1).y
						&& d.get(p - 1).y <= h.get(i).y
						&& d.get(p - 1).x <= d.get(i).x + length) {
					Point u = new Point(d.get(i).x, d.get(p - 1).y);
					Point v = new Point(h.get(p).x - length, h.get(p).y);
					D.add(d.get(i));
					D.add(u);
					D.add(v);
					return D;
				}
				D.add(d.get(i));
				D.add(h.get(i));
				i++;
			}
		}

		while (D.size() > 1 && D.getFirst().x < 0) {
			D.removeFirst();
			Point p = D.getFirst();
			if (p.x > 0) {
				p = new Point(0, p.y);
				D.addFirst(p);
			}
		}

		double xLimit = s.FT.getLast().x - length;
		while (D.size() > 1 && D.getLast().x > xLimit) {
			D.removeLast();
			Point p = D.getLast();
			if (p.x < xLimit) {
				p = new Point(xLimit, p.y);
				D.addLast(p);
			}
		}

		return D;
	}

	public static BlfLayout getLayout(List<Packet> packets,
			BinConfiguration bins) {
		ArrayList<CoreBin> coreBins = new ArrayList<CoreBin>();

		for (int i = 0; i < packets.size(); i++) {
			int j = 0;
			boolean inserito = false;
			while (!inserito && j < coreBins.size()) {
				inserito = coreBins.get(j).insertPacket(packets.get(i));
				j++;
			}
			if (!inserito) {
				coreBins.add(new CoreBin(bins.getWidth(), bins.getHeight()));
				if (!coreBins.get(j).insertPacket(packets.get(i)))
					return null;
			}
		}

		ArrayList<Bin> resultBins = new ArrayList<Bin>();
		for (int i = 0; i < coreBins.size(); i++) {
			Bin appBin = new Bin(i, bins.getWidth(), bins.getHeight());
			for (int j = 0; j < coreBins.get(i).packets.size(); j++) {
				appBin.addPacket(coreBins.get(i).packets.get(j));
			}
			resultBins.add(appBin);
		}

		return new BlfLayout(resultBins, 0);
	}

}
