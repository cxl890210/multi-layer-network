import java.util.ArrayList;


public class Point {

	public int number ;//��¼����
	public int branchNumber;//��¼������cluster���
	public ArrayList<Point> adjVector;//�õ���ڽӵ�����
	public boolean isFailed;//�ڵ�ʧЧ���
	public int communityNumber;
	public Point(int number,int branchNumber,int communityNum)
	{
		this.number = number;
		this.communityNumber = communityNum;
		this.branchNumber = branchNumber;
		isFailed=false;
		adjVector = new ArrayList<Point>();
	}
}
