
import java.util.*;

import CNF_Solver.CNF_Solver;

public class taskA {

    public static void main(String args[])
    {
        //String A = "a v b v -c";
        //String B = "-a v b v f";
        // Set<String> A = new HashSet<String>(Arrays.asList("-a", "b", "d"));
        // Set<String> B = new HashSet<String>(Arrays.asList("-a", "b", "c", "-f"));
        // Set<String> C = new HashSet<String>(Arrays.asList("a", "-c", "-f"));
        // Set<String> D = new HashSet<String>(Arrays.asList("b", "g", "f"));
        // Set<String> E = new HashSet<String>(Arrays.asList("-b", "e", "-f"));

        // BOB
        // Set<String> A = new HashSet<String>(Arrays.asList("-sun", "-money", "ice"));
        // Set<String> B = new HashSet<String>(Arrays.asList("-money", "ice", "movie"));
        // Set<String> C = new HashSet<String>(Arrays.asList("-movie", "money"));
        // Set<String> D = new HashSet<String>(Arrays.asList("-movie", "-ice"));
        // Set<String> E = new HashSet<String>(Arrays.asList("movie"));
        

        // TASK 2 HATS

        // Pos = blue
        // Neg = red
        // a = person 1, b = person 2, c = me
        // red(a) & blue(b) => blue(c)
        Set<String> A = new HashSet<String>(Arrays.asList("a", "-b", "c"));

        // blue(a) & red(b) => blue(c)
        Set<String> B = new HashSet<String>(Arrays.asList("-a", "b", "c"));

        // blue(a) & blue(b) => red(c)
        Set<String> C = new HashSet<String>(Arrays.asList("-a", "-b", "-c"));

        // given a is red and b is blue
        Set<String> D = new HashSet<String>(Arrays.asList("a"));
        Set<String> E = new HashSet<String>(Arrays.asList("-b"));


        //Set<String> res = Solver.Resolution(C, B);

        //System.out.println(res);

        ArrayList<Set<String>> KB = new ArrayList<Set<String>>(Arrays.asList(A, B, C, D, E));
        //Solver solver = new Solver();
        KB = CNF_Solver.Solver(KB);

        System.out.println(KB);
    }
}