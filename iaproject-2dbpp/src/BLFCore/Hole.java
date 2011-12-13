package BLFCore;
import java.util.LinkedList;
import java.util.ArrayList;


public class Hole {
	
	ArrayList<Edge> edges;
	LinkedList<SubHole> subHoles;
	ArrayList<Point> Qi;
	
	Hole(ArrayList<Edge> e)
	{
		edges = e;
		Qi = new ArrayList<Point>();
		subHoles = new LinkedList<SubHole>();
		divideSubHoles();
	}
	
	public void setEdges(ArrayList<Edge> e)
	{
		edges = e;
		divideSubHoles();
	}
	
	private Edge getEdge(int index)
	{
		return edges.get((index + edges.size())%edges.size()); 
	}
	
	public void divideSubHoles()
	{
		Qi.clear();
		subHoles.clear();
		
		int numberOfEdges = edges.size();
		int upperEdge = -1,lowerEdge = -1;
		int i = 0;
		
		//check for edges to be linked
		for(i=0;i < numberOfEdges;i++)
			if(!(Point.equals(getEdge(i-1).p2,getEdge(i).p1) && Point.equals(getEdge(i).p2, getEdge(i+1).p1)))
				throw new IllegalArgumentException("edges are not linked");
		
		//finding upper and lower edges
		for(i=0;i<numberOfEdges;i++)
		{
			if(edges.get(i).isHorizontal() 
					&& (upperEdge == -1 || edges.get(i).p1.y > edges.get(upperEdge).p1.y))
				upperEdge = i;
			if(edges.get(i).isHorizontal() 
					&& (upperEdge == -1 || edges.get(i).p1.y < edges.get(lowerEdge).p1.y))
				lowerEdge = i;
		}
		
		//is the list in clockWise order?
		boolean clockWise;
		if(Point.equals(edges.get(upperEdge).p1,edges.get(upperEdge).getLeftPoint()))
				clockWise = true;
		else clockWise = false;
		
		if(!clockWise)
		{
			//we have to revert the list
			ArrayList<Edge> app = new ArrayList<Edge>();
			i = numberOfEdges;
			while(i>0)
			{
				app.add(new Edge(getEdge(i).p2,getEdge(i).p1));
				i--;
			}
			upperEdge = (numberOfEdges - upperEdge) % numberOfEdges;
			lowerEdge = (numberOfEdges - lowerEdge) % numberOfEdges;
			edges = app;
		}
		
		
		ArrayList<Integer> leftMostEdges = new ArrayList<Integer>();
		//find leftMostEdges & Qi
		//scan the left side of the hole
		Qi.add(null);// Qi starts from Q1. every Qi is related to his upper LeftMostEdges
		i = lowerEdge + 1;
		while(i < upperEdge)
		{
			Edge currentEdge = edges.get(i);
			Edge previousEdge = edges.get(i-1);
			Edge nextEdge = edges.get(i+1);
			
			if(currentEdge.isVertical())
			{
				if(Point.equals(currentEdge.getLowerPoint(),previousEdge.getLeftPoint()) &&
						Point.equals(currentEdge.getUpperPoint(), nextEdge.getLeftPoint()))
				{
					//LEFTMOST
				}
				if(Point.equals(currentEdge.getLowerPoint(), previousEdge.getRightPoint()) &&
						Point.equals(currentEdge.getUpperPoint(), nextEdge.getRightPoint()))
				{
					//Qi
				}
			}
		}
	}
}
