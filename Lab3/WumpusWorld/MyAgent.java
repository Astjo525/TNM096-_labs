import java.util.*;

//import ../CNF_Solver.CNF_Solver;

public class MyAgent implements Agent
{
    private World w;
    private Set<Set<String>> KB;
    private CNF_Solver solve;
    
    public MyAgent(World world)
    {
        w = world;

        KB = new HashSet<Set<String>>();
        init_KB();

        //System.out.println(KB);


        // (-B22 V P21) &
        // (-B22 V P23) &
        // (-B22 V P12) &
        // (-B22 V P32)
       // B22 -> (P21 or P23 or P12 or P32)
       // (-B22 V P21 V P23 V P12 V P32)
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
                add_rule_to_KB(gij, t, i, j);
                add_rule_to_KB(tij, g, i, j);

                Set<String> breeze = new HashSet<String>(Arrays.asList("-" + bij));
                Set<String> stench = new HashSet<String>(Arrays.asList("-" + sij));
                
                one_wumpus.add(wij);

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
            }
        }
        KB.add(one_wumpus);
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

        return options;
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
        }
        else{
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

        //System.out.println(KB);
        KB = solve.Solver(KB);

        System.out.println(KB);
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
        w.doAction(options.get(move_index));

        

        
        
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
			
			
