package BLFCore;

import java.util.ArrayList;
import java.util.LinkedList;

public class PackingProcedures {
	
	public static ArrayList<CandidatePoint> Placing(double h,
			LinkedList<Point> Cp, LinkedList<Point> Dp) {
		int i = 0;
		int j = 0;
		LinkedList<Edge> C = new LinkedList<Edge>();
		LinkedList<Edge> D = new LinkedList<Edge>();

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
		int m = C.size();
		int p = D.size();

		ArrayList<CandidatePoint> E = new ArrayList<CandidatePoint>();
		E.add(new CandidatePoint(C.get(1).getLeftPoint(), D.get(1)
				.getLeftPoint().y - C.get(1).getLeftPoint().y >= h));

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

		Edge b = new Edge(new Point(d.get(0).x - length, d.get(0).y),
				new Point(d.get(0)));
		LinkedList<Point> C = new LinkedList<Point>();
		LinkedList<Edge> Q = new LinkedList<Edge>();
		C.add(b.p1);
		Point support = new Edge(Edge.Intersection(
				new Edge(h.get(1), d.get(1)), new Edge(b.p1.x, false)))
				.isPoint();

		if (support == null) {
			throw new IllegalStateException("support is null");
		}

		slide(1, length, b, C, h, d, l, r, Q, support);

		i = 0;

		while (i < C.size() && C.get(0).x < d.get(0).x)
			C.remove(0);

		i = C.size();
		while (i > 0 && C.get(i).x > s.Q.x) {
			C.remove(i);
			i--;
		}

		return C;
	}

	private static void slide(int start, double length, Edge b,
			LinkedList<Point> C, ArrayList<Point> h, ArrayList<Point> d,
			ArrayList<Point> l, ArrayList<Point> r, LinkedList<Edge> Q,
			Point support) {
		int m = h.size() - 1;
		while (start < m) {
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
					support = Edge.Intersection(
							new Edge(h.get(i + 1), d.get(i + 1)),
							new Edge(b.p1.x, false)).isPoint();

					slide(i + 1, length, b, C, h, d, l, r, Q, support);
					return;
				} else if (i == m) {
					return;
				} else {
					i++;
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
	}

	private static LinkedList<Edge> setup(int start, int end,
			ArrayList<Point> l, ArrayList<Point> r) {
		LinkedList<Edge> Q = new LinkedList<Edge>();
		int i = end - 1;
		while (i >= start) {
			if (Q.isEmpty() || Q.getFirst().p1.y <= l.get(i).y)
				Q.addFirst(new Edge(l.get(i), r.get(i)));
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
				if(appEdge.p1.y > appEdge.p2.y)
					fallingC = true;
			}
			i++;
		}
		
		if (!fallingC) {
			for (i = 0; i < s.FT.size(); i++)
				D.add(s.FT.get(i));
			return D;
		}
		
		int p = h.size() - 1;
		
		D.add(new Point(h.get(0).x-length,h.get(0).y));
		i = 1;
		while(i < p)
		{
			if(d.get(p-1).y <= d.get(i).y && d.get(p-1).x <= d.get(i).x + length)
			{
				Point u = new Point(d.get(p-1).x - length,d.get(i).y);
				Point v = new Point(d.get(p-1).x - length,d.get(p-1).y);
				Point z = new Point(h.get(p).x - length,h.get(p).y);
				D.add(u); D.add(v); D.add(z);
				return D;
			}
			D.add(d.get(i));
			if(d.get(i).y <= d.get(p-1).y && d.get(p-1).y <= h.get(i).y &&
					d.get(p-1).x <= d.get(i).x + length)
			{
				Point u = new Point(d.get(i).x,d.get(p-1).y);
				Point v = new Point(h.get(p).x - length,h.get(p).y);
				D.add(d.get(i)); D.add(u); D.add(v);
				return D;
			}
			D.add(d.get(i));
			D.add(h.get(i));
			i++;
		}

		return D;
	}

}
