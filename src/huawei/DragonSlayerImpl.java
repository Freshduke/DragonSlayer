package huawei;

import huawei.exam.*;
import java.util.ArrayList;
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
	private Map map;
	private int sys_time=0;
	private boolean isTurnadoSet;
	private boolean isPortalSet;
	private Hero hero;
	private int[][] path_sequence=new int[100][3];
	private static int[][] temp_map = new int[16][16];
	
	
	
	public void ComparePath(){
		
		for(int i=0;i<16;i++) {
			for(int j=0;j<16;j++) {

				if(map.table[i][j].element == MyElement.FIRE) {
					temp_map[i][j]=1;
				}
				else if(map.table[i][j].element == MyElement.TORNADO) {
						temp_map[i][j]=1;
				}
				else if(map.table[i][j].element == MyElement.PORTAL_ENTRANCE) {
					temp_map[i][j]=1;
			    }
				else if(map.table[i][j].element == MyElement.TORNADO_PORTAL_EXIT) {
					temp_map[i][j]=1;
				}
				else if(map.table[i][j].element == MyElement.FIRE_PORTAL_EXIT) {
					temp_map[i][j]=1;	
				}
			}
			
			
		}
		//
	}
	
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
		
		 return path_seq;
	}
	
	public void updateHero(int time){
		
	}
	
    public void update(int time)
    {
    	ComparePath();
    	updateHero(time);//update title and state
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
