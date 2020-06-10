public class WriteOutputFileThread extends Thread {

    private Graph graph;
    private String newPath;

    public WriteOutputFileThread(Graph graph, String newPath) {
        this.graph = graph;
        this.newPath = newPath;
    }

    /**
     * If this thread was constructed using a separate
     * {@code Runnable} run object, then that
     * {@code Runnable} object's {@code run} method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of {@code Thread} should override this method.
     *
     * @see #start()
     */
    @Override
    public void run(){
        graph.writeOutput(newPath);
    }
}
