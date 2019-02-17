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
	public static Map map;
	private int sys_time;
	private boolean isTurnadoSet;
	private boolean isPortalSet;
	private Hero hero;
	private int[][] path_sequence=new int[100][2];
	private int[][] path_sequence_with_time=new int[100][3];     //最优英雄位置序列。
	private static int[][] temp_map = new int[16][16];
	private static ArrayList<Edge> G4[] = new ArrayList[256];
	private static ArrayList<Edge> G[] = new ArrayList[256];
	private static int[][] Gmat = new int[256][256]; //注意这里面使用999来代表无穷大。
	private static boolean[] vis = new boolean[256];
	private static ArrayList<P> q = new ArrayList<>();
	private static ArrayList<Ans> paths = new ArrayList<>();

			//比较不同case下的总路径长度，并且将最优路径赋值到path_sequence中。
	public void ComparePath(){
		int[][] p_no_portal = new int[100][2];          //不经过传送门的最优路径
		int length_p_no_portal=50;                      //不经过传送门的最优路径的序列长度
		int[][] p_portal_entran = new int[100][2];      //以传送门入口为终点的最优路径
		int length_p_portal_entran=50;                  //以传送门入口为终点的最优路径的序列长度
		int[][] p_portal_exit = new int[100][2];        //以传送门出口为起点的最优路径
		int length_p_portal_exit=100;                   //以传送门出口为起点的最优路径的序列长度
		int portal_entrance_x=0;
		int portal_entrance_y=0;
		int portal_exit_x=20;
		int portal_exit_y=20;
		int FIRE_PORTAL_EXIT_x=20;
		int FIRE_PORTAL_EXIT_y=20;


		for(int i=0 ; i<100; i++)
		{
			path_sequence[i][0] = 999;
			path_sequence[i][1] = 999;
		}
		
	    // 不经过传送门可以经过龙卷风的最优路径。
		p_no_portal = p_cmp_tornado(hero.getArea().getX(), hero.getArea().getY() , 15 , 15 ,1);
		length_p_no_portal = detech_sequence_length(p_no_portal);
		
		//判断地图中有无传送门，若没有传送门（出口值不更新）or 传送门出口处有火焰则无需计算经过传送门的最优路径。
		for(int i = 0; i< 16;i++)
		{
			for(int j =0 ;j < 16;j++) 
			{
				if(map.table[i][j].element == MyElement.PORTAL_EXIT || map.table[i][j].element == MyElement.HERO_PORTAL_EXIT  || map.table[i][j].element == MyElement.TORNADO_PORTAL_EXIT)
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
		if((FIRE_PORTAL_EXIT_x !=20 && FIRE_PORTAL_EXIT_y !=20)||(portal_exit_x ==20 && portal_exit_y ==20))
		{
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
			
			length_p_portal_entran = detech_sequence_length(p_portal_entran);
			length_p_portal_exit   = detech_sequence_length(p_portal_exit);
			if((length_p_portal_exit+length_p_portal_entran) > length_p_no_portal)
			{
				path_sequence =p_no_portal;
			}
			else 
			{
				int k=0;
				for(int i=1; i< 100; i++) {
					if(p_portal_entran[i][0] == 999)
					{
						p_portal_entran[i][0] = p_portal_exit[k][0];
						p_portal_entran[i][1] = p_portal_exit[k][1];
						k++;
					}
				}
				path_sequence =p_portal_entran;
			}
		}
	}
	
	
	// 对比经过或不经过龙卷风下的最优路径长度，返回最优路径序列
	public static int[][] p_cmp_tornado(int depart_x,int depart_y,int destin_x, int destin_y,int status_portal_entran){
		int[][] p_tornado_access = new int[100][2];
		int[][] p_tornado_inaccess = new int[100][2];
		int length_p_tornado_access=0;
		int length_p_tornado_inaccess=0;
		int tornado_x=0;
		int tornado_y=0;
		
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
				else if(map.table[i][j].element == MyElement.FIRE_PORTAL_EXIT) {
					temp_map[i][j]=1;	
				}	
				else if(map.table[i][j].element == MyElement.TORNADO) {
					temp_map[i][j]=0;
					tornado_x = i;
					tornado_y = j;
				}
			}
		 }
		 p_tornado_access = findpath(depart_x,depart_y,destin_x,destin_y);
		 for(int i=0; i<100;i++) {
			if(p_tornado_access[i][0] == tornado_x && p_tornado_access[i][1] == tornado_y) {
				for (int j = 100; j>i; j--) {
					p_tornado_access[j][0]=p_tornado_access[j-1][0];
					p_tornado_access[j][1]=p_tornado_access[j-1][1];
				}
				for (int j = 100; j>i+1; j--) {
					p_tornado_access[j][0]=p_tornado_access[j-1][0];
					p_tornado_access[j][1]=p_tornado_access[j-1][1];
				}
				p_tornado_access[i][0]=tornado_x;
				p_tornado_access[i][1]=tornado_y;
				p_tornado_access[i+1][0]=tornado_x;
				p_tornado_access[i+1][1]=tornado_y;
			}
		 }
		 length_p_tornado_access = detech_sequence_length(p_tornado_access);
		
		 
		 for(int i=0;i<16;i++) {
			for(int j=0;j<16;j++) {
				if(map.table[i][j].element == MyElement.TORNADO) {
					temp_map[i][j]=1;
				}
				else if(map.table[i][j].element == MyElement.TORNADO_PORTAL_EXIT) {
					temp_map[i][j]=1;
				}
			}
		 }		
		 p_tornado_inaccess = findpath(depart_x,depart_y,destin_x,destin_y);
		 length_p_tornado_inaccess =detech_sequence_length(p_tornado_inaccess) ;
		 if(length_p_tornado_access > length_p_tornado_inaccess ) {
		 	p_tornado_access = p_tornado_inaccess;
		 }
		
		 return p_tornado_access;
	}
	
	
	
	//检测最优路径序列长度，返回长度
	public static int detech_sequence_length(int[][] array) {
		int k;
		for(k =0; k<100;k++) {
			if(array[k][1]== 999 && array[k][0]== 999) {  //###############################################################!!!!!
				return k;
			}
		}
		return 100;
	}


	//缁欏畾鍦板浘璧风偣&璧风偣&缁堢偣&杩斿洖鏈�浼樿矾寰勫簭鍒�
	public static int[][] findpath(int depart_x, int depart_y, int destin_x, int destin_y){


		//鍏ㄥ眬鍙橀噺锛歩nt[16][16] temp_map璁板綍鐫�褰撳墠鍦板浘鐨勪俊鎭細1浠ｈ〃涓嶅彲缁忚繃锛�0浠ｈ〃鍙粡杩囥��
		//璋冪敤绀轰緥锛�
		/*if(temp_map[1][2] == 1) {
		 blablabla;
		}*/

		//杈撳叆锛歵emp_map[depart_x,depart_y]
		//(depart_x,depart_y)鏄捣鐐癸紝
		//temp_map[destin_x,destin_y]
		//(destin_x,destin_y)鏄粓鐐�
		for(int i = 0; i<256; i++){
			vis[i] = false;
		}
		//杩斿洖鐨勫彉閲�
		int[][] path_seq = new int[100][2];   //鑷姩鍒濆鍖栫殑鏁板�煎氨鏄�0锛岀洿鎺ュ簭鍒椾粠涓婂線涓嬪啓灏辫

		for(int i= 0; i< 100 ; i++)
		{
			path_seq[i][0]=999;
			path_seq[i][1]=999;
		}
/* 绗竴鍒�	绗簩鍒� 杩斿洖鍊间妇渚嬶紙绗竴鍒楁í鍧愭爣锛岀浜屽垪绾靛潗鏍囷級
		1   1
		2   2
		3   3
		4   4
		5   5
		0   0
		......
		0   0
		*/
		//#######################################################################鍚戣悓鏂藉睍鎵嶅崕鐨勫湴鏂�
		//寤虹珛閭绘帴鐭╅樀Gmat

		for(int i=0;i<256;i++)
			for(int j=i;j<256;j++)
			{
				Gmat[i][j]=9;
				Gmat[j][i]=9;
			}


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


		dijkstra(0,G);

		Ans ans = new Ans();
		dfs(depart_x*16+depart_y, destin_x*16+destin_y, ans, depart_x*16+depart_y);
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
		}

		return path_seq;
	}


	public static void dfs(int s, int t, Ans A,  int start)
	{
		if (s == t)
		{
			System.out.println("haha");
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



	public static void dijkstra(int s,ArrayList<Edge>[] G)
	{

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
			P p = q.get(0);   //浠庡皻鏈娇鐢ㄧ殑椤剁偣涓壘鍒颁竴涓窛绂绘渶灏忕殑椤剁偣
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
	public void updateHero(int time){
		for(int i =0; i<100;i++) {
			if(path_sequence_with_time[i][2]==time) {
				hero.setArea(new Area(path_sequence_with_time[i][0],path_sequence_with_time[i][1]));
				hero.setStatus(Status.MARCHING);
				sys_time = time;
			}
		}
		if(hero.getArea().getX() == 15 && hero.getArea().getY()==15)
		{
			hero.setTitle(Title.DRAGON_SLAYER);
		}
	}
	
	//更新系统时间&英雄位置&称号&行进状态总函数。
    public void update(int time)
    {
    	ComparePath();
    	
    	int non_zero_row_index=0;
    	
		int column3_cnt = sys_time;			
	    if(path_sequence[1][0]==hero.getArea().getX() && path_sequence[1][1]==hero.getArea().getX()){
	    	hero.setStatus(Status.WAITING);
			sys_time = time;
		}
	    else
	    {
			hero.setStatus(Status.MARCHING);
			for(int i=0;i<2;i++) {
				path_sequence_with_time[0][i] = path_sequence[0][i];
			}
			for(int i =1;i<100;i++){
				if(path_sequence[i][1]!=999) {  //########################################################！！！！
					path_sequence_with_time[i][0] = path_sequence[i][0];
					path_sequence_with_time[i][1] = path_sequence[i][1];
					path_sequence_with_time[i][2] = column3_cnt;
					column3_cnt++;
					non_zero_row_index = i;
				}
				else
				{
					path_sequence_with_time[i][0] = path_sequence[non_zero_row_index][0];
					path_sequence_with_time[i][1] = path_sequence[non_zero_row_index][1];
					path_sequence_with_time[i][2] = column3_cnt;
					column3_cnt++;
				}
			}
	    	updateHero(time);//update title and state
	    }
    }


    public DragonSlayerImpl()
    {
    	this.map=new Map();
    	this.sys_time=0;
    	this.isPortalSet=false;
    	this.isTurnadoSet=false;
    	this.hero=new Hero(Title.WARRIOR,Status.MARCHING,new Area(0,0));
    	this.isTurnadoSet=false;
    	this.isPortalSet=false;
    }
    
    /**
     * 待考生实现，系统重置
     * 
     * @return 返回码
     */
    @Override
    public OpResult reset()
    {
    	this.map=new Map();
    	this.sys_time=0;
    	this.isPortalSet=false;
    	this.isTurnadoSet=false;
    	this.hero=new Hero(Title.WARRIOR,Status.MARCHING,new Area(0,0));
    	this.isTurnadoSet=false;
    	this.isPortalSet=false;
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
    public OpResult setFire(Area area, int time)
    {    	
    	if(sys_time<=time)
    	{
    		if(sys_time<time){
    			update(time);
    		}
    		int x=area.getX();
        	int y=area.getY();
        	char flag=isCollision(x,y);
    		if(flag==1)
    		{
    			this.map.setMap(x, y, MyElement.FIRE);
				System.out.println("Fire set at ("+x+","+y+")");
    			return new OpResult(ReturnCode.S002);
    		}else if(flag==2){
    			this.map.setMap(x, y, MyElement.FIRE_PORTAL_EXIT);
				System.out.println("Fire set at ("+x+","+y+")");
    			return new OpResult(ReturnCode.S002);
    		}else{
				System.out.println("Collision! SF failed!");
    			return new OpResult(ReturnCode.E005);
    		}
    	}
    	else{
			System.out.println("Time ERROR!");
    		return new OpResult(ReturnCode.E004);
    	}
    	 // return new OpResult(ReturnCode.E001);
    }
    
    
    /**
     * 待考生实现，设置龙卷风
     * 
     * @param area 设置区域
     * @param time 设置时间
     * @return 返回码
     */
    @Override
    public OpResult setTornado(Area area, int time)
    {	
    	if(sys_time<=time)
    	{
    		if(sys_time<time){
    			update(time);
    		}
    		int x=area.getX();
        	int y=area.getY();
        	char flag=isCollision(x,y);
    		if(flag==0)
    		{
				System.out.println("ERROR:Collision! ST failed!");
    			return new OpResult(ReturnCode.E005);
    			
    		}else{
    			if(this.isTurnadoSet){
					System.out.println("ERROR:Turnado has been set before!");
    				return new OpResult(ReturnCode.E006);
    			}else{
    				if(flag==1)
    	    		{
    	    			this.map.setMap(x, y, MyElement.FIRE);
    	    		}else if(flag==2){
    	    			this.map.setMap(x, y, MyElement.FIRE_PORTAL_EXIT);
    	    		}
					System.out.println("Turnado set at ("+x+","+y+")");
    				this.isTurnadoSet=true;
	    			return new OpResult(ReturnCode.S002);
    			}
    		}
    	}else{
			System.out.println("Time ERROR!");
    		return new OpResult(ReturnCode.E004);
    	}
    	 // return new OpResult(ReturnCode.E001);
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
    public OpResult setPortal(Area entry, Area exit, int time)
    {
    	if(sys_time<=time)
    	{
    		if(sys_time<time){
    			update(time);
    		}
        	int entry_x=entry.getX();
        	int entry_y=entry.getY();
        	int exit_x=exit.getX();
        	int exit_y=exit.getY();
        	char flag=isCollision(entry_x,entry_y,exit_x,exit_y);
    		if(flag==0)
    		{
				System.out.println("ERROR:Collision! SP failed!");
    			return new OpResult(ReturnCode.E005);    			
    		}else{
    			if(this.isPortalSet){
					System.out.println("ERROR:Portal has been set before!");
    				return new OpResult(ReturnCode.E007);
    			}else{
    				if(flag==3){
						System.out.println("ERROR:The entrance should be different to the exit!");
    					return new OpResult(ReturnCode.E008);
    				}else{
        	    		this.map.setMap(entry_x, entry_y, MyElement.PORTAL_ENTRANCE);
        	    		this.map.setMap(exit_x, exit_y, MyElement.PORTAL_EXIT);
						System.out.println("Portal_entry set at ("+entry_x+","+entry_y+")");
        	    		System.out.println("Portal_exit set at ("+exit_x+","+exit_y+")");
    	    			return new OpResult(ReturnCode.S002);
    				}
    			}
    		}
    	}else{
			System.out.println("Time ERROR!");
    		return new OpResult(ReturnCode.E004);
    	}
      //  return new OpResult(ReturnCode.E001);
    }
    
    /**
     * 待考生实现，查询
     * 
     * @param time 查询时间
     * @return 英雄信息
     */
    @Override
    public OpResult query(int time)
    {
        if(time>=this.sys_time)
    	{
    		if(time>this.sys_time){
        		update(time);
        	}
			System.out.println(this.hero.getTitle()+" "+this.hero.getStatus()+" at("+this.hero.getArea().getX()+","+this.hero.getArea().getY()+")");
    		return new OpResult(this.hero);
    	}else{
			System.out.println("Time ERROR!");
    		return new OpResult(ReturnCode.E004);	
    	}
    }
    
    public char isCollision(int x,int y)
    {
    	char flag=1;// 1:NONE 2:PORTAL_EXIT 3:OTHERS
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
    
    public char isCollision(int entry_x,int entry_y,int exit_x, int exit_y){
    	char flag=1;// 1:NONE 2:PORTAL_EXIT 3:entry is the same as exit 0:OTHERS
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