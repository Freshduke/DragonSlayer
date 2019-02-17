package huawei;

import java.util.*;

public class Ans{
    ArrayList<Integer> path = new ArrayList<>();
    int cost;
    int start;


    public void setStart(int start_number)
    {
        this.start = start_number;
    }
    public void setCost(int cost_number)
    {
        this.cost = cost_number;
    }
    public int getValue()
    {
        return this.start;
    }
    public void getCost(int[][] G1)
    {
        this.cost = G1[this.start][this.path.get(0)];
        for(int i=0; i<path.size()-1; i++)
        {
            this.cost = this.cost + G1[this.path.get(i)][this.path.get(i+1)];
        }
    }

}
