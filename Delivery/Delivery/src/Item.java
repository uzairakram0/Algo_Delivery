
public class Item {

	private int profit;
	private int weight;
	private int addressId;
	
	
	public Item(int addressId, int cost, int weight) {
		this.profit = cost/10;
		this.weight = weight;
		this.addressId = addressId;
	}
	
	public int getProfit() {
		return profit;
	}
	
	public void setProfit(int cost) {
		this.profit = cost/10;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public int getAddressId() {
		return addressId;
	}
	
	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
		
	
	@Override
	public String toString() {
		String toPrint = "Cost: " + profit + "; ";
		toPrint += "Weight: " + weight + "; ";
		toPrint += "AddressId: " + addressId + "; ";
		return toPrint;
	}
}
