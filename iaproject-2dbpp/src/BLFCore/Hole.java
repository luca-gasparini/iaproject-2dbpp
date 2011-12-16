package BLFCore;

import java.util.LinkedList;
import java.util.ArrayList;

public class Hole {

	ArrayList<Edge> edges;
	LinkedList<SubHole> subHoles;
	ArrayList<Point> Qi;

	Hole(ArrayList<Edge> e) {
		edges = e;
		Qi = new ArrayList<Point>();
		subHoles = new LinkedList<SubHole>();
		divideSubHoles();
	}

	// add rect to the hole and return a list of the new Holes created from this
	public ArrayList<Hole> updateHoles(CoreRectangle rect) {
		ArrayList<Hole> holes = new ArrayList<Hole>();

		boolean[] flags = new boolean[edges.size()];
		for (int i = 0; i < flags.length; i++)
			flags[i] = false;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < edges.size(); j++) {
				Edge intersection = Edge.Intersection(rect.getEdge(i),
						getEdge(j));

				// se è un punto, al precedente o prossimo edge trovo un
				// segmento
				if (intersection != null && intersection.isPoint() == null) {
					if (Edge.equals(intersection, getEdge(j))) {
						if (!flags[j]) {
							Hole hole = this.traverseFromHoleLine(j, rect,
									flags, true);
							if (hole != null)
								holes.add(hole);
							hole = this.traverseFromHoleLine(j, rect, flags,
									false);
							if (hole != null)
								holes.add(hole);
						}

					} else if (Edge.equals(intersection, rect.getEdge(i))) {
						/*
						 * assumo che essendo entrambi attraversate in
						 * clockwise: p1 vicino a p1 e p2 vicino a p2
						 */
						// incremental
						ArrayList<Edge> li = new ArrayList<Edge>();
						if (!Point.equals(rect.getEdge(i).p2, getEdge(j).p2))
							li.add(new Edge(rect.getEdge(i).p2, getEdge(j).p2));
						if (this.traverse(li, rect.getEdge(i).p2, j, rect,
								flags, true)) {
							holes.add(new Hole(li));
						}

						// not incremental
						li = new ArrayList<Edge>();
						if (!Point.equals(getEdge(j).p1, rect.getEdge(i).p1))
							li.add(new Edge(getEdge(j).p1, rect.getEdge(i).p1));
						if (this.traverse(li, rect.getEdge(i).p1, j, rect,
								flags, false)) {
							holes.add(new Hole(li));
						}

					} else {
						ArrayList<Edge> li = new ArrayList<Edge>();

						// trovo il punto dell'hole che delimita l'intersezione
						// devo continuare dal lato "successivo"
						if (Edge.Intersection(intersection, getEdge(j).p1) != null) {
							li.add(getEdge(j - 1));
							if (this.traverse(li, getEdge(j).p1, j - 1, rect,
									flags, false)) {
								holes.add(new Hole(li));
							}

							// trovo punto del lato del rect che delimita
							// intersezione
							Point stopPoint;
							if (Edge.Intersection(intersection,
									rect.getEdge(i).p1) != null)
								stopPoint = rect.getEdge(i).p1;
							else
								stopPoint = rect.getEdge(i).p2;

							li = new ArrayList<Edge>();
							li.add(new Edge(stopPoint, getEdge(j).p2));
							if (this.traverse(li, stopPoint, j, rect, flags,
									true)) {
								holes.add(new Hole(li));
							}
						} else {
							li.add(getEdge(j + 1));
							if (this.traverse(li, getEdge(j).p2, j + 1, rect,
									flags, true))
								holes.add(new Hole(li));

							Point stopPoint;
							if (Edge.Intersection(intersection,
									rect.getEdge(i).p1) != null)
								stopPoint = rect.getEdge(i).p1;
							else
								stopPoint = rect.getEdge(i).p2;

							li = new ArrayList<Edge>();
							li.add(new Edge(stopPoint, getEdge(j).p1));
							if (this.traverse(li, stopPoint, j, rect, flags,
									false)) {
								holes.add(new Hole(li));
							}

						}
					}
				}
			}
		}
		return holes;
	}

	// traverse a partire da un lato dell'hole fino a trovare stopping
	// point(passsando per rect)..
	// ritorna false se scopre che questo hole è già stato inserito (incontra un
	// edge flaggato)
	private boolean traverse(ArrayList<Edge> newHoleEdge, Point stopPoint,
			int j, CoreRectangle rect, boolean[] flags, boolean incremental) {

		/*
		 * attraverso sui lati dell'hole e mi fermo quando interseco il rect se
		 * interseco per un segmento ho 2 possibili strade da seguire
		 */
		int c = j;
		boolean firstIteration = true;// alla prima iterazione non devo trovare
										// intersezioni
		boolean stopCondition = false;
		while (!stopCondition) {
			if (incremental)
				c++;
			else
				c--;
			int intersectEdge = -1;
			int intersectPoint = -1;
			for (int i = 0; i < 4; i++) {
				Edge inter = Edge.Intersection(getEdge(c), rect.getEdge(i));
				if (inter != null && inter.isPoint() == null)
					intersectEdge = i;

				if (inter != null && inter.isPoint() != null)
					intersectPoint = i;
			}

			if (intersectEdge == -1 && intersectPoint == -1) {
				if (flags[c])
					return false;
				firstIteration = false;
				newHoleEdge.add(getEdge(c));
				flags[c] = true;
			} else {
				if (firstIteration)
					return false;
				stopCondition = true;
				boolean verso = false;
				int rectIndex = 0;
				// trovata intersezione...traverse su rect
				// prima di tutto scelgo il lato del rect da dove partire
				if (intersectEdge != -1) {
					rectIndex = intersectEdge;
					Point firstPoint = incremental ? getEdge(c).p1
							: getEdge(c).p2;
					if (new Edge(firstPoint, rect.getEdge(intersectEdge).p1)
							.length() > new Edge(firstPoint,
							rect.getEdge(intersectEdge).p2).length()) {
						verso = true;
						newHoleEdge.add(new Edge(firstPoint, rect
								.getEdge(intersectEdge).p2));
					} else {
						verso = false;
						newHoleEdge.add(new Edge(firstPoint, rect
								.getEdge(intersectEdge).p1));
					}
				} else {
					rectIndex = intersectPoint;
					Point startPoint = Edge.Intersection(getEdge(c),
							rect.getEdge(intersectPoint)).isPoint();
					newHoleEdge.add(getEdge(c));
					Edge e1 = new Edge(startPoint,
							rect.getEdge(intersectPoint).p1);
					Edge e2 = new Edge(startPoint,
							rect.getEdge(intersectPoint).p2);
					if (Edge.Intersection(e1, getEdge(incremental ? c + 1
							: c - 1)) == null) {
						newHoleEdge.add(e1);
						verso = false;
					} else {
						newHoleEdge.add(e2);
						verso = true;
					}
				}
				if (verso == true)
					rectIndex++;
				else
					rectIndex--;
				while (Edge.Intersection(rect.getEdge(rectIndex), stopPoint) == null) {
					newHoleEdge.add(rect.getEdge(rectIndex));
					if (verso == true)
						rectIndex++;
					else
						rectIndex--;
				}
				if (verso) {
					newHoleEdge.add(new Edge(rect.getEdge(rectIndex).p1,
							stopPoint));
				} else {
					newHoleEdge.add(new Edge(rect.getEdge(rectIndex).p2,
							stopPoint));
				}
			}
		}
		return true;
	}

	private Hole traverseFromHoleLine(int j, CoreRectangle rect,
			boolean[] flags, boolean incremental) {
		// se il lato successivo interseca con il rect per più di un punto mi
		// fermo
		// se interseca per un punto (sempre) sarà li che mi devo fermare
		int c = incremental ? j + 1 : j - 1;
		for (int i = 0; i < 4; i++) {

			Edge inter = Edge.Intersection(getEdge(c), rect.getEdge(i));
			if (inter != null && inter.isPoint() == null)
				return null;
		}

		ArrayList<Edge> newHoleEdge = new ArrayList<Edge>();
		Point stopPoint = incremental ? getEdge(j).p2 : getEdge(j).p1;
		newHoleEdge.add(getEdge(c));
		if (this.traverse(newHoleEdge, stopPoint, c, rect, flags, incremental))
			return new Hole(newHoleEdge);
		else
			return null;
	}

	public void setEdges(ArrayList<Edge> e) {
		edges = e;
		divideSubHoles();
	}

	private Edge getEdge(int index) {
		return edges.get((index + edges.size()) % edges.size());
	}

	public void divideSubHoles() {
		Qi.clear();
		subHoles.clear();

		int numberOfEdges = edges.size();
		int upperEdge = -1, lowerEdge = -1;
		int i = 0;

		// check for edges to be linked
		if (!Point.equals(getEdge(0).p2, getEdge(1).p1))
			if (Point.equals(getEdge(0).p1, getEdge(1).p1)
					|| Point.equals(getEdge(0).p1, getEdge(1).p2)) {
				getEdge(0).swapPoints();
			}

		for (i = 0; i < numberOfEdges; i++) {
			if (!(Point.equals(getEdge(i).p2, getEdge(i + 1).p1))) {
				if (Point.equals(getEdge(i).p2, getEdge(i + 1).p2)) {
					getEdge(i + 1).swapPoints();
				} else
					throw new IllegalArgumentException("edges are not linked");
			}
		}

		// finding upper and lower edges
		for (i = 0; i < numberOfEdges; i++) {
			if (edges.get(i).isHorizontal()
					&& (upperEdge == -1 || edges.get(i).p1.y > edges
							.get(upperEdge).p1.y))
				upperEdge = i;
			if (edges.get(i).isHorizontal()
					&& (upperEdge == -1 || edges.get(i).p1.y < edges
							.get(lowerEdge).p1.y))
				lowerEdge = i;
		}

		// is the list in clockWise order?
		boolean clockWise;
		if (Point.equals(edges.get(upperEdge).p1, edges.get(upperEdge)
				.getLeftPoint()))
			clockWise = true;
		else
			clockWise = false;

		if (!clockWise) {
			// we have to revert the list
			ArrayList<Edge> app = new ArrayList<Edge>();
			i = numberOfEdges;
			while (i > 0) {
				app.add(new Edge(getEdge(i).p2, getEdge(i).p1));
				i--;
			}
			upperEdge = (numberOfEdges - upperEdge) % numberOfEdges;
			lowerEdge = (numberOfEdges - lowerEdge) % numberOfEdges;
			edges = app;
		}

		ArrayList<Integer> leftMostEdges = new ArrayList<Integer>();
		// find leftMostEdges, Qi and relative Qw
		// scan the left side of the hole starting from down
		Qi.add(null);// Qi starts from Q1. every Qi is related to his upper
						// LeftMostEdges
		ArrayList<Point> Qw = new ArrayList<Point>();
		ArrayList<Edge> edgeOfQw = new ArrayList<Edge>();

		Qw.add(null);
		edgeOfQw.add(null);

		i = lowerEdge + 1;
		while (i < upperEdge) {
			Edge currentEdge = edges.get(i);
			Edge previousEdge = edges.get(i - 1);
			Edge nextEdge = edges.get(i + 1);

			if (currentEdge.isVertical()) {
				if (Point.equals(currentEdge.getLowerPoint(),
						previousEdge.getLeftPoint())
						&& Point.equals(currentEdge.getUpperPoint(),
								nextEdge.getLeftPoint())) {
					// LEFTMOST
					leftMostEdges.add(i);
				}
				if (Point.equals(currentEdge.getLowerPoint(),
						previousEdge.getRightPoint())
						&& Point.equals(currentEdge.getUpperPoint(),
								nextEdge.getRightPoint())) {
					// Qi
					Qi.add(currentEdge.getUpperPoint());

					// search for Qw;
					Edge rightHalfLine = Edge.rightHalfLine(currentEdge
							.getUpperPoint());
					int j = i - 1;
					boolean foundQw = false;
					while (!foundQw) {
						Edge candidateQw = Edge.Intersection(getEdge(j),
								rightHalfLine);
						if (candidateQw != null
								&& candidateQw.isPoint() != null) {
							Qw.add(candidateQw.isPoint());
							edgeOfQw.add(getEdge(j));
							foundQw = true;
						}
						j--;
					}
				}
			}

			i = (i + 1) % edges.size();
		}

		// Ok.. find subHole's FT & FB
		subHoles.clear();
		for (int lIndex = 0; lIndex < leftMostEdges.size(); lIndex++) {
			int edgeIndex = leftMostEdges.get(lIndex);
			i = edgeIndex + 1;

			subHoles.add(new SubHole());
			SubHole sb = subHoles.getLast();
			sb.FT.add(getEdge(edgeIndex).getLowerPoint());
			sb.FT.add(getEdge(edgeIndex).getUpperPoint());

			boolean foundQn;
			boolean stop = false; // true quando trovo qW o lowerEdge
			while (!stop) {
				foundQn = false;
				for (int qIndex = lIndex + 1; !foundQn || qIndex < Qi.size(); qIndex++) {
					// search for a Qi & "jump over it".
					if (Edge.Intersection(getEdge(i), Qi.get(qIndex)) != null) {
						foundQn = true;
						sb.Q = Qi.get(qIndex);
						Point a = getEdge(i).getUpperPoint();
						i = (i + 2) % edges.size();

						Edge Eb = Edge.Intersection(Edge.upHalfLine(a),
								getEdge(i));
						while (Eb == null || Eb.isPoint() == null) {
							i = (i + 1) % edges.size();
							Eb = Edge.Intersection(Edge.upHalfLine(a),
									getEdge(i));
						}
						Point b = Eb.isPoint();
						sb.FT.add(b);
						sb.FT.add(getEdge(i).getRightPoint());
						i = (i + 1) % edges.size();
					}
				}

				if (!foundQn) {// search for Qwi or lower edge
					boolean foundQw = false;

					Edge appEdge;
					Point appQw = null;

					if (lIndex == 0)// search lowerEdge
					{
						appEdge = Edge.Intersection(getEdge(i),
								getEdge(lowerEdge));
						if (appEdge != null) {
							appQw = appEdge.isPoint();
							if (appQw != null) {
								foundQw = true;
							}
						}
					}

					else// search for qW
					{
						appQw = Edge.Intersection(getEdge(i), Qw.get(lIndex));
						if (appQw != null) {
							foundQw = true;
						}
					}

					if (foundQw) {
						sb.FT.add(appQw);
						Point stoppingPoint = lIndex == 0 ? getEdge(lowerEdge)
								.getLeftPoint() : Qi.get(lIndex);
						int j = edgeIndex;
						while (!Point.equals(getEdge(j).p1, stoppingPoint)) {
							sb.FB.add(getEdge(j).p1);
							j--;
						}
						sb.FB.add(getEdge(j).p1);
						sb.FB.add(Qw.get(lIndex));
						sb.FB.add(edgeOfQw.get(lIndex).getUpperPoint());

						stop = true;
					} else {
						sb.FT.add(getEdge(i).p2);
						i = (i + 1) % edges.size();
					}
				}
			}
		}
	}

	public ArrayList<Point> getCandidates(CoreRectangle rect)
	{
		ArrayList<Point> li = new ArrayList<Point>();
		for(int i = 0;i < subHoles.size();i++)
		{
			LinkedList<Point> Top,Bottom;
			Top = PackingProcedures.Top(subHoles.get(i), rect.width);
			Bottom = PackingProcedures.Bottom(subHoles.get(i), rect.width);
			ArrayList<CandidatePoint> app = PackingProcedures.Placing(rect.heigth, Bottom, Top);
			for(int j =0; j<app.size();j++)
			{
				CandidatePoint x = app.get(j);
				if(x.feasible == true)
					li.add(x.p);
			}
		}
		return li;
	}
	
}
