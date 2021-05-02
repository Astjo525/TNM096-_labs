package CNF_Solver;
import java.util.*;

public class CNF_Solver {
    public static Set<String> remove_minus(Set<String> S)
    {
        Set<String> Sn = new HashSet<String>();
        // for(String item: S)
        // {
        //     item = item.replace("-", "");
        //     System.out.println(item);
        //     Sn.add(item);
        // }

        S.forEach(e -> Sn.add(e.replace("-", "")));

        //Set<String> res = new HashSet<String>(Sn);

        return Sn;
    }

    public static Set<String> add_minus(Set<String> S)
    {
        Set<String> Sn = new HashSet<String>();
        // for(String item: S)
        // {
        //     item = "-" + item;
        // }

        S.forEach(e -> Sn.add("-" + e));

        //Set<String> res = new HashSet<String>(S);

        return Sn;
    }

    public static Set<Set<String>> split_pos_neg(Set<String> S)
    {
        Set<String> Sp = new HashSet<>(S);
        Sp.removeIf(x -> x.startsWith("-"));

        Set<String> Sn = new HashSet<>(S);
        Sn.removeAll(Sp);
  
        Sn = remove_minus(Sn);
        
        Set<Set<String>> res = new HashSet<Set<String>>(Arrays.asList(Sp,Sn));
        return res;
    }

    public static Set<String> split_pos(Set<String> S)
    {
        Set<String> Sp = new HashSet<String>(S);
        Sp.removeIf(x -> x.startsWith("-"));
        return Sp;
    }

    public static Set<String> split_neg(Set<String> S, Set<String> Sp)
    {
        Set<String> Sn = new HashSet<String>(S);

        Sn.removeAll(Sp);
  
        Sn = remove_minus(Sn);

        return Sn;
    }

    public static Set<String> Resolution(Set<String> A, Set<String> B)
    {
        
        Set<String> new_A = new HashSet<String>(A);
        Set<String> Ap = split_pos(new_A);
        Set<String> An = split_neg(new_A, Ap);
        
        //Set<Set<String>> new_B = split_pos_neg(B);
        Set<String> new_B = new HashSet<String>(B);
        Set<String> Bp = split_pos(new_B);
        Set<String> Bn = split_neg(new_B, Bp);
        
        Set<String> intersection_An_Bp = new HashSet<>(An);
        intersection_An_Bp.retainAll(Bp);

        Set<String> intersection_Ap_Bn = new HashSet<>(Ap);
        intersection_Ap_Bn.retainAll(Bn);

        Set<String> C = new HashSet<String>();

        if (intersection_An_Bp.size() == 0 && intersection_Ap_Bn.size() == 0)
        {
            return C;
        }

        if(intersection_Ap_Bn.size() != 0)
        {
            String[] arrayNumbers = intersection_Ap_Bn.toArray(new String[intersection_Ap_Bn.size()]);
            int rand_index = new Random().nextInt(intersection_Ap_Bn.size());
            String el = arrayNumbers[rand_index];
            Ap.remove(el);
            Bn.remove(el);     
        }
        else
        {
            String[] arrayNumbers = intersection_An_Bp.toArray(new String[intersection_An_Bp.size()]);
            int rand_index = new Random().nextInt(intersection_An_Bp.size());
            String el = arrayNumbers[rand_index];
            An.remove(el);
            Bp.remove(el);     
        }
        Set<String> Cn = new HashSet<>(An);
        Cn.addAll(Bn);
        Set<String> Cp = new HashSet<>(Ap);
        Cp.addAll(Bp);

        Set<String> intersection_Cp_Cn = new HashSet<String>(Cp);
        intersection_Cp_Cn.retainAll(Cn);

        if(intersection_Cp_Cn.size() != 0)
        {
            return C;
        }

        Cn = add_minus(Cn);

        C = Cp;
        C.addAll(Cn);

        return C;
    }

    public static Set<Set<String>> Solver(Set<Set<String>> KB)
    {

        Set<Set<String>> S = new HashSet<Set<String>>();
        Set<Set<String>> KB_prim = new HashSet<Set<String>>();
        Set<String> C;

        while(!KB_prim.equals(KB))
        {
            S = new HashSet<Set<String>>();
            KB_prim = new HashSet<Set<String>>(KB);

            for(Set<String> e1: KB)
            {
                for(Set<String> e2: KB)
                {
                    if(e1 != e2) {
                        C = Resolution(e1, e2);
    
                        if(!C.isEmpty())
                        {
                            if(!S.contains(C))
                            {
                                S.add(C);
                            }        
                        }
                    }
                }
                
            }

            if(S.isEmpty())
            {
                return KB;
            }

            KB = Incorporate(S, KB);
        }
        
        return KB;
    }

    public static Set<Set<String>> Incorporate(Set<Set<String>> S, Set<Set<String>> KB)
    {
        for(Set<String> item: S)
        {
            KB = Incorporate_clause(item, KB);
        }
        return KB;
    }

    public static Set<Set<String>> Incorporate_clause(Set<String> A, Set<Set<String>> KB)
    {
        // Set<Set<String>> new_A = split_pos_neg(A);
        // Set<String> Ap = new_A.get(0);
        // Set<String> An = new_A.get(1);

        Set<String> Ap = split_pos(A);
        Set<String> An = split_neg(A, Ap);

        Set<Set<String>> remove = new HashSet<Set<String>>();
        
        for(Set<String> e1: KB)
        {
            // Set<String> B = e1;
            // ArrayList<Set<String>> new_B = split_pos_neg(B);
            // Set<String> Bp = new_B.get(0);
            // Set<String> Bn = new_B.get(1);

            Set<String> Bp = split_pos(e1);
            Set<String> Bn = split_neg(e1, Bp);

            if(Bp.containsAll(Ap) && Bn.containsAll(An) && !e1.equals(A))
            {
                //KB.remove(e1);
                //i--;
                remove.add(e1);
            }
        }
        
        remove.forEach(e -> KB.remove(e));

        for(Set<String> e1: KB)
        {
            //Set<String> B = KB.get(i);
            // Set<Set<String>> new_B = split_pos_neg(e1);
            // Set<String> Bp = new_B.get(0);
            // Set<String> Bn = new_B.get(1);

            Set<String> Bp = split_pos(e1);
            Set<String> Bn = split_neg(e1, Bp);

            if(Ap.containsAll(Bp) && An.containsAll(Bn))
            {
                return KB;
            }
        }

        KB.add(A);

        return KB;
    }

}
