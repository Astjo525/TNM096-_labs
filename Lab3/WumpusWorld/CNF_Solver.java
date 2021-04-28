import java.util.*;

public class CNF_Solver {
    public static Set<String> remove_minus(Set<String> S)
    {
        ArrayList<String> arrayS = new ArrayList<String>(S);
        for(int i = 0; i < arrayS.size(); i++)
        {
            arrayS.set(i, arrayS.get(i).replace("-", ""));
        }

        Set<String> res = new HashSet<String>(arrayS);

        return res;
    }

    public static Set<String> add_minus(Set<String> S)
    {
        ArrayList<String> arrayS = new ArrayList<String>(S);
        for(int i = 0; i < arrayS.size(); i++)
        {
            arrayS.set(i, "-" + arrayS.get(i));
        }

        Set<String> res = new HashSet<String>(arrayS);

        return res;
    }

    public static ArrayList<Set<String>> split_pos_neg(Set<String> S)
    {
        Set<String> Sp = new HashSet<>(S);
        Sp.removeIf(x -> x.startsWith("-"));

        Set<String> Sn = new HashSet<>(S);
        Sn.removeAll(Sp);
  
        Sn = remove_minus(Sn);
        
        ArrayList<Set<String>> res = new ArrayList<Set<String>>(Arrays.asList(Sp,Sn));
        return res;
    }
    public static Set<String> Resolution(Set<String> A, Set<String> B)
    {
        
        ArrayList<Set<String>> new_A = split_pos_neg(A);
        Set<String> Ap = new_A.get(0);
        Set<String> An = new_A.get(1);
        
        ArrayList<Set<String>> new_B = split_pos_neg(B);
        Set<String> Bp = new_B.get(0);
        Set<String> Bn = new_B.get(1);
        
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

    public static ArrayList<Set<String>> Solver(ArrayList<Set<String>> KB)
    {

        ArrayList<Set<String>> S = new ArrayList<Set<String>>();
        ArrayList<Set<String>> KB_prim = new ArrayList<Set<String>>();

        while(!KB_prim.equals(KB))
        {
            S = new ArrayList<Set<String>>();
            KB_prim = new ArrayList<Set<String>>(KB);

            for(int i = 0; i < KB.size(); i++)
            {
                for(int j = i+1; j < KB.size(); j++)
                {
                    Set<String> C = Resolution(KB.get(i), KB.get(j));

                    if(!C.isEmpty())
                    {
                        if(!S.contains(C))
                        {
                            S.add(C);
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

    public static ArrayList<Set<String>> Incorporate(ArrayList<Set<String>> S, ArrayList<Set<String>> KB)
    {
        for(int i = 0; i < S.size(); i++)
        {
            KB = Incorporate_clause(S.get(i), KB);
        }
        return KB;
    }

    public static ArrayList<Set<String>> Incorporate_clause(Set<String>A, ArrayList<Set<String>> KB)
    {
        ArrayList<Set<String>> new_A = split_pos_neg(A);
        Set<String> Ap = new_A.get(0);
        Set<String> An = new_A.get(1);
        
        for(int i = 0; i < KB.size(); i++)
        {
            Set<String> B = KB.get(i);
            ArrayList<Set<String>> new_B = split_pos_neg(B);
            Set<String> Bp = new_B.get(0);
            Set<String> Bn = new_B.get(1);

            if(Bp.containsAll(Ap) && Bn.containsAll(An) && !B.equals(A))
            {
                KB.remove(i);
                i--;
            }
        }

        for(int i = 0; i < KB.size(); i++)
        {
            Set<String> B = KB.get(i);
            ArrayList<Set<String>> new_B = split_pos_neg(B);
            Set<String> Bp = new_B.get(0);
            Set<String> Bn = new_B.get(1);

            if(Ap.containsAll(Bp) && An.containsAll(Bn))
            {
                return KB;
            }
        }

        KB.add(A);

        return KB;
    }

}
