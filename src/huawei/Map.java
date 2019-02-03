package huawei;

public class Map {
	Square[][] table=new Square[16][16];

	
	public Map()
	{
		int i,j;
		for(i=0;i<16;i++)
		{
			for(j=0;j<16;j++)
			{
				
				this.table[i][j]=new Square(MyElement.NONE,0);
			}
		}
		
		this.table[0][0].setElement(MyElement.HERO);
		this.table[15][15].setElement(MyElement.DRAGON);
	}
	
	public void setMap(int x, int y, MyElement e)
	{
		this.table[x][y].setElement(e);
	}

}
