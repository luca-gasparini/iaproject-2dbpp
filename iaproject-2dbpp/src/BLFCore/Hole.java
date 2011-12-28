package BLFCore;

import java.util.LinkedList;
import java.util.ArrayList;
import BLFCore.*;

public class Hole {

	ArrayList<Edge> edges;
	ArrayList<SubHole> subHoles;
	ArrayList<Point> Qi;

	// data una lista di Edge, crea l'hole contenuto in questi lati
	Hole(ArrayList<Edge> e) {
		edges = e;
		Qi = new ArrayList<Point>();
		subHoles = new ArrayList<SubHole>();
		divideSubHoles();
	}

	// add rectangle to the hole and return a list of the new Holes created from
	// this
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

					// intersezione uguale a lato hole
					if (Edge.equals(intersection, getEdge(j))) {
						Hole hole = this.traverseFromHoleLine(j, rect, flags,
								true);
						if (hole != null)
							holes.add(hole);
						hole = this.traverseFromHoleLine(j, rect, flags, false);
						if (hole != null)
							holes.add(hole);

						// intersezione uguale a lato rect
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

						//intersezione che non coincide con nessuno dei due lati
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
							{
								stopPoint = rect.getEdge(i).p1;
							}
							else{
								stopPoint = rect.getEdge(i).p2;
							}

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
		int c = (j) % flags.length;
		boolean firstIteration = true;// alla prima iterazione non devo trovare
		// intersezioni
		boolean stopCondition = false;
		while (!stopCondition) {
			if (incremental)
				c = (c + 1) % flags.length;
			else
				c = (c - 1 + flags.length) % flags.length;
			int intersectEdge = -1;
			int intersectPoint = -1;

			// controllo se interseco il rettangolo
			for (int i = 0; i < 4; i++) {
				Edge inter = Edge.Intersection(getEdge(c), rect.getEdge(i));
				if (inter != null && inter.isPoint() == null)
					intersectEdge = i;

				if (inter != null && inter.isPoint() != null) {
					Point x = inter.isPoint();
					if (!(Point.equals(x, rect.getEdge(i).p1) || Point.equals(
							x, rect.getEdge(i).p2)))
						intersectPoint = i;
				}
			}

			// se non trovo intersezioni aggiungo lato e proseguo
			if (intersectEdge == -1 && (intersectPoint == -1 || firstIteration)) {
				if (flags[c])
					return false;
				firstIteration = false;
				newHoleEdge.add(getEdge(c));
				flags[c] = true;

			} else {

				if (firstIteration && intersectEdge != -1)
					return false;

				stopCondition = true;
				boolean verso = false;
				int rectIndex = 0;

				// trovata intersezione...traverse su rettangolo
				// prima di tutto scelgo il lato del rettangolo da dove partire

				// se l'intersezione è un lato avro altri due lati che
				// intersecano con un punto
				if (intersectEdge != -1) {
					rectIndex = intersectEdge;
					Point firstPoint = incremental ? getEdge(c).p1
							: getEdge(c).p2;

					// prendo il lato più vicino tra questi due e scelgo di
					// conseguenza il verso
					// del traverse sul rettangolo
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
					// intersezione punto
					rectIndex = intersectPoint;
					Point startPoint = Edge.Intersection(getEdge(c),
							rect.getEdge(intersectPoint)).isPoint();
					newHoleEdge.add(getEdge(c));
					flags[c] = true;
					Edge e1 = new Edge(startPoint,
							rect.getEdge(intersectPoint).p1);
					Edge e2 = new Edge(startPoint,
							rect.getEdge(intersectPoint).p2);

					// Potrei trovare subito stopping point e chiudere l'hole
					if (Edge.Intersection(e1, stopPoint) != null
							|| Edge.Intersection(e2, stopPoint) != null) {
						newHoleEdge.add(new Edge(startPoint, stopPoint));
						return true;
					}

					// altrimenti continuo il traverse sul rect nel verso dove
					// non trovo intersezioni
					/*
					 * if (Edge.Intersection(e1, getEdge(incremental ? c + 1 : c
					 * - 1)).isPoint() != null) {
					 */
					Edge intEdge = (Edge.Intersection(e1,
							getEdge(incremental ? c + 1 : c - 1)));
					if (intEdge == null || intEdge.isPoint() != null) {

						newHoleEdge.add(e1);
						verso = false;
					} else {

						newHoleEdge.add(e2);
						verso = true;
					}
				}

				// ora proseguo finche non trovo intersezione con lo stop point
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

				// chiudo l'hole sullo stopping point
				if (verso) {
					if (!Point.equals(rect.getEdge(rectIndex).p1, stopPoint))
						newHoleEdge.add(new Edge(rect.getEdge(rectIndex).p1,
								stopPoint));
				} else {
					if (!Point.equals(rect.getEdge(rectIndex).p2, stopPoint))
						newHoleEdge.add(new Edge(rect.getEdge(rectIndex).p2,
								stopPoint));
				}
			}
		}
		return true;
	}

	// traverse nel caso in cui trovo un intersezione che coincide con un lato
	// dell'hole
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

		// trovo il punto dove devo tornare dopo il traverse
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
		while (index < 0)
			index += edges.size();
		return edges.get(index % edges.size());
	}

	// Prende un hole e calcola tutti i subHole in esso contenuti
	public void divideSubHoles() {
		Qi.clear();
		subHoles.clear();

		// elimino lati che sono un punto.. potrebbe succedere durante
		// updateHoles..
		ArrayList<Edge> app = new ArrayList<Edge>();
		for (int i = 0; i < edges.size(); i++) {
			if (getEdge(i).isPoint() == null)
				app.add(new Edge(getEdge(i)));
		}

		edges = app;
		int upperEdge = -1, lowerEdge = -1, rightMost = -1;
		int i = 0;

		// check for edges to be linked
		if (!Point.equals(getEdge(0).p2, getEdge(1).p1)) {
			if (Point.equals(getEdge(0).p2, getEdge(1).p2))
				getEdge(1).swapPoints();
			else if (Point.equals(getEdge(0).p1, getEdge(1).p1)) {
				getEdge(0).swapPoints();
			} else if (Point.equals(getEdge(0).p1, getEdge(1).p2)) {
				getEdge(0).swapPoints();
				getEdge(1).swapPoints();
			} else {
				throw new IllegalArgumentException("edges are not linked");
			}
		}

		for (i = 0; i < edges.size(); i++) {
			if (!(Point.equals(getEdge(i).p2, getEdge(i + 1).p1))) {
				if (Point.equals(getEdge(i).p2, getEdge(i + 1).p2)) {
					getEdge(i + 1).swapPoints();
				} else
					throw new IllegalArgumentException("edges are not linked");
			}
		}

		// se ho due lati consecutivi entrambi verticali o entrambi orizzontali
		// li unisco in un solo lato.. sembrerebbe inutile ma poi mi risolve un
		// po
		// di problemi..
		for (i = -1; i < edges.size(); i++) {
			if ((getEdge(i).isVertical() && getEdge(i + 1).isVertical())
					|| (getEdge(i).isHorizontal() && getEdge(i + 1)
							.isHorizontal())) {
				getEdge(i).p2 = new Point(getEdge(i + 1).p2);
				edges.remove((i + 1 + edges.size()) % edges.size());
			}
		}

		int leftMostEdge = -1;

		// finding upper, lower and rightmost edges
		for (i = 0; i < edges.size(); i++) {
			if (getEdge(i).isHorizontal()
					&& (upperEdge == -1 || getEdge(i).p1.y > getEdge(upperEdge).p1.y))
				upperEdge = i;
			if (getEdge(i).isHorizontal()
					&& (lowerEdge == -1 || getEdge(i).p1.y < getEdge(lowerEdge).p1.y))
				lowerEdge = i;
			if (getEdge(i).isVertical()
					&& (rightMost == -1 || getEdge(i).p1.x > getEdge(rightMost).p1.x))
				rightMost = i;
			if (getEdge(i).isVertical()
					&& (leftMostEdge == -1 || getEdge(i).p1.x < getEdge(leftMostEdge).p1.x))
				leftMostEdge = i;
		}

		// is the list in clockWise order?
		boolean clockWise;
		if (Point.equals(getEdge(upperEdge).p1, getEdge(upperEdge)
				.getLeftPoint()))
			clockWise = true;
		else
			clockWise = false;

		if (!clockWise) {
			// we have to revert the list
			app = new ArrayList<Edge>();
			i = edges.size() - 1;
			boolean setUpper = false;
			boolean setLower = false;
			boolean setRight = false;
			boolean setLeft = false;
			while (i >= 0) {
				app.add(new Edge(getEdge(i).p2, getEdge(i).p1));
				if (!setUpper && i == upperEdge) {
					upperEdge = app.size() - 1;
					setUpper = true;
				}
				if (!setLower && i == lowerEdge) {
					lowerEdge = app.size() - 1;
					setLower = true;
				}
				if (!setRight && i == rightMost) {
					rightMost = app.size() - 1;
					setRight = true;
				}
				if (!setLeft && i == leftMostEdge) {
					leftMostEdge = app.size() - 1;
					setLeft = true;
				}
				i--;
			}
			edges = app;
		}

		/*
		 * for(i = leftMostEdge;i != rightMost; i = (i+1)%edges.size()) {
		 * if(getEdge(i).p1.x > getEdge(i).p2.x) { throw new
		 * IllegalArgumentException("coming back edge"); } }
		 */

		ArrayList<Integer> leftMostEdges = new ArrayList<Integer>();
		// find leftMostEdges, Qi and relative Qw
		// scan the left side of the hole starting from down
		Qi.add(null);
		// Qi starts from Q1. every Qi is related to his upper
		// LeftMostEdges
		ArrayList<Point> Qw = new ArrayList<Point>();
		ArrayList<Edge> edgeOfQw = new ArrayList<Edge>();

		Qw.add(null);
		edgeOfQw.add(null);

		i = (lowerEdge + 1 + edges.size()) % edges.size();
		while (i != upperEdge) {
			Edge currentEdge = getEdge(i);
			Edge previousEdge = getEdge(i - 1);
			Edge nextEdge = getEdge(i + 1);

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
		// per ogni leftMostEdge ho un subHole
		for (int lIndex = 0; lIndex < leftMostEdges.size(); lIndex++) {
			int edgeIndex = leftMostEdges.get(lIndex);
			i = edgeIndex + 1;

			subHoles.add(new SubHole());
			SubHole sb = subHoles.get(subHoles.size() - 1);
			sb.FT.add(getEdge(edgeIndex).getLowerPoint());
			sb.FT.add(getEdge(edgeIndex).getUpperPoint());
			sb.Q = Qi.get(lIndex);

			boolean foundQn;
			boolean stop = false; // true quando trovo qW o rightMost
			while (!stop) {
				foundQn = false;
				for (int qIndex = lIndex + 1; !foundQn && qIndex < Qi.size(); qIndex++) {
					// search for a Qi & "jump over it".
					if (Edge.Intersection(getEdge(i), Qi.get(qIndex)) != null) {
						foundQn = true;
						Point a = getEdge(i).getUpperPoint();
						i = (i + 2) % edges.size();

						Edge Eb = Edge.Intersection(Edge.upHalfLine(a),
								getEdge(i));
						while (Eb == null || Eb.isPoint() == null || Point.equals(Eb.isPoint(),getEdge(i).getRightPoint())) {
							i = (i + 1) % edges.size();
							Eb = Edge.Intersection(Edge.upHalfLine(a),
									getEdge(i));
						}

						Point b = Eb.isPoint();
						sb.FT.add(b);
						sb.FT.add(getEdge(i).getRightPoint());
						i = (i + 1) % edges.size();
						//TODO delete the next rows.. this is only for debug
						/*if(sb.FT.size() > edges.size())
						{
							throw new BlfErrorException("FT is growing too much..");
						}*/
					}
				}

				// se ho trovato Qn aspetto un giro.. non credo serva ma male
				// non fa...
				if (!foundQn) {// search for rightMost edge
					boolean foundQw = false;
					/*Point stoppingPoint = lIndex == 0 ? getEdge(rightMost)
							.getLowerPoint() : edgeOfQw;*/
							
					if (Edge.equals(getEdge(i), getEdge(rightMost)))
						foundQw = true;

					if (foundQw) {

						sb.FT.add(getEdge(rightMost).getLowerPoint());

						// ora trovo FB


						int j = edgeIndex;// lato del leftMost

						// scorro la parte bassa del subhole fino a
						// rightMostEdge saltando
						// quando trovo un Qi precedente
						sb.FB.add(getEdge(j).p2);
						int interQi = -1;
						boolean jumpedRightMost = false;
						while (j != rightMost && !jumpedRightMost) {
							interQi = -1;
							for (int i1 = 1; i1 < Qi.size() && interQi == -1; i1++) {
								if (Point.equals(getEdge(j).p1, Qi.get(i1)))
									interQi = i1;
							}
							
							if (interQi != -1) {
								j = (j - 2 + edges.size()) % edges.size();
								Edge half = Edge.rightHalfLine(Qi.get(interQi));
								Edge app2 = Edge.Intersection(getEdge(j), half);
								while (app2 == null || app2.isPoint() == null || Point.equals(app2.isPoint(),getEdge(j).getUpperPoint())) {
									j = (j - 1 + edges.size()) % edges.size();
									if(j == rightMost)
										jumpedRightMost = true;
									app2 = Edge.Intersection(getEdge(j), half);
								}
								sb.FB.add(app2.isPoint());
								if(jumpedRightMost)
									sb.FB.add(getEdge(j).getUpperPoint());

							} else {
								sb.FB.add(getEdge(j).p1);
								if (j != rightMost)
									j = (j - 1 + edges.size()) % edges.size();
							}
						}

						sb.FB.add(getEdge(rightMost).getUpperPoint());

						stop = true;

					} else {
						// se non ho trovato niente aggiungo il punto e avanzo
						sb.FT.add(getEdge(i).p2);
						i = (i + 1) % edges.size();
					}
				}
			}

			// correggo FT fermandola all'altezza dell'ultimo elemento di FB..
			// per evitare
			// che vada avanti e poi torni indietro..(da problemi nel placing)
			if (lIndex != 0) {
				ArrayList<Point> appFt = new ArrayList<Point>();
				boolean stopCondition = false;
				int ind = 0;
				Point limit = sb.FB.get(sb.FB.size() - 2);
				while (ind < sb.FT.size() && !stopCondition) {
					if (sb.FT.get(ind).x < limit.x) {
						appFt.add(sb.FT.get(ind));
					} else {
						Point semilast = new Point(limit.x, appFt.get(appFt
								.size() - 1).y);
						appFt.add(semilast);
						appFt.add(limit);
						stopCondition = true;
					}
					ind++;
				}
				sb.FT = appFt;
			}
		}
	}

	public ArrayList<Point> getCandidates(CoreRectangle rect) {
		ArrayList<Point> li = new ArrayList<Point>();
		for (int i = 0; i < subHoles.size(); i++) {
			LinkedList<Point> Top, Bottom;

			Top = PackingProcedures.Top(subHoles.get(i), rect.width);
			Bottom = PackingProcedures.Bottom(subHoles.get(i), rect.width);

			ArrayList<CandidatePoint> app = PackingProcedures.Placing(
					rect.heigth, Bottom, Top);

			// controllo i candidati e mi prendo solo quelli feasible..
			for (int j = 0; j < app.size(); j++) {
				CandidatePoint x = app.get(j);
				if (x.feasible == true)
					li.add(x.p);
			}
		}
		return li;
	}

}
