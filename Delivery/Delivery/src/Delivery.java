import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;



public class Delivery {
	
	/*GLOBAL VARIABLES*/
	private static final String ITEMS_FILE = "/Users/uzairakram/School/CSCE4110/Project/Delivery/Delivery/src/ItemData.txt";
	private static final String ADDRESS_FILE = "/Users/uzairakram/School/CSCE4110/Project/Delivery/Delivery/src/addressMap.txt";
	private static final int INF = 9999;			//Definition of infinity for the purpose of the assignment
	private static final int NO_PARENT = -1; 		//NO PARENT
	private static final int testProblemSize = 200;	//Test Problem size
	
	static int totalCost = 0;						//KEEPS RUNNING TOTAL Of the trip COST
	static int Profit = 0;							//KEEPS running TOTAL of the profit
	static int maxCapacity = 7;						//This holds the capacity for the knapsack
	static int Complexity = 0;						//This variable is for complexity analysis of computation

	public static void main(String[] args) {
		
		int[][] adjacencyMatrix;									//Stores the adjacency Matrix For the Graph
		int[][] knapsackTable;							   		   //This is the knapsack table for the knapsack algorithm
		List<Item> items = getItems();							  //List of Items
		Set<Item> shippedItems = new HashSet<Item>();			 //SET of shipped items
		List<Edge> edges = getAddresses();						//List of addresses
		Set<Integer> addressIds = new HashSet<Integer>();	   //Set of unique address IDs
		CostPair [][] dijkstrasTable;						  //This is table for dijkstra's Algorithm
		int [][]MST;										 //This is the MST from dijkstra's
		
		System.out.println("Example Problem: \n");
		printItemsTable(items);									
		
		/*INITIALIZATIONS*/
		knapsackTable = new int[items.size() + 1][maxCapacity + 1];
		getAddressIds(edges, addressIds);
		adjacencyMatrix = new int[addressIds.size()][addressIds.size()];
		buildAdjacencyMatrix(edges, adjacencyMatrix);
		System.out.printf("\nGraph");
		printAdjacencyMatrix(adjacencyMatrix);
		
		/*DIJKSTRA'S*/
		dijkstrasTable = dijkstraAlgorithm(adjacencyMatrix);
	    System.out.println();
	    printDijkstras(dijkstrasTable);
	    System.out.println();
		
		/*buildsMST[]*/
	    MST = new int[addressIds.size()][addressIds.size()];
		MST = buildMST(dijkstrasTable, MST, adjacencyMatrix);
		System.out.print("MST");
		printAdjacencyMatrix(MST);
		
		/*Run Knapsack Algorithm*/
		shippedItems = knapsackAlgorithm(items, knapsackTable);
		printKnapsackTable(knapsackTable);
		
		/*Compute the trip from the given set of items and addresses*/
		computeTrip(shippedItems, adjacencyMatrix);
		System.out.println();
		
		System.out.println("\n\n5 different set of inputs\n");
		/*Run algorithm on 5 sets of random input data*/
		for(int i = 0; i < 5; i++) {
			System.out.println("-------------------------------------------------------------\n");
			System.out.println("Input " + (i+1));
			randomInput();
			System.out.println("-------------------------------------------------------------\n");
		}
	}
	
	//Functions creates a unique set of address IDs
	public static void getAddressIds(List<Edge> edges, Set<Integer> addressIds) {
		/*create set of addresses*/
		for (Edge edge : edges) {
			addressIds.add(edge.getSrc());
			addressIds.add(edge.getDest());
		}
	}
	
	//Function Builds the adjacency matrix of the input graph
	public static void buildAdjacencyMatrix(List<Edge> edges, int[][]adjacencyMatrix){
		/*build adjacency matrix*/
		initializeToInfinity(adjacencyMatrix);
		/*iterate through the edges to get the cost of links between connected nodes*/
		for (Edge edge : edges) {
			adjacencyMatrix[edge.getSrc() - 1][edge.getDest() - 1] = edge.getCost();
			adjacencyMatrix[edge.getDest() - 1][edge.getSrc() - 1] = edge.getCost();
		}
	}
	
	//Function implements the dijkstra's algorithm
	public static CostPair[][] dijkstraAlgorithm (int[][]adjacencyMatrix) {
		
		CostPair[][] dijkstrasTable = new CostPair[adjacencyMatrix.length - 1][adjacencyMatrix[0].length];
		initializeDijkstrasTable(dijkstrasTable);
		
		HashSet<Integer> visited = new HashSet<Integer>();
		
		/*Initialize first row*/
		int min = INF;
	    int minIndex = 0;
	    visited.add(1);
	    
	    /*Initialize the first row of dijkstra's table*/
	    for(int i = 0; i < dijkstrasTable[0].length; i++) {
	    	/*populate the first row with connected edges in adjacency matrix*/
	        if(adjacencyMatrix[0][i] != 0 || i == 0) {
	        	dijkstrasTable[0][i].setCost(adjacencyMatrix[0][i]);
	        } else {
	        	dijkstrasTable[0][i].setCost(INF);
	        }
	        /*write the parent*/
	        dijkstrasTable[0][i].setParent(1);
	        /*find the minimum cost link to create the next row of dijkstra*/
	        if (dijkstrasTable[0][i].getCost() < min && i!=0){
	            min = dijkstrasTable[0][i].getCost();
	            minIndex = i;
	        }
	    }
	    
	    /*include the minimum*/
	    visited.add(minIndex+1);
	    
	    /*Compute the table for dijkstra's*/
	    for(int i = 1; i < dijkstrasTable.length; i++) {
	        
	        for(int j = 0; j < dijkstrasTable[i].length; j++) {
	            
	            if(adjacencyMatrix[minIndex][j] != 0 || dijkstrasTable[i-1][j].getCost() != INF) {
	            	/*populate the table with the minimum edge*/
	            	//the table element is either taken from previous row or the adjacency matrix
	            	dijkstrasTable[i][j].setCost(getMin(min + adjacencyMatrix[minIndex][j], dijkstrasTable[i -1][j].getCost()));
	            	/*add the parent for the table element*/
	                if(dijkstrasTable[i][j].getCost() == dijkstrasTable[i-1][j].getCost()){
	                	dijkstrasTable[i][j].setParent(dijkstrasTable[i-1][j].getParent());
	                } else {
	                	dijkstrasTable[i][j].setParent(minIndex);
	                }
	            /*If there is no link yet found write infinite*/ 
	            } else if (minIndex != j){
	            	dijkstrasTable[i][j].setCost(INF);
	            	dijkstrasTable[i][j].setParent(i);
	            	/*otherwise get the value from previous row (Redundant)*/
	            } else {
	            	dijkstrasTable[i][j].setCost(dijkstrasTable[i-1][j].getCost());
	            	dijkstrasTable[i][j].setParent(dijkstrasTable[i-1][j].getParent());
	            }
	        }
	        /*After populating the row get the next node to compute the next row*/
	        min = INF;
	        
	        for(int j = 0; j < dijkstrasTable[i].length; j++) {
	            
	            if (visited.contains(j + 1) ) { continue; }
	            
	            if (dijkstrasTable[i][j].getCost() < min){
	                min = dijkstrasTable[i][j].getCost();
	                minIndex = j;
	            }
	        }
	        
	        visited.add(minIndex + 1);
	    }
	    
		return dijkstrasTable;
	}
	
	//Function builds the Minimum Spanning tree from the dijkstra's algorithm
	public static int [][] buildMST(CostPair[][] table, int[][]MST, int[][] adjacecymatrix){
	    //Use the last row of the table to build the minimum spanning tree
		for(int x = 0; x < table[table.length -1].length; x++) {
	        int y = table[table.length-1][x].getParent() ;
	        
	        MST[x][y] = adjacecymatrix[x][y];
	        MST[y][x] = adjacecymatrix[x][y];
	    }
	    
	    return MST;
	}
	
	//Function initialize the dijkstra's table
	public static void initializeDijkstrasTable(CostPair [][] dijkstrasTable) {
		//initialize every value 0
		for(int i = 0; i < dijkstrasTable.length; i++) {
			for (int j = 0; j < dijkstrasTable[i].length; j++) {
				dijkstrasTable[i][j] = new CostPair(0, 0);
			}
		}
	}
	
	//Function gets the minimum of two integers
	static int getMin(int a, int b){
	    return a <= b ? a : b;
	}
	
	//Function Prints the Dijkstra's Algorithm table
	static void printDijkstras(CostPair[][] dijkstrasTable){
		System.out.println("Dijkstra's Algorithm Table: ");
		for(int i = 0; i < dijkstrasTable[0].length; i++) {
			System.out.print((i+1) + "\t");
		}
		System.out.println();
	    for(int i = 0; i < dijkstrasTable.length; i++) {
	        for(int j = 0; j < dijkstrasTable[i].length; j++) {
	            if(dijkstrasTable[i][j].getCost() == INF)
	                System.out.printf("%s,%d\t", "∞", dijkstrasTable[i][j].getParent());
	            else
	                System.out.printf("%d,%d\t", dijkstrasTable[i][j].getCost(), dijkstrasTable[i][j].getParent());
	        }
	        System.out.println();
	    }
	    System.out.println();
		
	}
	
	//Function reads the file line by line
	public static List<String> readFileByLine(String filePath) {
		List<String> lines = new ArrayList<String>();
		
		File file = new File(filePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);

			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			while (dis.available() != 0) {
				lines.add(dis.readLine());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				bis.close();
				dis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return lines;
	}
	
	//Function extracts the list of of items from the Item file
	public static List<Item> getItems() {
		List<Item> items = new ArrayList<Item>();
		List<String> itemRawData = readFileByLine(ITEMS_FILE);
		
		for (String line : itemRawData) {
			String[] itemData = line.split(" ");
			int addressId = Integer.parseInt(itemData[0]);
			int profit = Integer.parseInt(itemData[1]);
			int weight = Integer.parseInt(itemData[2]);
			
			Item toAdd = new Item(addressId, profit, weight);
			items.add(toAdd);
		}
		items = items.stream().sorted((item, item2) -> item.getWeight() - item2.getWeight())
				.collect(Collectors.toList());
		return items;
	}
	
	//Function gets the list of edges from the Address map file
	public static List<Edge> getAddresses() {
		List<Edge> addresses = new ArrayList<>();
		List<String> addressRawData = readFileByLine(ADDRESS_FILE);
		
		for (String line : addressRawData) {
			String[] addressData = line.split(" ");
			int src = Integer.parseInt(addressData[0]);
			int dest = Integer.parseInt(addressData[1]);
			int cost = Integer.parseInt(addressData[2]);
			
			Edge toAdd = new Edge(src, dest, cost);
			addresses.add(toAdd);
		}
		return addresses;
	}
	
	//Function initializes the adjacency list to infinity
	public static void initializeToInfinity(int [][] adjacencyMatrix) {
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = 0; j < adjacencyMatrix[i].length; j++) {
				if (i != j) {
					adjacencyMatrix[i][j] = INF;
				}
				
			}
		}
	}
	
	//Function prints the adjacency Matrix
	public static void printAdjacencyMatrix(int[][] adjacencyMatrix) {
		System.out.println("---------------");
		System.out.println("\nAdjacency Matrix");
		System.out.print("\t");
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			System.out.print((i+1) + "\t");
		}
		System.out.println();
		
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			System.out.print(i+1);
			for (int j = 0; j < adjacencyMatrix[i].length; j++) {
				if(adjacencyMatrix[i][j] == INF)
	                System.out.printf("\t%s", "∞");
				else
					System.out.print("\t" + adjacencyMatrix[i][j]);
			}
			System.out.println();
		}
		System.out.println("---------------");
	}
	
	//Function prints the table of items in the list
	public static void printItemsTable(List<Item> items) {
		System.out.println("Item data:");
		for (Item item : items) {
			System.out.println(item);
		}
		
	}
	
	//Function Prints the knapsack table
	public static void printKnapsackTable(int [][] knapsackTable) {
		System.out.println("++++++++++");
		System.out.println("Knapsack Table");
		for (int i = 0; i < knapsackTable[0].length; i++) {
			System.out.print("\t" + i);
		}
		System.out.println();
		
		for (int i = 0; i < knapsackTable.length; i++) {
			System.out.print(i);
			for (int j = 0; j < knapsackTable[i].length; j++) {
				System.out.print("\t" + knapsackTable[i][j]);
			}
			System.out.println();
		}
		System.out.println("++++++++++");
	}
	
	//Function for testing the algorithm on several random inputs
	private static void randomInput() {
			int[][] adjacencyMatrix;									//Stores the adjacency Matrix For the Graph
			int[][] knapsackTable;							   		   //This is the knapsack table for the knapsack algorithm
			List<Item> items = randomItems();						  //List of Items
			Set<Item> shippedItems = new HashSet<Item>();			 //SET of shipped items
			Random rand = new Random(); 							// create instance of Random class
			
			maxCapacity = rand.nextInt(50) + 50; 
			
			/*INITIALIZATIONS*/
			knapsackTable = new int[testProblemSize + 1][maxCapacity + 1];
			adjacencyMatrix = new int[testProblemSize][testProblemSize];
			randomAdacencyMatrix(adjacencyMatrix);
			
			/*Run Knapsack Algorithm*/
			System.out.println("\nItems picked for shipping: \n");
			shippedItems = knapsackAlgorithm(items, knapsackTable);
			
			/*Compute the trip from the given set of items and addresses*/
			computeTrip(shippedItems, adjacencyMatrix);
			System.out.println();
		}
		
	//Function generates a random adjacency matrix of test size
	private static void randomAdacencyMatrix(int[][] adjacencyMatrix) {
			Random rand = new Random(); 							// create instance of Random class
			//GENERATE random adjacency matrix
			for(int i = 0; i < testProblemSize; i++) {
				for (int j = 0; j < testProblemSize; j++) {
					if (i == j)
						adjacencyMatrix[i][j] = 0;
					else 
						adjacencyMatrix[i][j] = rand.nextInt(9) + 1;
				}
			}
		}
		
	//Function generates random set of items list of test problem size
	public static List<Item> randomItems(){
			List<Item> items = new ArrayList<Item>();				//list of random items
			Random rand = new Random(); 							// create instance of Random class
			//Generate random items with random values of appropriate ranges
			for(int i = 0; i < testProblemSize; i++) {
				int cost = (rand.nextInt(19) + 1) * 10;
				int weight = rand.nextInt(8) + 1;
				Item item = new Item(i+1, cost, weight);
				items.add(item);
			}
			
			return items;
		}
	
	//Function Implements the knapsack Algorithm and returns the set of items
	public static Set<Item> knapsackAlgorithm(List<Item> items, int[][] knapsackTable) {
		
		Set<Item> shippedItems = new HashSet<Item>();
		//compute the optimal for each element in the knapsack table using the knapsack equation: V[i, w] = max(V[i - 1, w], Pi + V[i - 1, w - wi])
		for (int i = 1; i < knapsackTable.length; i++) {
			Item toConsider = items.get(i - 1);
			for (int j = 1; j < knapsackTable[i].length; j++) {		Complexity++;
				if (j - toConsider.getWeight() >= 0) {
					knapsackTable[i][j] = Integer.max(knapsackTable[i - 1][j],
													  toConsider.getProfit() + knapsackTable[i-1][j - toConsider.getWeight()]);
				} else {
					knapsackTable[i][j] = knapsackTable[i - 1][j];
				}
			}
			
		}
		
		//Get the items included in the knapsack through reverse engineering what items were included
		int considerValue = knapsackTable[items.size()][maxCapacity];
		int considerWeight = maxCapacity;
		
		while(considerWeight > 0) {			
			for (int i = items.size(); i > 0; i--) {		Complexity++;
				
				Item item = items.get(i-1);
				
				//If the optimal value is the same in the previous row go to the previous row other wise include item
				if (knapsackTable[i-1][considerWeight] == considerValue) {
					continue;
				} else {
					shippedItems.add(item);
					considerValue -= item.getProfit();
					considerWeight -= item.getWeight();
				}
				
			}
			
		}
		
		
		for (Item item : shippedItems) {
			System.out.println("Sent: " + item);
			Profit += item.getProfit();
		}
		return shippedItems;
	}
	
	// Function Computes the route given the optimal knapsack
	private static void computeTrip(Set<Item> shippedItems, int [][]adjacencyMatrix) {
		Item[] shippedArray = shippedItems.toArray(new Item[shippedItems.size()]);
		
		System.out.print("\nAddress\t Distance\tPath");
		/*loop through the picked items*/
		for (int i = 0; i < shippedArray.length - 1; i++) {
			dijkstra(adjacencyMatrix, shippedArray[i].getAddressId() - 1,  shippedArray[i+1].getAddressId() - 1);
		}
		
		System.out.println("\n\ncost of the entire trip: " + totalCost + "\n\nprofit of the entire trip: " + (Profit - totalCost));
		System.out.println("\nComputational Operations: " + Complexity);
		/*Reset TOTAL COST and profit for next route*/
		totalCost = 0;
		Profit = 0;
		Complexity = 0;
	}
	
/* citation: https://www.geeksforgeeks.org/printing-paths-dijkstras-shortest-path-algorithm/ */
	//Function Implements Dijkstra's Algorithm
	private static void dijkstra(int[][] adjacencyMatrix, int src, int dst) { 
		
		int nVertices = adjacencyMatrix[0].length; 			 // Total number of vertices in Graph
		int[] shortestDistances = new int[nVertices]; 		// Array holds the minimum distance from source to node
		boolean[] added = new boolean[nVertices];		   // Array records the added node as boolean 1-0 at node index
		int[] parents = new int[nVertices]; 			  // Array records parents nodes included to form path
		
		/* Initialize all distances as INFINITE and included as false*/
		for (int v = 0; v < nVertices; v++) { 
			shortestDistances[v] = INF; 
			added[v] = false; 
			} 
		
		/* Distance from source vertex to itself is always 0 */
		shortestDistances[src] = 0; 
		/* source has no predecessor parent node in path */
		parents[src] = NO_PARENT; 
		
		/*Find shortest path for all vertices*/
		for (int i = 1; i < nVertices; i++) { 		Complexity++;
			 /*Include the minimum distance vertex from the set of nodes not included*/
			int nearestVertex = -1; 
			int shortestDistance = INF; 
			/*iterate through the vertices and find min distance*/
			for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) { 
				if (!added[vertexIndex] && shortestDistances[vertexIndex] <  shortestDistance)  { 
					nearestVertex = vertexIndex; 
					shortestDistance = shortestDistances[vertexIndex]; 
					} 
				} 
			
			added[nearestVertex] = true; 
			
			/*Update distance values of the adjacent vertices of the new added node*/
			for (int vertexIndex = 0; vertexIndex < nVertices;  vertexIndex++)  { 	Complexity++;
				int edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex]; 
				if (edgeDistance > 0 && ((shortestDistance + edgeDistance) <  shortestDistances[vertexIndex]))  { 
					parents[vertexIndex] = nearestVertex; 
					shortestDistances[vertexIndex] = shortestDistance + edgeDistance; 
					} 
				} 
			} 
		
		computePath(src, shortestDistances, parents, dst); 
		
	} 

	// A utility function to print the constructed distances array and shortest paths 
	private static void computePath(int src, int[] distances, int[] parents, int dst) { 
		int nVertices = distances.length; 
		/*print Path*/
		System.out.print("\n" + (src+1) + " -> "); 
		System.out.print((dst+1) + " \t "); 
		System.out.print(distances[dst] + "\t\t"); 
		/*Running total sum of distances*/
		totalCost += distances[dst];
		/*tracePath back to source*/
		tracePath(dst, parents); 
		} 

	// Function to print shortest path from source to currentVertex using parents array 
	private static void tracePath(int currentVertex, int[] parents) { 
		/*BASE CASE: source is reached*/ 
		if (currentVertex == NO_PARENT) { return; } 
		/*recursively traverse the path*/
		tracePath(parents[currentVertex], parents); 
		/*Record the previous node*/
		System.out.print((currentVertex+1) + " "); 
	}
	
}
