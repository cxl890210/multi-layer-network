import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Map.Entry;

public class AttackModel {
//	�����������(1-p)�������������
	public void attackProcess(NetWork netWork1,NetWork netWork2, double attackProb)
	{
//		��ʼ����A���������1
		int communitySize = netWork1.community[0].communitySize;
		Random random = new Random();
		int attackNum = (int)((1-attackProb)*communitySize);
		//��ʼ�������Ľڵ㼯
		HashSet<Integer> failedSet = new HashSet<Integer>();
		while(failedSet.size()<attackNum)
		{
			int index = random.nextInt(communitySize);
			failedSet.add(index);
		}
		//��ʼ����ʧЧ
		cascadeProcess(failedSet,netWork1,netWork2);
	}
	//����ʧЧ����
	public void cascadeProcess(HashSet<Integer> failedSet,NetWork netWork1,NetWork netWork2)
	{
		HashSet<Integer> set1,set2;//����ʧЧ/�������ڵ���
		set1=set2=failedSet;
		while(set1.size()>0&&set2.size()>0)
		{
			set2=failedFunc(set1,netWork1,netWork2);
			if(set2.size()<=0)break;
			set1=failedFunc(set2,netWork2,netWork1);
		}
	}
	public HashSet<Integer> failedFunc(HashSet<Integer> failedSet,NetWork attNetwork,NetWork casNetWork)
	{
		HashSet<Integer> set=new HashSet<Integer>();//��������1�������������2�еĽڵ�
		ArrayList<Integer> attackedCluster = new ArrayList<Integer>();//���汻��������ͨ��֧��
		ArrayList<Integer> attCurrentAll = attNetwork.currentAll;//��ǰϵͳ�е���������Ч�ڵ��ż���
		ArrayList<Integer> casCurrentAll = casNetWork.currentAll;
		for(Iterator<Integer> it=failedSet.iterator();it.hasNext();)
		{
			//Ѱ�ұ���������ͨ��֧
			int failedNode = it.next();
			Point failedPoint = attNetwork.points[failedNode];
			int branchNumber=failedPoint.branchNumber;
			if(!attackedCluster.contains(branchNumber))
			{//Ѱ�ұ���������ͨ��֧
				attackedCluster.add(branchNumber);
			}
			//�������ڵ�ֱ�������Ľڵ㼯��
			delNeighbor(failedPoint);
		}
//		��������ͨ��֧,�����������ͨ��֧�ڵ�ʧЧ
		int giantCluster=getGiantComponent(attNetwork,attackedCluster);	
		attNetwork.giantComponentIndex = giantCluster;
//		Ѱ��set2
		ArrayList<Integer> giantNodes=attNetwork.cluster_nodeList.get(giantCluster);//��ǰ�����ͨ��֧�ڵ��ż���
		ArrayList<Integer> tempNodes = new ArrayList<Integer>();//��ʱ����ʧЧ�ڵ���
		attCurrentAll.removeAll(giantNodes);
		tempNodes.addAll(attCurrentAll);
		attCurrentAll.clear();
		attCurrentAll.addAll(giantNodes);
		casCurrentAll.removeAll(tempNodes);
		for(Integer index:tempNodes)
		{
			set.add(index);
		}
		return set;
	}
	public void delNeighbor(Point failedPoint)
	{
		ArrayList<Point> adjList = failedPoint.adjVector;
		for(Point connPoint:adjList )
		{
			delete(failedPoint,connPoint);		
		}
		//��ձ������ڵ��ڽӵ㼯��	
		failedPoint.adjVector.clear();
	}
	public void delete(Point point1,Point point2)
	{
        for(Iterator<Point> it=point2.adjVector.iterator();it.hasNext();)
		{
			Point p = it.next();
			if(p.equals(point1))
			{
				it.remove();
			}
		}
	}
	
	public int getGiantComponent(NetWork netWork,ArrayList<Integer> attackedCluster)
	{
		//��ÿ����������֧����һ�ι����������ָ�֮�����ͨ��֧
		for(int clusterIndex:attackedCluster)
			BFS(clusterIndex,netWork);
		//Ѱ�������ͨ��֧��������ͨ��֧ʧЧ
		Entry<Integer,ArrayList<Integer>> entry;
		int giantCluster=-1;
		int maxSize=-1;
		HashMap<Integer,ArrayList<Integer>> cluster_nodeList = netWork.cluster_nodeList;
		ArrayList<Integer> tempList;
		for(Iterator<Entry<Integer, ArrayList<Integer>>> it=cluster_nodeList.entrySet().iterator();it.hasNext();)
		{	
			entry= it.next();
			int clusterIndex=entry.getKey();
			int clusterSize=entry.getValue().size();
			if(clusterSize>maxSize)
			{
				giantCluster=clusterIndex;
				maxSize=clusterSize;
			}
		}
		tempList = cluster_nodeList.get(giantCluster);
		cluster_nodeList.clear();
		cluster_nodeList.put(giantCluster,tempList);
		return giantCluster;
	}
//��ԭ������ÿ���������ķ�֧���й����������
	public void BFS(int clusterNum,NetWork netWork)
	{
		ArrayList<Integer> indexList = netWork.cluster_nodeList.get(clusterNum);
		netWork.cluster_nodeList.remove(clusterNum);//ɾ���ɷ�֧
		HashMap<Integer,Boolean> visited = new HashMap<Integer,Boolean>();
		for(int index:indexList)
			visited.put(index,false);
		LinkedList<Integer> queue = new LinkedList<Integer>();//��ʼ������
		for(int v:indexList)
		{
			if(!visited.get(v))
			{
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				netWork.cluster_nodeList.put(v, tempList);//����һ���·�֧
				visited.put(v, true);//��v���Ϊ�ѱ�����
				queue.add(v);
				while(queue.size()>0)
				{
					int nodeIndex = queue.removeFirst();
					netWork.points[nodeIndex].branchNumber=v;//���Ľڵ�������ͨ��֧��
					netWork.cluster_nodeList.get(v).add(nodeIndex);
					for(Point point:netWork.points[nodeIndex].adjVector)//�����ڽӽڵ�������
					{
						if(!visited.get(point.number))
						{
							visited.put(point.number, true);
							queue.add(point.number);
						}
					}
				}
			}
		}	
}
}
