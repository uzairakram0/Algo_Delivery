/*CLASS for computing dijkstra's table*/
public class CostPair {

	private int parent;
	private int cost;
	
	
	public CostPair(int parent, int cost) {
		this.parent = parent;
		this.cost = cost;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getParent() {
		return parent;
	}
	
	public void setParent(int parent) {
		this.parent = parent;
	}
		
	
	@Override
	public String toString() {
		String toPrint = "Parent: " + parent + "; ";
		toPrint += "Cost: " + cost + "; ";
		return toPrint;
	}
}
