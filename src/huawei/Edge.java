package huawei;

public class Edge {
    int to;
    int cost;
    public Edge(int to1, int cost1){
        this.to = to1;
        this.cost = cost1;
    }
    public void change_Edge(int to1, int cost1){
        this.to = to1;
        this.cost = cost1;
    }
}
