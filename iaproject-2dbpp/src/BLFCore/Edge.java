package BLFCore;

public class Edge {
	// Lati solo verticali o orizzontali
	public Point p1, p2;

	Edge(Point p1, Point p2) {
		if (p1.x != p2.x && p1.y != p1.y) {
			this.p1 = this.p2 = null;
		} else {
			this.p1 = p1;
			this.p2 = p2;
		}
	}

	//crea la copia di un lato
	Edge(Edge e)
	{
		p1 = new Point(e.p1);
		p2 = new Point(e.p2);
	}
	
	//crea una retta verticale o orizzontale
	//c  la coordinata x se verticale, y se orizzontale
	Edge(double c,boolean vertical)
	{
		if(vertical)
		{
			p1 = new Point(c,0);
			p2 = new Point(c,Double.MAX_VALUE);
		}
		else
		{
			p1 = new Point(0,c);
			p2 = new Point(Double.MAX_VALUE,c);
		}
	}
	
	public boolean isVertical() {
		return p1 != null && p2 != null && p1.x == p2.x;
	}

	public boolean isNull() {
		return p1 == null || p2 == null;
	}

	public boolean isHorizontal() {
		return !isNull() && !isVertical();
	}

	public Point getUpperPoint() {
		return p1.y > p2.y ? p1 : p2;
	}

	public Point getLowerPoint() {
		return p1.y > p2.y ? p2 : p1;
	}

	public Point getRightPoint() {
		return p1.x < p2.x ? p2 : p1;
	}

	public Point getLeftPoint() {
		return p1.x < p2.x ? p1 : p2;
	}

	public Point isPoint()//se il lato coincide con un punto ritorna il punto. altrimenti null
	{
		return Point.equals(p1, p2) ? new Point(p1) : null;
	}
	
	public static Edge upHalfLine(Point x)//semiretta da x verso l'alto
	{
		return new Edge(x,new Point(x.x,Double.MAX_VALUE));
	}
	
	public static Edge rightHalfLine(Point x)//semiretta da x verso destra
	{
		return new Edge(x,new Point(Double.MAX_VALUE,x.y));
	}
	
	//scambia i punti, utile per tenere le varie liste in clockwise
	public void swapPoints()
	{
		Point app = p1;
		p1 = p2;
		p2 = app;
	}
	
	// ritorna il segmento intersezione tra i due lati, null se lati disgiunti
	public static Edge Intersection(Edge e1, Edge e2) {
		
		if (e1.isVertical() && e2.isVertical()) {
			if (e1.p1.x != e2.p1.x) {
				return null;
			}
			if(e1.getLowerPoint().y >e2.getUpperPoint().y ||
					e2.getLowerPoint().y > e1.getUpperPoint().y)
				return null;
				
			return new Edge(new Point(e1.p1.x, Math.min(e1.getUpperPoint().y,
					e2.getUpperPoint().y)), new Point(e1.p1.x, Math.max(
					e1.getLowerPoint().y, e2.getLowerPoint().y)));

		} else if (e1.isHorizontal() && e2.isHorizontal()) {
			
			if (e1.p1.y != e2.p1.y) {
				return null;
			}
			
			if(e1.getRightPoint().x < e2.getLeftPoint().x ||
					e2.getRightPoint().x < e1.getLeftPoint().x)
				return null;
			
			return new Edge(new Point(Math.max(e1.getLeftPoint().x,
					e2.getLeftPoint().x), e1.p1.y), new Point(Math.min(
					e1.getRightPoint().x, e2.getRightPoint().x), e2.p2.y));

		}
		else if(e1.isVertical() && e2.isHorizontal())
		{
			if(e1.getUpperPoint().y >= e2.p1.y && 
					e1.getLowerPoint().y <= e2.p1.y &&
					e2.getLeftPoint().x <= e1.p1.x &&
					e2.getRightPoint().x >= e1.p1.x)
			{
				Point app = new Point(e1.p1.x,e2.p1.y);
				return new Edge(app,app);
			}
			else return null;
		}
		else return Intersection(e2,e1);

	}

	//ritorna la lunghezza del segmento
	public double length()
	{
		double diff = 0;
		if(isVertical())
			diff = p1.y - p2.y;
		else
			diff = p1.x - p2.x;
		
		if(diff>0)
			return diff;
		else return -diff;
	}
	
	//ritorna il punto intersezione tra punto x e Edge e1 se esiste, altrimenti null
	public static Point Intersection(Edge e1,Point x)
	{
		return e1.getLeftPoint().x <= x.x && e1.getRightPoint().x >= x.x && 
				e1.getUpperPoint().y >= x.y &&  e1.getLowerPoint().y <= x.y ?
						x : null;
	}
	
	public static boolean equals(Edge e1, Edge e2)
	{
		if (Point.equals(e1.p1,e2.p1) && Point.equals(e1.p2,e2.p2))
			return true;
		if (Point.equals(e1.p1,e2.p2) && Point.equals(e1.p2,e2.p1))
			return true;
		return false;
	}
}
