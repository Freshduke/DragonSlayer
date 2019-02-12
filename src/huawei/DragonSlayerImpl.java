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
	private static Map map;
	private int sys_time=0;
	private boolean isTurnadoSet;
	private boolean isPortalSet;
	private Hero hero;
	private int[][] path_sequence=new int[100][2];
	private int[][] path_sequence_with_time=new int[100][3];     //最优英雄位置序列。
	private static int[][] temp_map = new int[16][16];
	public int[] P = new int[2];


	
	// 比较不同case下的总路径长度，并且将最优路径赋值到path_sequence中。
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
		
	    // 不经过传送门可以经过龙卷风的最优路径。
		p_no_portal = p_cmp_tornado(hero.getX(), hero.getY() , 15 , 15 ,1);
		length_p_no_portal = detech_sequence_length(p_no_portal);
		
		//判断地图中有无传送门，若没有传送门（出口值不更新）or 传送门出口处有火焰则无需计算经过传送门的最优路径。
		for(int i = 0; i< 16;i++)
		{
			for(int j =0 ;j < 16;j++) 
			{
				if(map.table[i][j].element == MyElement.FIRE_PORTAL_EXIT||map.table[i][j].element == MyElement.PORTAL_EXIT || map.table[i][j].element == MyElement.HERO_PORTAL_EXIT  || map.table[i][j].element == MyElement.TORNADO_PORTAL_EXIT)	
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
			path_sequence =p_no_portal;
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

			p_portal_entran = p_cmp_tornado(hero.getX(), hero.getY() , portal_entrance_x , portal_entrance_y , 0);
			
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
					if(p_portal_entran[i][1] != 0)
					{
						p_portal_entran[i][1] = p_portal_exit[k][1];
						p_portal_entran[i][2] = p_portal_exit[k][2];
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
			if(p_tornado_access[i][1] == tornado_x && p_tornado_access[i][2] == tornado_y) {
				for (int j = 100; j>i; j--) {
					p_tornado_access[j][1]=p_tornado_access[j-1][1];
					p_tornado_access[j][2]=p_tornado_access[j-1][2];
				}
				for (int j = 100; j>i+1; j--) {
					p_tornado_access[j][1]=p_tornado_access[j-1][1];
					p_tornado_access[j][2]=p_tornado_access[j-1][2];
				}
				p_tornado_access[i][1]=tornado_x;
				p_tornado_access[i][2]=tornado_y;
				p_tornado_access[i+1][1]=tornado_x;
				p_tornado_access[i+1][2]=tornado_y;
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
			if(array[k][1]== 0) {
				return k;
			}
		}
		return 100;
	}
	
	
	//给定地图起点&起点&终点&返回最优路径序列
	public static int[][] findpath(int depart_x, int depart_y, int destin_x, int destin_y){
		//全局变量：int[16][16] temp_map记录着当前地图的信息：1代表不可经过，0代表可经过。
		//调用示例：
		/*if(temp_map[1][2] == 1) {
		 blablabla;
		}*/
		
		//输入：temp_map[depart_x,depart_y] 
		//(depart_x,depart_y)是起点，
		//temp_map[destin_x,destin_y]
		//(destin_x,destin_y)是终点
		
		//返回的变量
		int[][] path_seq = new int[100][2];   //自动初始化的数值就是0，直接序列从上往下写就行
/* 第一列	第二列 返回值举例（第一列横坐标，第二列纵坐标）   
		1   1
		2   2
		3   3
		4   4
		5   5
		0   0
		......
		0   0
		*/
		//#######################################################################向萌施展才华的地方

		//建立邻接矩阵Gmat
		int[][] Gmat = new int[256][256]; //注意这里面使用999来代表无穷大。

		for(int i=1;i<15;i++)
			for(int j=1;j<15;j++)
			{
				if(temp_map[i-1][j-1]==0||temp_map[i-1][j]==0||temp_map[i-1][j+1]==0||temp_map[i][j-1]==0||temp_map[i][j+1]==0||temp_map[i+1][j-1]==0||temp_map[i+1][j]==0||temp_map[i+1][j+1]==0)
				{
					Gmat[i][j]=1;
					Gmat[j][i]=1;
				}
				else
				{
					Gmat[i][j]=999;
					Gmat[j][i]=999;
				}
			}


		 return path_seq;
	}

	void dijkstra(int s, ArrayList<Edge> G)
	{
		//Vector dist = new Vector();
		int [] dist = new int[256];
		for(int i = 0; i<256 ; i++)
		{
			dist[i] = 9999;
		}
		ArrayList<> q;

		dist[s] = 0;
		q.push(P(0, s));
		while(!q.empty())
		{
			P p = q.top();   //从尚未使用的顶点中找到一个距离最小的顶点
			q.pop();
			int v = p.second;
			if(dist[v] < p.first)
				continue;
			for(int i=0; i<G[v].size(); i++)
			{
				Edge &e = G[v][i];
				int dis = dist[v] + e.cost;
				if(dist[e.to] > dis)
				{
					dist[e.to] = dist[v] + e.cost;
					q.push(P(dist[e.to], e.to));
					G4[v].push_back(e);
				}
				else if(dist[e.to] == dis)
				{
					G4[v].push_back(e);
				}
			}
		}
	}

	
	//更新英雄称号&行进状态
	public void updateHero(int time){
		for(int i =0; i<100;i++) {
			if(path_sequence_with_time[i][3]==time) {
				hero.setX(path_sequence_with_time[i][0]);
				hero.setY(path_sequence_with_time[i][1]);
				hero.March();
				sys_time = time;
			}
		}
		if(hero.getX() == 15 && hero.getY()==15)
		{
			hero.changeTitle();
		}
	}
	
	//更新系统时间&英雄位置&称号&行进状态总函数。
    public void update(int time)
    {
    	ComparePath();
    	
    	int non_zero_row_index = 0;;
    	
		int column3_cnt = sys_time;			
	    if(path_sequence[1][1]==0 && path_sequence[1][2]==0) {
			hero.Wait();
			sys_time = time;
		}
	    else
	    {
			for(int i=0;i<2;i++) {
				path_sequence_with_time[1][i] = path_sequence[1][i];
			}		
			for(int i =1;i<100;i++){
				if(path_sequence[i][1]!=0) {
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
    	this.hero=new Hero();
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
    	this.hero=new Hero();
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
    	if(sys_time>=time)
    	{
    		if(sys_time>time){
    			update(time);
    		}
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
    	 //  return new OpResult(ReturnCode.E001);
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
    	if(sys_time>=time)
    	{
    		if(sys_time>time){
    			update(time);
    		}
    		int x=area.getX();
        	int y=area.getY();
        	char flag=isCollision(x,y);
    		if(flag==0)
    		{
    			return new OpResult(ReturnCode.E005);
    			
    		}else{
    			if(this.isTurnadoSet){
    				return new OpResult(ReturnCode.E006);
    			}else{
    				if(flag==1)
    	    		{
    	    			this.map.setMap(x, y, MyElement.FIRE);
    	    		}else if(flag==2){
    	    			this.map.setMap(x, y, MyElement.FIRE_PORTAL_EXIT);
    	    		}
    				this.isTurnadoSet=true;
	    			return new OpResult(ReturnCode.S002);
    			}
    		}
    	}else{
    		return new OpResult(ReturnCode.E004);
    	}
    	 //  return new OpResult(ReturnCode.E001);
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
    	if(sys_time>=time)
    	{
    		if(sys_time>time){
    			update(time);
    		}
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
        return new OpResult(ReturnCode.E001);
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
