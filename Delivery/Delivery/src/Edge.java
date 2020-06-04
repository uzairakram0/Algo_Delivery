
public class Edge {

	private int src;
	private int dest;
	private int cost;
	
	/*Class Constructor, initializer*/
	public Edge(int src, int dest, int cost) {
		this.src = src;
		this.dest = dest;
		this.cost = cost;
	}
	
	/*ACCESSERS*/
	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getSrc() {
		return src;
	}
	
	/*MUTATORS*/
	public void setSrc(int src) {
		this.src = src;
	}
	
	public int getDest() {
		return dest;
	}
	
	public void setDest(int dest) {
		this.dest = dest;
	}
		
	/*PRINTING*/
	@Override
	public String toString() {
		String toPrint = "SourceId: " + src + "; ";
		toPrint += "DestId: " + dest + "; ";
		toPrint += "Cost: " + cost + "; ";
		return toPrint;
	}
}
