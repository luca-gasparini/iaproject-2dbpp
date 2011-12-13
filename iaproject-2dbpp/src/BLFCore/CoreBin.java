package BLFCore;
import java.util.ArrayList;
import java.util.LinkedList;

public class CoreBin {
	ArrayList<CoreRectangle> packets;
	LinkedList<Hole> holes;
	
	CoreBin()
	{
		packets = new ArrayList<CoreRectangle>();
		holes = new LinkedList<Hole>();
	}
}
