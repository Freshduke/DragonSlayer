package huawei;
import java.util.*;

public class PriorityQueue {
    public P a = new P();
    public ArrayList<P> priority_queue;

    public void insert(P P_insert)
    {
        this.priority_queue.add(P_insert);
        priority_queue.sort(new Comparator<P>(){
            @Override
            public int compare(P o1,P o2){
                if(o1.getFirst()>o2.getFirst()){
                    return 1;
                }
                else if(o1.getFirst()==o2.getFirst()){
                    return 0;
                }
                else if(o1.getFirst()<o2.getFirst()){
                    return -1;
                }
                return 0;
            }
        });
    }

    public P gettop(){
        return this.priority_queue.get(0);
    }

    public void removetop(){
        this.priority_queue.remove(0);
    }

    public int length(){
        return this.priority_queue.size();
    }



}
