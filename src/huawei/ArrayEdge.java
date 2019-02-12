package huawei;

import java.util.ArrayList;

public class ArrayEdge {
    public ArrayList<Edge> edeline;

    public ArrayEdge() {
        this.edeline = new ArrayList<>();
    }

    public Edge getelement(int index){
        return this.edeline.get(index);
    }

    public int size()
    {
        return this.edeline.size();
    }

    public void addelement(Edge item){
        this.edeline.add(item);
    }

}
