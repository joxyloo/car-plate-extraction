package dmpProject;

public class Peak {
	
	private int max, left, right, maxValue,diff = 0;
	
	public Peak(int max, int left, int right){
		this.max = max;
		this.left = left;
		this.right = right;
		this.maxValue = 0;
	}
	
	public void setMaxValue(int maxValue){
		this.maxValue = maxValue;
	}
	
	public void setMax(int max){
		this.max = max;
	}
	
	public void setLeft(int left){
		this.left = left;
	}
	
	public void setRight(int right){
		this.right = right;
	}
	public void setDiff(int diff){
		this.diff = diff;
	}
	
	public int getMaxValue(){
		return maxValue;
	}
	
	public int getLeft(){
		return left;
	}
	
	public int getRight(){
		return right;
	}
	
	public int getDiff(){
		return (this.right-this.left);
	}

}
