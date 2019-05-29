import java.util.ArrayList;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

/**
 * Algorithms 4th, part 2, assignment 8.
 * @author Chenyin Gong
 * args: *.txt from: /Users/gongchenyin/eclipse-workspace/Algorithms/src/project-data/p8-baseball/baseball 
 */
public class BaseballElimination
{
    private final int n;                    // number of teams
    private final ArrayList<String> teams;  // array of teams' names
    private final int[] w;                  // array of wins
    private final int[] l;                  // array of losses
    private final int[] r;                  // array of remaining games
    private final int[][] g;                // array of games left to play between two teams
    
    /**
     * Creates a baseball division from given filename.
     * @param filename the given file
     */
    public BaseballElimination(String filename)
    {
        In in = new In(filename);
        n = in.readInt();
        teams = new ArrayList<String>();
        w = new int[n];
        l = new int[n];
        r = new int[n];
        g = new int[n][n];
        
        for (int i = 0; i < n; i++)
        {
            teams.add(in.readString());
            w[i]     = in.readInt();
            l[i]     = in.readInt();
            r[i]     = in.readInt();
            for (int j = 0; j < n; j++)
                g[i][j] = in.readInt();
        }
    }
    
    /**
     * Returns number of teams.
     * @return number of teams
     */
    public int numberOfTeams()
    {
        return n;
    }
    
    /**
     * Returns all teams.
     * @return all teams
     */
    public Iterable<String> teams()
    {
        return teams;
    }
    
    // check whether the team is valid
    private void validate(String team)
    {
        if (team == null) throw new IllegalArgumentException("argument is null");
        if (!teams.contains(team))
            throw new IllegalArgumentException("argument is invalid: team not belong to input teams");
    }
    
    /**
     * Returns number of wins for given team.
     * @param team the given team
     * @return number of wins for given team
     */
    public int wins(String team)
    {
        validate(team);
        return w[teams.indexOf(team)];
    }
    
    /**
     * Returns number of losses for given team.
     * @param team the given team
     * @return number of losses for given team
     */
    public int losses(String team)
    {
        validate(team);
        return l[teams.indexOf(team)];
    }
    
    /**
     * Returns number of remaining games for given team.
     * @param team the given team
     * @return number of remaining games for given team
     */
    public int remaining(String team)
    {
        validate(team);
        return r[teams.indexOf(team)];
    }
    
    /**
     * Returns number of remaining games between team1 and team2.
     * @param team1 team 1
     * @param team2 team 2
     * @return number of remaining games between team1 and team2
     */
    public int against(String team1, String team2)
    {
        validate(team1);
        validate(team2);
        int i = teams.indexOf(team1), j = teams.indexOf(team2);
        return g[i][j];
    }
    
    /**
     * Returns true if given team is eliminated; false otherwise.
     * @param team the given team
     * @return true if given team is eliminated; false otherwise
     */
    public boolean isEliminated(String team)
    {
        validate(team);
        // trivial elimination
        int x = teams.indexOf(team);
        for (int i = 0; i < n; i++)
            if (w[x] + r[x] < w[i]) return true;
        
        // nontrivial elimination
        FlowNetwork G = flowNetwork(team);
        int s = G.V() - 2, t = G.V() - 1;
        FordFulkerson maxflow = new FordFulkerson(G, s, t);
        double value = maxflow.value();
        double sumOfCapacity = 0;
        for (FlowEdge edge : G.adj(s))
            sumOfCapacity += edge.capacity();
        if (value < sumOfCapacity) return true;    // some edges pointing from s are not full
        return false;                              // all edges pointing from s are full
    }
    
    /**
     * Returns subset R of teams that eliminates given team; null if not eliminated.
     * @param team the given team
     * @return subset R of teams that eliminates given team; null if not eliminated
     */
    public Iterable<String> certificateOfElimination(String team)
    {
        validate(team);
        Queue<String> list = new Queue<>();
        // trivial elimination
        int x = teams.indexOf(team);
        for (int i = 0; i < n; i++)
            if (w[x] + r[x] < w[i])
            {
                list.enqueue(teams.get(i));
                return list;
            }
        
        // nontrivial elimination
        FlowNetwork G = flowNetwork(team);
        int s = G.V() - 2, t = G.V() - 1;
        FordFulkerson maxflow = new FordFulkerson(G, s, t);
        for (int i = 0; i < n - 1; i++)
        {
            int i0; // index of arrays
            if (i < x) i0 = i;
            else       i0 = i + 1;
            if (maxflow.inCut(i)) list.enqueue(teams.get(i0));
        }
        if (list.isEmpty()) return null;
        return list;
    }
    
    /**********************************************************************
     *                      Helper Functions
     * ********************************************************************/
    // construct a FlowNetwork object (exclude the team)
    private FlowNetwork flowNetwork(String team)
    {
        FlowNetwork G = new FlowNetwork(sumOfgames(n-1)+n+1);
        
        int s = G.V() - 2, t = G.V() - 1, x = teams.indexOf(team);
        int count = n - 2; // helper variable to record game vertices
        for (int i = 0; i < n-1; i++)
        {
            int i0 = 0; // index of instance arrays
            if (i < x) i0 = i;
            else       i0 = i + 1;
            G.addEdge(new FlowEdge(i, t, w[x] + r[x] - w[i0]));
            for (int j = i+1; i < n - 2 && j < n - 1; j++)
            {
                int j0 = 0; // index of instance arrays
                if (j < x) j0 = j;
                else       j0 = j + 1;
                G.addEdge(new FlowEdge(s, j+count, g[i0][j0]));
                G.addEdge(new FlowEdge(j+count, j, Double.POSITIVE_INFINITY));
                G.addEdge(new FlowEdge(j+count, i, Double.POSITIVE_INFINITY));
            }
            count += (n - 3 - i);
        }
        return G;
    }
    
    // combination function to compute the number of game vertices
    private int sumOfgames(int i)
    {
        int sum = 0;
        if (i <= 1) return 0;
        sum = i - 1 + sumOfgames(--i);
        return sum;
    }
    
    // unit testing
    public static void main(String[] args)
    {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) 
        {
            if (division.isEliminated(team))
            {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                    StdOut.print(t + " ");
                StdOut.println("}");
            }
            else StdOut.println(team + " is not eliminated");
        }
    }
}