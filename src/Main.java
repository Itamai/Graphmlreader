import CustomExceptions.GraphIsNotConnectedException;
import CustomExceptions.NoNodesParameterException;
import CustomExceptions.NoOutputPathException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main
{

    public static void main(String[] argv) throws Exception {
		Logger mainLogger = Logger.getLogger(Graph.class.getName());

		Graph graph;

		try {	// check if there was a path given as a parameter
			graph = new Graph(argv[0]);
		} catch(ArrayIndexOutOfBoundsException e){
			mainLogger.log(Level.SEVERE, "No path to the graphml file entered", e);
			throw e;
		}

		try {	// check if there is a second argument, otherwise continue in catch
			switch (argv[1]) {
				case "-s":
					mainLogger.log(Level.INFO, "Second argument for shortest path");

					if(!graph.getConnectivity()){
						try{
							throw new GraphIsNotConnectedException();
						} catch(GraphIsNotConnectedException e){
							mainLogger.log(Level.SEVERE, "The graph is not connected", e);
							throw e;
						}
					}

					int sourceId;
					int targetId;

					try {
						sourceId = Integer.parseInt(argv[2]);
						targetId = Integer.parseInt(argv[3]);
					} catch (ArrayIndexOutOfBoundsException e) {
						Exception ex = new NoNodesParameterException();
						mainLogger.log(Level.INFO, "No source node or/and target node argument entered", ex);
						throw ex;
					} catch (NumberFormatException e) {
						mainLogger.log(Level.INFO, "Wrong source and/or target argument entered", e);
						throw e;
					}

					graph.getShortestPaths(sourceId, targetId);
					break;
				case "-b":
					mainLogger.log(Level.INFO, "Second argument for betweenness");

					int nodeId;

					try {
						nodeId = Integer.parseInt(argv[2]);
					} catch (ArrayIndexOutOfBoundsException e) {
						Exception ex = new NoNodesParameterException();
						mainLogger.log(Level.INFO, "No node argument entered", ex);
						throw ex;
					} catch (NumberFormatException e) {
						mainLogger.log(Level.INFO, "Wrong node argument entered", e);
						throw e;
					}

					graph.getBetweennessCentrality(nodeId);
					break;
				case "-a":
					mainLogger.log(Level.INFO, "Second argument for calculating everything and creating a new output file");

					Path newPath;

					try {
						newPath = Paths.get(argv[2]);
					} catch (ArrayIndexOutOfBoundsException e) {
						Exception ex = new NoOutputPathException();
						mainLogger.log(Level.INFO, "No directory was chosen for the output file", ex);
						throw ex;
					}

					if (!Files.isDirectory(newPath))
						mainLogger.log(Level.INFO, "No valid directory was chosen for the output file");
					else
						graph.calculateEverything(newPath.toString());
					break;
				default:
					Exception e = new IllegalArgumentException();
					mainLogger.log(Level.INFO, "Wrong second argument given. Unknown function." , e);
					System.exit(1);
			}
		} catch(ArrayIndexOutOfBoundsException e){ // Case when there is just the path to the graph given
			mainLogger.log(Level.INFO, "No second argument, printing the graph's properties");
			graph.printGraphProperties();
		}
		System.exit(0);
	}
}