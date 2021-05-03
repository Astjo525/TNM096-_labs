    import java.util.*;

//import ../CNF_Solver.CNF_Solver;

public class MyAgent implements Agent
{
    private World w;
    private Set<Set<String>> KB;
    private Set<Set<String>> possible_wumpus;
    private Set<Set<String>> possible_stenches;
    private int[] wumpus = {};
    private CNF_Solver solve;
    private ArrayList<String> visited;
    private Boolean wumpus_killed = false;
    
    public MyAgent(World world)
    {
        w = world;

        possible_wumpus = new HashSet<Set<String>>();
        possible_stenches = new HashSet<Set<String>>();

        KB = new HashSet<Set<String>>();
        init_KB();

        visited = new ArrayList<String>();

        //System.out.println(KB);
    }

    public void init_KB()
    {

        String p = World.PIT;
        String b = World.BREEZE;
        String s = World.STENCH;
        String w = World.WUMPUS;
        String g = World.GLITTER;
        String t = World.GOLD;

        //TODO: does the board only contain one Wumpus??
        Set<String> one_wumpus = new HashSet<String>();
        Set<String> one_stench = new HashSet<String>();
        
        for(int i = 1; i < 5; i++)
        {
            for(int j = 1; j < 5; j++)
            {
                String pij = p + String.valueOf(i) + String.valueOf(j);
                String wij = w + String.valueOf(i) + String.valueOf(j);
                String gij = g + String.valueOf(i) + String.valueOf(j);
                String tij = t + String.valueOf(i) + String.valueOf(j);
                String bij = b + String.valueOf(i) + String.valueOf(j);
                String sij = s + String.valueOf(i) + String.valueOf(j);

                //TODO: Do we need to add glitter and gold in KB?
                //add_rule_to_KB(gij, t, i, j);
                //add_rule_to_KB(tij, g, i, j);

                Set<String> breeze = new HashSet<String>(Arrays.asList("-" + bij));
                Set<String> stench = new HashSet<String>(Arrays.asList("-" + sij));
                
                one_wumpus = new HashSet<String>();
                one_wumpus.add(wij);
                // one_stench = new HashSet<String>();
                // one_stench.add(sij);

                if(i + 1 < 5)
                {
                    //TODO: Should we add for breeze and stench also??
                    add_rule_to_KB(pij, b, i+1, j);
                    add_rule_to_KB(wij, s, i+1, j);

                    breeze = concatenate_rule(breeze, p, i+1, j);
                    stench = concatenate_rule(stench, w, i+1, j);                    
                }
                if(i - 1 > 0)
                {
                    add_rule_to_KB(pij, b, i-1, j);
                    add_rule_to_KB(wij, s, i-1, j);

                    breeze = concatenate_rule(breeze, p, i-1, j);
                    stench = concatenate_rule(stench, w, i-1, j);      
                }
                if(j + 1 < 5)
                {
                    add_rule_to_KB(pij, b, i, j+1);
                    add_rule_to_KB(wij, s, i, j+1);

                    breeze = concatenate_rule(breeze, p, i, j + 1);
                    stench = concatenate_rule(stench, w, i, j + 1);      
                }
                if(j - 1 > 0)
                {
                    add_rule_to_KB(pij, b, i, j-1);
                    add_rule_to_KB(wij, s, i, j-1);

                    breeze = concatenate_rule(breeze, p, i, j-1);
                    stench = concatenate_rule(stench, w, i, j-1);   
                }
                
                KB.add(breeze);
                KB.add(stench);
                
                possible_wumpus.add(one_wumpus);
                // possible_stenches.add(one_stench);
            }
        }
        
        System.out.println("DONE");
    }

    public void add_rule_to_KB(String current, String follows, int x, int y)
    {
        String adjacent = follows + String.valueOf(x) + String.valueOf(y);
        Set<String> rule = new HashSet<String>(Arrays.asList("-" + current, adjacent));
        KB.add(rule);
    }

    public Set<String> concatenate_rule(Set<String> rule, String type, int x, int y)
    {
        String adjacent = type + String.valueOf(x) + String.valueOf(y);
        rule.add(adjacent);
        return rule;
    }

    public void add_observation_to_KB(String obs, int cX, int cY)
    {
        Set<String> observation = new HashSet<String>(Arrays.asList(obs + String.valueOf(cX) + String.valueOf(cY)));
        if(!KB.contains(observation))
        {
            KB.add(observation);
        }
    }

    public ArrayList<String> check_moves(ArrayList<String> options, String direction, int x, int y)
    {
        String pit = World.PIT + String.valueOf(x) + String.valueOf(y);
        String wumpus = World.WUMPUS + String.valueOf(x) + String.valueOf(y);
        Set<String> pit_set = new HashSet<String>(Arrays.asList("-" + pit));
        Set<String> wumpus_set = new HashSet<String>(Arrays.asList("-" + wumpus));
        //TODO: How to check where we can go?
        if(KB.contains(pit_set) && KB.contains(wumpus_set))
        {
            options.add(direction);
        }
        else if(KB.contains(pit_set) && wumpus_killed == true)
        {
            options.add(direction);
        }

        return options;
    }
    
    public void update_wumpus_killed()
    {
        int x = wumpus[0];
        int y = wumpus[1];

        add_observation_to_KB(World.WUMPUS, x, y);
       
        if(x + 1 < 5)
        {
            add_observation_to_KB("-" + World.STENCH, x + 1, y);
        }
        if(x - 1 > 0)
        {
            add_observation_to_KB("-" + World.STENCH, x - 1, y);
        }
        if(y + 1 < 5)
        {
            add_observation_to_KB("-" + World.STENCH, x, y + 1);
        }
        if(y - 1 > 0)
        {
            add_observation_to_KB("-" + World.STENCH, x,  y-1);
        }
    }
 
// Ask your solver to do an action

    public void doAction()
    {
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();

        //System.out.println(cX);
        //System.out.println(cY);
        
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY))
        {
            w.doAction(World.A_GRAB);
            return;
        }
        
        //Basic action:
        //We are in a pit. Climb up.
        if (w.isInPit())
        {
            w.doAction(World.A_CLIMB);
            return;
        }

        //Check if has breeze
        if (w.hasBreeze(cX, cY))
        {
            add_observation_to_KB(World.BREEZE, cX, cY);
            System.out.println("I am in a Breeze");
        }
        else
        {
            add_observation_to_KB("-" + World.BREEZE, cX, cY);
        }

        // Check if has stench
        if (w.hasStench(cX, cY))
        {
            add_observation_to_KB(World.STENCH, cX, cY);
            System.out.println("I am in a Stench");
            Set<String> one_stench = new HashSet<String>();
            one_stench.add(World.STENCH + String.valueOf(cX) + String.valueOf(cY));
            possible_stenches.add(one_stench);
        }
        else{
            System.out.println("Not stench");
            add_observation_to_KB("-" + World.STENCH, cX, cY);
        }

        // Check if has pit
        if (w.hasPit(cX, cY))
        {
            //System.out.println(cX);
            //System.out.println(cY);

            add_observation_to_KB(World.PIT, cX, cY);
            System.out.println("I am in a Pit");
        }
        else
        {
            add_observation_to_KB("-" + World.PIT, cX, cY);
        }
        
        if(w.hasWumpus(cX,cY))
        {
            System.out.println("DEAD");
        }
        else
        {
            add_observation_to_KB("-" + World.WUMPUS, cX, cY);
        }

        //String visit = String.valueOf(cX + cY);

        //System.out.println(KB);
        // if(!visited.contains(visit))
        // {
        //     KB = CNF_Solver.Solver(KB);
        //     visited.add(visit);
        // }

        KB = CNF_Solver.Solver(KB);

        System.out.println(KB);

        if(wumpus_killed == false)
        {

            Set<Set<String>> stench_found = new HashSet<Set<String>>(KB);

            System.out.println(stench_found);

            stench_found.retainAll(possible_stenches);

            System.out.println(stench_found);

            if(stench_found.size() == 2)
            {
                System.out.println("hej");
                int[] stench = new int[4];
                //int[] stench2 = new int[2];

                int count = 0;
                
                for(Set<String> e: stench_found)
                {
                    String e_str = e.iterator().next();
                    System.out.println(e_str);
                    char[] stench_pos = e_str.toCharArray();
                    stench[count] = Character.getNumericValue(stench_pos[1]);
                    stench[count + 1] = Character.getNumericValue(stench_pos[2]);
                    count = 2;
                }

                System.out.println(stench[0]);
                
                System.out.println(stench[1]);
                
                System.out.println(stench[2]);
                
                System.out.println(stench[3]);

                if(stench[0] + 1 == stench[2] && stench[1] == stench[3] + 1)
                {
                    add_observation_to_KB(World.WUMPUS, stench[2], stench[1]);
                    System.out.println("Wumpus in" + stench[2] + stench[1]);
                    KB = CNF_Solver.Solver(KB);
                }
                else if(stench[0] == stench[2] + 1 && stench[1] + 1 == stench[3])
                {
                    add_observation_to_KB(World.WUMPUS, stench[0], stench[3]);
                    System.out.println("Wumpus in" + stench[0] + stench[3]);
                    KB = CNF_Solver.Solver(KB);
                }
            }
        }

        if(wumpus.length == 0)
        {
            Set<Set<String>> wumpus_found = new HashSet<Set<String>>(KB);

            wumpus_found.retainAll(possible_wumpus);

            if(wumpus_found.size() != 0)
            {
                Set<String> wump_set = wumpus_found.iterator().next();
                String wump_str = wump_set.iterator().next();

                char[] wump_pos = wump_str.toCharArray();

                int x = Character.getNumericValue(wump_pos[1]);
                int y = Character.getNumericValue(wump_pos[2]);

                wumpus = new int[2];

                wumpus[0] = x;
                wumpus[1] = y;
            }
        }

        if(wumpus.length > 0 && wumpus_killed == false)
        {
            if(wumpus[0] == cX)
            {
                wumpus_killed = true;
                if(wumpus[1] > cY)
                {
                    w.doAction(World.A_SHOOT_UP);
                    update_wumpus_killed();
                    KB = CNF_Solver.Solver(KB);
                    return;
                }
                else if(wumpus[1] < cY)
                {
                     w.doAction(World.A_SHOOT_DOWN);
                     update_wumpus_killed();
                    KB = CNF_Solver.Solver(KB);
                     return;
                }
            }
            else if(wumpus[1] == cY)
            {
                wumpus_killed = true;
                if(wumpus[0] > cX)
                {
                    w.doAction(World.A_SHOOT_RIGHT);
                    update_wumpus_killed();
                    KB = CNF_Solver.Solver(KB);
                    return;
                }
                else if(wumpus[0] < cY)
                {
                    w.doAction(World.A_SHOOT_LEFT);
                    update_wumpus_killed();
                    KB = CNF_Solver.Solver(KB);
                    return;
                }
            }
        }
        
        ArrayList<String> options = new ArrayList<String>();

        if(cX + 1 < 5)
        {
            options = check_moves(options, World.A_MOVE_RIGHT, cX + 1, cY);
        }
        if(cX - 1 > 0)
        {
            options = check_moves(options, World.A_MOVE_LEFT, cX - 1, cY);
        }
        if(cY + 1 < 5)
        {
            options = check_moves(options, World.A_MOVE_UP, cX, cY + 1);
        }
        if(cY - 1 > 0)
        {
            options = check_moves(options, World.A_MOVE_DOWN, cX, cY - 1);
        }
        
        int move_index = new Random().nextInt(options.size());
        
        System.out.println("Move options");
        System.out.println(options);

        System.out.println("Random move index");
        System.out.println(move_index);

        if(options.size() != 0)
        {
            w.doAction(options.get(move_index));
        }
        else
        {
            int rnd = (int)(Math.random() * 3);
            if (rnd == 0) 
            {
                w.doAction(World.A_MOVE_RIGHT);
                return;
            }
            if (rnd == 1)
            {
                w.doAction(World.A_MOVE_UP);
                return;
            }
            if (rnd == 2) 
            {
                w.doAction(World.A_MOVE_LEFT);
                return;
            }
            if (rnd >= 3)
            {
                w.doAction(World.A_MOVE_DOWN);
                return;
            }
        }
        

        
        
        //Random move actions
        // int rnd = (int)(Math.random() * 3);
        // if (rnd == 0) 
        // {
        //     w.doAction(World.A_MOVE_RIGHT);
        //     return;
        // }
        // if (rnd >= 1)
        // {
        //     w.doAction(World.A_MOVE_UP);
        //     return;
        // }
    }
}


            // ACTIONS AVAILABLE
			// --------------------------------
            // w.doAction(World.A_MOVE_RIGHT);
            // w.doAction(World.A_MOVE_LEFT);
            // w.doAction(World.A_MOVE_UP);
            // w.doAction(World.A_MOVE_DOWN);
			
			// w.doAction(World.A_SHOOT_UP);
			// w.doAction(World.A_SHOOT_DOWN);
			// w.doAction(World.A_SHOOT_LEFT);
			// w.doAction(World.A_SHOOT_RIGHT);
			
			// w.doAction(World.A_GRAB);
			// w.doAction(World.A_CLIMB)
						
			
			// SENSING ACTIONS (return true/false)
			// ------------------------------------
			// w.hasGlitter(cX,cY)
			// w.hasBreeze(cX,cY)
			// w.hasStench(cX, cY)
			// w.hasPit(cX, cY)
			// w.hasWumpus(cX, cY)
			// w.isInPit()
			// w.wumpusAlive()
			// w.hasArrow()
			
			
