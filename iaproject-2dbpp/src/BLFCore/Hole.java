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
						Point stoppingPoint = lIndex == 0 ? getEdge(lowerEdge).getLeftPoint() : Qi.get(lIndex);
						int j = edgeIndex;
						while(!Point.equals(getEdge(j).p1,stoppingPoint))
						{
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
}
