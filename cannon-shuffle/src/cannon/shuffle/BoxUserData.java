package cannon.shuffle;

public class BoxUserData{
	
	
	private int collisionGroup;
	private int boxId;
	
	public BoxUserData(int boxid,int collisiongroup){
		this.boxId = boxid;
		this.collisionGroup = collisiongroup;
	}
	public int getBoxId(){
		return this.boxId;
	}
	public int getCollisionGroup(){ 
		return this.collisionGroup;
	}

}