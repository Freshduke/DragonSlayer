package huawei;

import java.util.ArrayList;

public class priority_queue {
    public P a = new P();
    public ArrayList<P> q;

    public void insert(P P_insert)
    {
        this.q.add(P_insert);
        for(int i = 0;i< this.q.size();i++)
        {
            for(int j=1;j< this.q.size()-i;j++) {
                if (q.get(j)>q.get(j+1)){
                    P temp;
                    temp = q.get(j);
                    q.sort();
                }

            }
        }
    }
}
