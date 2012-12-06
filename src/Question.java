
public class Question {

	private String side1 = "";
	private String side2 = "";
	private String cornerImage = null;
	
	public Question(String side1, String side2) {
		this.side1 = side1;
		this.side2 = side2;
	}
	public String getSide1() {
		return side1;
	}
	public void setSide1(String side1) {
		this.side1 = side1;
	}
	public String getSide2() {
		return side2;
	}
	public void setSide2(String side2) {
		this.side2 = side2;
	}
	public String getLatexString() {
		return side1 + " = " + side2;
	}
	public String getCornerImage() {
		return cornerImage;
	}
	public Question setCornerImage(String cornerImage) {
		this.cornerImage = cornerImage;
		return this;
	}
	
	

}