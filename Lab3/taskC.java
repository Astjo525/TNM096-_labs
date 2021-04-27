import WumpusWorld.*;
import CNF_Solver.CNF_Solver;

public class taskC {
    public static void main(String args[])
    {
        //WumpusWorld.MyAgent wumpus = new WumpusWorld.MyAgent.MyAgent();

        //WumpusWorld.MyAgent.doAction();

        WumpusWorld.WumpusWorld w = new WumpusWorld.WumpusWorld();
        WumpusWorld.MyAgent wumpus = new WumpusWorld.MyAgent.MyAgent(w);


    }
    
}
