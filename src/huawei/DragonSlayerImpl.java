package huawei;
import java.util.*;
import huawei.exam.*;

/**
 * 实现类
 * 
 * 各方法请按要求返回，考试框架负责报文输出
 */
public class DragonSlayerImpl implements ExamOp
{
	/**
     * ReturnCode(返回码枚举) .S001：重置成功 .S002：设置火焰成功 .S003：设置龙卷风成功 .S004：设置传送阵成功 .E001：非法命令
     * .E002：非法坐标 .E003：非法时间 .E004：操作时间不能小于系统时间 .E005：该区域不能设置元素 .E006：龙卷风数量已达上限
     * .E007：传送阵数量已达上限 .E008：传送阵的入口和出口重叠
     */
    
    /**
     * Area(区域类) int getX()：获取横坐标 void setX(int x)：设置横坐标 int getY()：获取纵坐标 void setY(int
     * y)：设置纵坐标 Element getElement()：获取元素 void setElement(Element element)：设置元素 boolean
     * equals(Object o)：区域横纵坐标相同，则区域相同
     */
    
    /**
     * Element(元素枚举) .NONE：空元素 .HERO：英雄 .DRAGON：恶龙 .FIRE：火焰 .TORNADO：龙卷风 .PORTAL：传送阵
     */
    
    /**
     * Hero(英雄类) Title getTitle()：获取称号 void setTitle(Title title)：设置称号 Status
     * getStatus()：获取状态 void setStatus(Status status)：设置状态 Area getArea()：获取区域
     * setArea(Area area)：设置区域
     */
    
    /**
     * Title(称号枚举) .WARRIOR：勇士 .DRAGON_SLAYER：屠龙者
     */
    
    /**
     * Status(状态枚举) .MARCHING：行进 .WAITING：等待
     */

    
    /**
     * 待考生实现，构造函数
     */
	public static Map map;       //定义地图
	private int sys_time;        //定义系统时间 指令更新
	private boolean istornadoSet;  //定义判断是否设置龙卷风的变量
	private boolean isPortalSet;  //定义判断是否设置龙卷风的变量
	private Hero hero;  //定义英雄
	private char in_tornado_count=3;  //记录龙卷风中的等待时间
	private int[][] path_sequence=new int[200][2];           //英雄最短路径
	private int[][] path_sequence_with_time=new int[200][3];     //最优英雄位置序列。
	private static int[][] temp_map = new int[16][16];              //游戏网格
	private static ArrayList<Edge> G4[] = new ArrayList[256];       
	private static ArrayList<Edge> G[] = new ArrayList[256];         
	private static int[][] Gmat = new int[256][256];   //注意这里面使用999来代表无穷大。
	private static boolean[] vis = new boolean[256];
	private static ArrayList<P> q = new ArrayList<P>();
	private static ArrayList<Ans> paths = new ArrayList<Ans>();   //路径序列集合

	//比较不同情况下的总路径长度 并且将最优路径赋值到path_sequence
	public void ComparePath(){
		for(int i=0;i<256;i++){
			G[i]=new ArrayList<Edge>();
			G4[i]=new ArrayList<Edge>();
		}
		int[][] p_no_portal = new int[200][2];          //不经过传送门的最优路径
		int length_p_no_portal=50;                      //不经过传送门的最优路径的序列长度
		int[][] p_portal_entran = new int[200][2];      //以传送门入口为终点的最优路径
		int length_p_portal_entran=50;                  //以传送门入口为终点的最优路径的序列长度
		int[][] p_portal_exit = new int[200][2];        //以传送门出口为起点的最优路径
		int length_p_portal_exit=200;                   //以传送门出口为起点的最优路径的序列长度
		int portal_entrance_x=0;                        //传送门入口x坐标
		int portal_entrance_y=0;                        //传送门入口y坐标
		int portal_exit_x=20;                           //传送门出口x坐标
		int portal_exit_y=20;                           //传送门出口y坐标
		int FIRE_PORTAL_EXIT_x=20;                      //为火焰的传送门出口x坐标
		int FIRE_PORTAL_EXIT_y=20;                      //为火焰的传送门出口y坐标


		//初始化最优路径序列
		for(int i=0 ; i<200; i++)
		{
			path_sequence[i][0] = 999;
			path_sequence[i][1] = 999;
		}
		
	    // 不经过传送门可以经过龙卷风的最优路径。
		p_no_portal = p_cmp_tornado(hero.getArea().getX(), hero.getArea().getY() , 15 , 15 ,1);
		length_p_no_portal = detect_sequence_length(p_no_portal);

		//判断地图中有无传送门，若没有传送门（出口值不更新）or 传送门出口处有火焰则无需计算经过传送门的最优路径。
		for(int i = 0; i< 16;i++)
		{
			for(int j =0 ;j < 16;j++) 
			{
				if(map.table[i][j].element == MyElement.PORTAL_EXIT || map.table[i][j].element == MyElement.HERO_PORTAL_EXIT  || map.table[i][j].element == MyElement.TORNADO_PORTAL_EXIT || map.table[i][j].element == MyElement.HERO_TORNADO_PORTAL_EXIT)
				{
					portal_exit_x = i;
					portal_exit_y = j;
					break;
				}
			}
		}
		for(int i = 0; i< 16;i++)
		{
			for(int j =0 ;j < 16;j++) 
			{
				if(map.table[i][j].element == MyElement.FIRE_PORTAL_EXIT)			
				{
					FIRE_PORTAL_EXIT_x = i;
					FIRE_PORTAL_EXIT_y = j;
					break;
				}
			}
		}


		
		if((FIRE_PORTAL_EXIT_x !=20)||(portal_exit_x ==20)) {
			path_sequence = p_no_portal;
		}
		else 
		{
			//计算经过传送门的最优路径
			for(int i = 0; i< 16;i++)
			{
				for(int j =0 ;j < 16;j++)
				{
					if(map.table[i][j].element == MyElement.PORTAL_ENTRANCE)
					{
						portal_entrance_x = i;
						portal_entrance_y = j;
						break;
					}
				}
			}

			p_portal_entran = p_cmp_tornado(hero.getArea().getX(), hero.getArea().getY() , portal_entrance_x , portal_entrance_y , 0);
			p_portal_exit   = p_cmp_tornado(portal_exit_x,portal_exit_y ,15 , 15 , 1);
			length_p_portal_entran = detect_sequence_length(p_portal_entran);
			length_p_portal_exit   = detect_sequence_length(p_portal_exit);

			
			//判断英雄是否需要使用传送门作为中继来缩短路径
			if((length_p_portal_exit+length_p_portal_entran) > length_p_no_portal)
			{
				path_sequence =p_no_portal;
			}
			else 
			{
				int k=0;
				for(int i=1; i< 200; i++) {
					if(p_portal_entran[i][0] == 999)
					{
						p_portal_entran[i-1][0] = p_portal_exit[k][0];
						p_portal_entran[i-1][1] = p_portal_exit[k][1];
						k++;
					}
				}
				path_sequence =p_portal_entran;
			}
		}
	}
	
	
	// 对比经过或不经过龙卷风下的最优路径长度，返回最优路径序列
	public static int[][] p_cmp_tornado(int depart_x,int depart_y,int destin_x, int destin_y,int status_portal_entran){
		int[][] p_tornado_access = new int[200][2];    //记录龙卷风可达时的最短路径序列
		int[][] p_tornado_inaccess = new int[200][2];  //记录龙卷风不可达时的最短路径序列
		int length_p_tornado_access=0;                 //记录龙卷风可达时的最短路径序列长度
		int length_p_tornado_inaccess=0;               //记录龙卷风不可达时的最短路径序列长度
		int tornado_x=99;                             //龙卷风的x坐标
		int tornado_y=99;                             //龙卷风的x坐标
		boolean is_tornado_passed=false;              //标记是否经过龙卷风
		
		//build temp_map
		for(int i=0;i<16;i++) {
			for(int j=0;j<16;j++) {
				if(map.table[i][j].element == MyElement.FIRE) {
					temp_map[i][j]=1;
				}
				else if(map.table[i][j].element == MyElement.PORTAL_ENTRANCE) {
					temp_map[i][j]=status_portal_entran;
			    }
				else if(map.table[i][j].element == MyElement.TORNADO_PORTAL_EXIT) {
					temp_map[i][j]=0;
					tornado_x = i;
					tornado_y = j;
				}
				else if(map.table[i][j].element == MyElement.HERO_TORNADO) {
					temp_map[i][j]=0;
					tornado_x = i;
					tornado_y = j;
				}
				else if(map.table[i][j].element == MyElement.HERO_PORTAL_EXIT) {
					temp_map[i][j]=0;
				}
				else if(map.table[i][j].element == MyElement.PORTAL_EXIT) {
					temp_map[i][j]=0;
				}
				else if(map.table[i][j].element == MyElement.FIRE_PORTAL_EXIT) {
					temp_map[i][j]=1;	
				}
				else if(map.table[i][j].element == MyElement.HERO_TORNADO_PORTAL_EXIT) {
					temp_map[i][j]=0;
				}
				else if(map.table[i][j].element == MyElement.TORNADO) {
					temp_map[i][j]=0;
					tornado_x = i;
					tornado_y = j;
				}else{
					temp_map[i][j] = 0;
				}
			}
		 }
		
		//龙卷风可达时
		 p_tornado_access = findpath(depart_x,depart_y,destin_x,destin_y); //寻找最优路径
        //记录龙卷风可达时的路径
		 for(int i=0; i<99;i++) {
			if(p_tornado_access[i][0] == tornado_x && p_tornado_access[i][1] == tornado_y) {
				is_tornado_passed=true;
				break;
			}
		 }
		 
		 if(is_tornado_passed==true){
			 length_p_tornado_access = detect_sequence_length(p_tornado_access)+2;
			 for(int i=0;i<16;i++) {
					for(int j=0;j<16;j++) {
						if(map.table[i][j].element == MyElement.TORNADO) {
							temp_map[i][j]=1;
						}
						else if(map.table[i][j].element == MyElement.TORNADO_PORTAL_EXIT) {
							temp_map[i][j] = 1;
						}
					}
			 }
				 p_tornado_inaccess = findpath(depart_x,depart_y,destin_x,destin_y);
				 length_p_tornado_inaccess =detect_sequence_length(p_tornado_inaccess) ;
				 
				 //比较龙卷风可达和不可达的路经长，返回最优
				 if(length_p_tornado_access > length_p_tornado_inaccess ) {
				 	p_tornado_access = p_tornado_inaccess;
				 }
		 }
		 
		
		 //龙卷风不可达
		 return p_tornado_access;
	}
	
	
	
	//检测最优路径序列长度，返回长度
	public static int detect_sequence_length(int[][] array) {
		int k;
		if(array[0][1]== 999 && array[0][0]== 999)
		{
			return array[99][0];     //返回可达的多条路径的长度
		}
		for(k =1; k<200;k++) {
			if(array[k][1]== 999 && array[k][0]== 999) {
				return k;            //返回可达唯一路径长度
			}
		}
		return 200;                  
	}


    //寻找两点间的最优路径
	public static int[][] findpath(int depart_x, int depart_y, int destin_x, int destin_y){

		
		//中间标志矩阵
		for(int i = 0; i<256; i++){
			vis[i] = false;                 
		}
        
		//初始化最短路径序列数组
		int[][] path_seq = new int[200][2];  
		for(int i= 0; i< 200 ; i++)
		{
			path_seq[i][0]=999;
			path_seq[i][1]=999;
		}

		//初始化地图
		for(int i=0;i<256;i++)               
			for(int j=i;j<256;j++)
			{
				Gmat[i][j]=9;
				Gmat[j][i]=9;
			}

        
		//将16*16的方格转化为256个点的邻接矩阵
		for(int i=0;i<16;i++)                
		{
			for(int j=0;j<16;j++)
			{
				if(temp_map[i][j]==0) {
					if((i-1>=0)&&(j-1>=0)){
						if (temp_map[i - 1][j - 1] == 0) {
							Gmat[(i - 1) * 16 + j - 1][i * 16 + j] = 1;
							Gmat[i * 16 + j][(i - 1) * 16 + j - 1] = 1;
						}
					}
					if(i-1>=0){
						if (temp_map[i - 1][j] == 0) {
							Gmat[(i - 1) * 16 + j][i * 16 + j] = 1;
							Gmat[i * 16 + j][(i - 1) * 16 + j] = 1;
						}
					}
					if((i-1>=0)&&(j+1<16)){
						if (temp_map[i - 1][j + 1] == 0) {
							Gmat[(i - 1) * 16 + j + 1][i * 16 + j] = 1;
							Gmat[i * 16 + j][(i - 1) * 16 + j + 1] = 1;
						}
					}
					if(j-1>=0){
						if (temp_map[i][j - 1] == 0) {
							Gmat[(i) * 16 + j - 1][i * 16 + j] = 1;
							Gmat[i * 16 + j][i * 16 + j - 1] = 1;
						}
					}
					if(j+1<16){
						if (temp_map[i][j + 1] == 0) {
							Gmat[i * 16 + j + 1][i * 16 + j] = 1;
							Gmat[i * 16 + j][i * 16 + j + 1] = 1;
						}
					}


					if((i+1<16)&&(j-1>=0)){
						if (temp_map[i + 1][j - 1] == 0) {
							Gmat[(i + 1) * 16 + j - 1][i * 16 + j] = 1;
							Gmat[i * 16 + j][(i + 1) * 16 + j - 1] = 1;
						}
					}
					if(i+1<16){
						if (temp_map[i + 1][j] == 0) {
							Gmat[(i + 1) * 16 + j][i * 16 + j] = 1;
							Gmat[i * 16 + j][(i + 1) * 16 + j] = 1;
						}
					}
					if((i+1<16)&&(j+1<16)){
						if (temp_map[i + 1][j + 1] == 0) {
							Gmat[(i + 1) * 16 + j + 1][i * 16 + j] = 1;
							Gmat[i * 16 + j][(i + 1) * 16 + j + 1] = 1;
						}
					}
				}
			}
		}

		
		//清空G
		for(int i = 0;i<256;i++)
		{
			G[i].clear();
		}


		for (int i = 0; i<256;i++)
		{
			for(int j=0; j<256; j++)
			{
				if(Gmat[i][j] == 1) {
					Edge G_element = new Edge(j, 1);
					G[i].add(G_element);
				}
			}
		}


		dijkstra(depart_x*16+depart_y);

		Ans ans = new Ans();
		dfs(depart_x*16+depart_y, destin_x*16+destin_y, ans, depart_x*16+depart_y);
		
		//返回唯一最短路径
		if(paths.size()==1)
		{
			int NodeNum=1;
			path_seq[0][0] = depart_x;
			path_seq[0][1] = depart_y;
			for(int j=0; j<paths.get(0).path.size(); j++)  //鍙湁涓�鏉℃槸锛宲aths浠呮湁涓�鏉¤矾寰勶紝鍗充粎鏈変竴琛屽厓绱狅紝鏁呭彇paths[0]
			{
				path_seq[NodeNum][0] = paths.get(0).path.get(j) / 16 ;
				path_seq[NodeNum][1] = paths.get(0).path.get(j) % 16 ;
				NodeNum++;
			}
		}else if(paths.size()>1){
			int NodeNum=1;
			for(int j=0; j<paths.get(0).path.size(); j++)  //鍙湁涓�鏉℃槸锛宲aths浠呮湁涓�鏉¤矾寰勶紝鍗充粎鏈変竴琛屽厓绱狅紝鏁呭彇paths[0]
			{
				NodeNum++;
			}
			path_seq[99][0]=NodeNum;//若多条最短路径，用以记录其最短长度
		}
		paths.clear();
		return path_seq;
	}


	//dfs寻找最短路径
	public static void dfs(int s, int t, Ans A,  int start)
	{
		if (s == t)
		{
			A.start = start;
			A.getCost(Gmat);
			Ans A2 = new Ans();
			A2.setStart(A.getValue());
			for (int i=0; i<A.path.size();i++)
			{
				A2.path.add(A.path.get(i));
			}
			A2.getCost(Gmat);
			paths.add(A2);
		}

		for (int i = 0; i < G4[s].size(); i++)
		{
			int u = G4[s].get(i).to;

			if (!vis[u])
			{
				vis[u] = true;
				A.path.add(u);
				dfs(u, t, A, start);
				A.path.remove(A.path.size()-1);
				vis[u] = false;
			}
		}
	}


	//基于dijkstra算法，寻找各点间的最短路径
	public static void dijkstra(int s)
	{

		// 清空G4
		for(int i = 0;i<256;i++)
		{
			G4[i].clear();
		}

		//Vector dist = new Vector();
		int [] dist = new int[256];
		boolean condition_if = true;
		for(int i = 0; i<256 ; i++)
		{
			dist[i] = 9999;
		}

		dist[s] = 0;
		P P_initial = new P();
		P_initial.setFirst(0);
		P_initial.setSecond(s);
		q.add(P_initial);
		sortingQueue();
		if(q.size() == 0){
			condition_if = false;
		}
		while(condition_if)
		{
			P p = q.get(0);   //从尚未使用的顶点中找到一个距离最小的顶点
			q.remove(0);
			sortingQueue();
			int v = p.second;
			if(dist[v] < p.first)
				continue;
			for(int i=0; i<G[v].size(); i++)
			{
				Edge e = G[v].get(i);
				int dis = dist[v] + e.cost;
				if(dist[e.to] > dis)
				{
					dist[e.to] = dist[v] + e.cost;
					P P_insert = new P();
					P_insert.setFirst(dist[e.to]);
					P_insert.setSecond(e.to);
					q.add( P_insert );
					sortingQueue();
					G4[v].add(e);
				}
				else if(dist[e.to] == dis)
				{
					G4[v].add(e);
				}
			}
			if(q.size() == 0){
				condition_if = false;
			}
		}
	}

	
	//将优先队列按从小到大进行排序  
	public static void sortingQueue() {
		for (int i=0;i< q.size();i++ )
		{
			q.sort(new Comparator<P>(){

				public int compare(P o1,P o2){
					if(o1.getFirst()>o2.getFirst()){
						return 1;
					}
					else if(o1.getFirst()==o2.getFirst()){
						if(o1.getSecond()>o2.getSecond())
							return 1;
						else if(o1.getSecond()<o2.getSecond())
							return -1;
						else
							return 0;
					}
					else if(o1.getFirst()<o2.getFirst()){
						return -1;
					}
					return 0;
				}
			});
		}
	}
	
	
	
	
	
	//更新英雄称号&行进状态
	public void updateHero(){
		
		int x_before=0;   //记录英雄上一时刻的x坐标
		int y_before=0;   //记录英雄上一时刻的y坐标
		int x_next=0;     //记录英雄下一时刻的x坐标
		int y_next=0;     //记录英雄下一时刻的y坐标
		int i=0;
		
		//求取英雄上一时刻的坐标
		for(i =0; i<200;i++) {
			if(path_sequence_with_time[i][2]==sys_time) {
				x_before=path_sequence_with_time[i][0];
				y_before=path_sequence_with_time[i][1];
				break;
			}
		}
		
		//对英雄可能的状态进行判断 更新其位置
		if(this.in_tornado_count==3){
			x_next=path_sequence_with_time[i+1][0];
			y_next=path_sequence_with_time[i+1][1];
			if(this.map.table[x_before][y_before].element == MyElement.HERO_PORTAL_EXIT)
			{
				this.map.table[x_before][y_before].element = MyElement.PORTAL_EXIT;
			}
			else if(this.map.table[x_before][y_before].element == MyElement.HERO_TORNADO)
			{
				this.map.table[x_before][y_before].element = MyElement.TORNADO;
			}
			else if(this.map.table[x_before][y_before].element == MyElement.HERO_TORNADO_PORTAL_EXIT)
			{
				this.map.table[x_before][y_before].element = MyElement.TORNADO_PORTAL_EXIT;
			}
			else {
				this.map.table[x_before][y_before].element = MyElement.NONE;
			}
			
			hero.setArea(new Area(x_next,y_next));
			
			
			if(this.map.table[x_next][y_next].element == MyElement.PORTAL_EXIT)
			{
				this.map.table[x_next][y_next].element = MyElement.HERO_PORTAL_EXIT;
			}
			else if((this.map.table[x_next][y_next].element == MyElement.TORNADO)||(this.map.table[x_next][y_next].element == MyElement.TORNADO_PORTAL_EXIT))
			{
				this.in_tornado_count=1;
				this.map.table[x_next][y_next].element = MyElement.HERO_TORNADO;
			}
			else if(this.map.table[x_next][y_next].element == MyElement.TORNADO_PORTAL_EXIT)
			{
				this.map.table[x_next][y_next].element = MyElement.HERO_TORNADO_PORTAL_EXIT;
			}
			else if(this.map.table[x_next][y_next].element == MyElement.HERO_TORNADO)
			{
				;
			}else if(this.map.table[x_next][y_next].element == MyElement.DRAGON)
			 ;
			else{
				this.map.table[x_next][y_next].element = MyElement.HERO;
			}
		}else{
				this.in_tornado_count++;
			
		}
			
        //判断游戏是否结束 更新英雄的称号
		if(hero.getArea().getX() == 15 && hero.getArea().getY()==15)
		{
			hero.setTitle(Title.DRAGON_SLAYER);
		}

	}
	
	
	
	
	
	
	//更新系统时间&英雄位置&称号&行进状态总函数。
    public void update(int time)
    {
    	ComparePath();  //更新最优路径

    	int non_zero_row_index=0;
    	
		int column3_cnt = sys_time;	
		
		
		if(path_sequence[0][0]==999 && path_sequence[0][1]==999){//无唯一最短路径时
			hero.setStatus(Status.WAITING);
			int hero_x=this.hero.getArea().getX();
			int hero_y=this.hero.getArea().getY();
			if((this.map.table[hero_x][hero_y].element==MyElement.HERO_TORNADO)||(this.map.table[hero_x][hero_y].element==MyElement.HERO_TORNADO_PORTAL_EXIT)){
				this.in_tornado_count=1;
			}
		}
	    else //存在最短路径时
	    {
			hero.setStatus(Status.MARCHING);  //更新行进状态
			
			//随时间更新最短路径序列
			for(int i =0;i<200;i++){
				if(path_sequence[i][1]!=999) {
					path_sequence_with_time[i][0] = path_sequence[i][0];
					path_sequence_with_time[i][1] = path_sequence[i][1];
					path_sequence_with_time[i][2] = column3_cnt;
					non_zero_row_index = i;
				}
				else
				{
					path_sequence_with_time[i][0] = path_sequence[non_zero_row_index][0];
					path_sequence_with_time[i][1] = path_sequence[non_zero_row_index][1];
					path_sequence_with_time[i][2] = column3_cnt;
				}
				column3_cnt++;
			}
			
			//判断是否需要更新英雄各种状态
			int delta_time=time-this.sys_time;
			for(int j=0;j<delta_time;j++){
				updateHero();
				this.sys_time++;
			}
	    }
		this.sys_time=time;
    }


    public DragonSlayerImpl()
    {
    	this.map=new Map();
    	this.sys_time=0;
    	this.isPortalSet=false;
    	this.istornadoSet=false;
    	this.in_tornado_count=3;
    	this.hero=new Hero(Title.WARRIOR,Status.MARCHING,new Area(0,0));
    	this.istornadoSet=false;
    	this.isPortalSet=false;
    }
    
    /**
     * 待考生实现，系统重置
     * 
     * @return 返回码
     */
    @Override
    public OpResult reset() //重置游戏
    {
    	this.map=new Map();
    	this.sys_time=0;
    	this.isPortalSet=false;
    	this.istornadoSet=false;
    	this.hero=new Hero(Title.WARRIOR,Status.MARCHING,new Area(0,0));
    	this.istornadoSet=false;
    	this.isPortalSet=false;
    	this.in_tornado_count=3;
    	for(int i= 0; i<16;i++)
		{
			for(int j =0;j<16;j++)
			{
				temp_map[i][j] = 0;
			}
		}

    	
       	return new OpResult(ReturnCode.S001);        
    }
    
    /**
     * 待考生实现，设置火焰
     * 
     * @param area 设置区域
     * @param time 设置时间
     * @return 返回码
     */
    @Override
    public OpResult setFire(Area area, int time)   //设置火焰
    {    	
    	if(sys_time<=time)
    	{
			if(this.hero.getTitle()==Title.WARRIOR){
    		if(sys_time<time){
    			update(time);
    		}}
    		int x=area.getX();
        	int y=area.getY();
        	char flag=isCollision(x,y);
    		if(flag==1)
    		{
    			this.map.setMap(x, y, MyElement.FIRE);
    			return new OpResult(ReturnCode.S002);
    		}else if(flag==2){
    			this.map.setMap(x, y, MyElement.FIRE_PORTAL_EXIT);
    			return new OpResult(ReturnCode.S002);
    		}else{
    			return new OpResult(ReturnCode.E005);
    		}
    	}
    	else{
    		return new OpResult(ReturnCode.E004);
    	}
    }
    
    
    /**
     * 待考生实现，设置龙卷风
     * 
     * @param area 设置区域
     * @param time 设置时间
     * @return 返回码
     */
    @Override
    public OpResult setTornado(Area area, int time)  //设置龙卷风
    {	
    	if(sys_time<=time)
    	{
			if(this.hero.getTitle()==Title.WARRIOR){
    		if(sys_time<time){
    			update(time);
    		}}
    		int x=area.getX();
        	int y=area.getY();
        	char flag=isCollision(x,y);
    		if(flag==0)
    		{
    			return new OpResult(ReturnCode.E005);
    			
    		}else{
    			if(this.istornadoSet){
    				return new OpResult(ReturnCode.E006);
    			}else{
    				if(flag==1)
    	    		{
    	    			this.map.setMap(x, y, MyElement.TORNADO);
    	    		}else if(flag==2){
    	    			this.map.setMap(x, y, MyElement.TORNADO_PORTAL_EXIT);
    	    		}
    				this.istornadoSet=true;
	    			return new OpResult(ReturnCode.S002);
    			}
    		}
    	}else{
    		return new OpResult(ReturnCode.E004);
    	}
    }
    
    /**
     * 待考生实现，设置传送阵
     * 
     * @param entry 入口区域
     * @param exit 出口区域
     * @param time 设置时间
     * @return 返回码
     */
    @Override
    public OpResult setPortal(Area entry, Area exit, int time)  //设置传送门
    {
    	if(sys_time<=time)
    	{
			if(this.hero.getTitle()==Title.WARRIOR){
    		if(sys_time<time){
    			update(time);
    		}}

        	int entry_x=entry.getX();
        	int entry_y=entry.getY();
        	int exit_x=exit.getX();
        	int exit_y=exit.getY();
        	char flag=isCollision(entry_x,entry_y,exit_x,exit_y);
    		if(flag==0)
    		{
    			return new OpResult(ReturnCode.E005);    			
    		}else{
    			if(this.isPortalSet){
    				return new OpResult(ReturnCode.E007);
    			}else{
    				if(flag==3){
    					return new OpResult(ReturnCode.E008);
    				}else{
        	    		this.map.setMap(entry_x, entry_y, MyElement.PORTAL_ENTRANCE);
        	    		this.map.setMap(exit_x, exit_y, MyElement.PORTAL_EXIT);
    	    			return new OpResult(ReturnCode.S002);
    				}
    			}
    		}
    	}else{
    		return new OpResult(ReturnCode.E004);
    	}
    }
    
    /**
     * 待考生实现，查询
     * 
     * @param time 查询时间
     * @return 英雄信息
     */
    @Override
    public OpResult query(int time)  //查询操作
    {
        if(time>=this.sys_time)
    	{
    		if(this.hero.getTitle()==Title.WARRIOR){
				if(time>this.sys_time){
					update(time);
				}
    		}
    		return new OpResult(this.hero);
    	}else{
    		return new OpResult(ReturnCode.E004);	
    	}
    }
    
    //用于判断设置元素时的冲突（一个坐标）
    public char isCollision(int x,int y)
    {
    	char flag=1;  //    1:NONE 2:PORTAL_EXIT 3:OTHERS
    	if(this.map.table[x][y].element==MyElement.NONE)
    	{
    		flag=1;
    	}else if(this.map.table[x][y].element==MyElement.PORTAL_EXIT){
    		flag=2;
    	}else{
    		flag=0;
    	}
    	return flag;
    }
    
    //用于判断设置元素时的冲突（两个坐标）
    public char isCollision(int entry_x,int entry_y,int exit_x, int exit_y){
    	char flag=1;//flag 取值意义为  1:NONE 2:PORTAL_EXIT 3:entry is the same as exit 0:OTHERS
    	if((this.map.table[entry_x][entry_y].element==MyElement.NONE)&&(this.map.table[exit_x][exit_y].element==MyElement.NONE))
    	{
    		flag=1;
    	}else if((this.map.table[entry_x][entry_y].element==MyElement.NONE)&&(this.map.table[exit_x][exit_y].element==MyElement.PORTAL_EXIT)){
    		flag=2;
    	}else if((this.map.table[entry_x][entry_y].element==MyElement.PORTAL_EXIT)&&(this.map.table[exit_x][exit_y].element==MyElement.NONE)){
    		flag=2;
    	}else if((entry_x==exit_x)&&(entry_y==exit_y)){
    		flag=3;
    	}else{
    		flag=0;
    	}
    	return flag;
    }
}