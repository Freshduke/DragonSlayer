package huawei;

public class Hero {
	private Title title;
	private int x;
	private int y;
	private boolean state;
	
	public Hero(){
		this.title=Title.WORIOR;
		this.x=0;
		this.y=0;
		this.state=true;	
	}
	
	public void March(){
		this.state=true;
	}
	
	public void Wait(){
		this.state=false;
	}
	
	public void changeTitle(){
		this.title=Title.DRAGON_SLAYER;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public boolean getState(){
		return this.state;
	}
	
	public Title getTitle(){
		return this.title;
	}

}
