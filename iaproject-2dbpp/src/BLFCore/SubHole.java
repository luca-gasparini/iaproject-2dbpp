package BLFCore;
import java.util.LinkedList;
public class SubHole {
	LinkedList<Point> FT;//contiene anche il rightmost
	LinkedList<Point> FB;//contiene anche il rightmost.. e il leftmost
	
	SubHole()
	{
		FT = new LinkedList<Point>();
		FB = new LinkedList<Point>();
	}
	Point Q;
}
