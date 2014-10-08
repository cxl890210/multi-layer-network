package com.multiLayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Map.Entry;

public class AttackModel {

//	随机攻击过程(1-p)比例的随机攻击
	public void attackProcess(NetWork netWork1,NetWork netWork2,NetWork netWork3,double attackProb)
	{
//		初始攻击A网络的社区1
		int communitySize = netWork1.community[0].communitySize;
		Random random = new Random();
		int attackNum = (int)((1-attackProb)*communitySize);
		//初始被攻击的节点集
		HashSet<Integer> failedSet = new HashSet<Integer>();
		while(failedSet.size()<attackNum)
		{
			int index = random.nextInt(communitySize);
			failedSet.add(index);
		}
		cascadeProcess(failedSet,netWork1,netWork2,netWork3);
	}
	//级联失效过程
	public void cascadeProcess(HashSet<Integer> failedSet,NetWork netWork1,NetWork netWork2,NetWork netWork3)
	{
		HashSet<Integer> set1,set2;
		set1=set2=failedSet;
		while(set1.size()>0&&set2.size()>0)
		{
			set2 = partialCascade(set1,netWork1,netWork2,netWork3);
			if(set2.size()<=0)break;
			set1 = partialCascade(set2,netWork3,netWork2,netWork1);
		}
		
	}
//	分阶段级联
	public HashSet<Integer> partialCascade(HashSet<Integer> failedSet,NetWork attNetWork,NetWork casNetWork,NetWork otherNetwork)
	{
		HashSet<Integer> set1,set2;
		set1=set2=failedSet;
		HashSet<Integer> set3 = new HashSet<Integer>();
		if(failedSet.size()<=0)return set3;
		while(set1.size()>0&&set2.size()>0)
		{
			set2=failedFunc(set1,attNetWork,casNetWork);
			set1=failedFunc(set2,casNetWork,attNetWork);
		}
// 		寻找第三个网络中，因前两个网络级联而失效的节点集合
		int giantIndex = casNetWork.giantComponentIndex;
		ArrayList<Integer> giantNodes = casNetWork.cluster_nodeList.get(giantIndex);
//		if(giantNodes.size()<=1)return set3;
		otherNetwork.currentAll.removeAll(giantNodes);
		for(Integer index:otherNetwork.currentAll)
		{
			set3.add(index);
		}
		otherNetwork.currentAll.clear();
		otherNetwork.currentAll.addAll(giantNodes);
		return set3;
	}
	public HashSet<Integer> failedFunc(HashSet<Integer> failedSet,NetWork attNetwork,NetWork casNetWork)
	{
		HashSet<Integer> set=new HashSet<Integer>();//保存网络1中相关联的网络2中的节点
		ArrayList<Integer> attackedCluster = new ArrayList<Integer>();//保存被攻击的连通分支号
		ArrayList<Integer> attCurrentAll = attNetwork.currentAll;//当前系统中的所有仍有效节点编号集合
		ArrayList<Integer> casCurrentAll = casNetWork.currentAll;
		if(failedSet.size()<=0)return set;
		for(Iterator<Integer> it=failedSet.iterator();it.hasNext();)
		{
			//寻找被攻击的连通分支
			int failedNode = it.next();
			Point failedPoint = attNetwork.points[failedNode];
			int branchNumber=failedPoint.branchNumber;
			if(!attackedCluster.contains(branchNumber))
			{//寻找被攻击的连通分支
				attackedCluster.add(branchNumber);
			}
			//被攻击节点直接相连的节点集合
			delNeighbor(failedPoint);
		}
		int giantCluster;
//		获得最大连通分支,其它非最大连通分支节点失效
		if(attNetwork.currentAll.size()==1)
		{
			giantCluster = attNetwork.currentAll.get(0);
			attNetwork.cluster_nodeList.clear();
			ArrayList<Integer> nodes = new ArrayList<Integer>();
			nodes.add(giantCluster);
			attNetwork.cluster_nodeList.put(giantCluster, nodes);	
		}
		else
			giantCluster=getGiantComponent(attNetwork,attackedCluster);	
		attNetwork.giantComponentIndex = giantCluster;
//		寻找set2
		ArrayList<Integer> giantNodes=attNetwork.cluster_nodeList.get(giantCluster);//当前最大连通分支节点标号集合
		attCurrentAll.removeAll(giantNodes);
		for(Integer index:attCurrentAll)
		{
			set.add(index);
		}
		casCurrentAll.removeAll(attCurrentAll);
		attCurrentAll.clear();
		attCurrentAll.addAll(giantNodes);
		return set;
	}
	public void delNeighbor(Point failedPoint)
	{
		ArrayList<Point> adjList = failedPoint.adjVector;
		for(Point connPoint:adjList )
		{
			delete(failedPoint,connPoint);		
		}
		//清空被攻击节点邻接点集合	
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
		//对每个被攻击分支进行一次广搜以搜索分割之后的连通分支
		for(int clusterIndex:attackedCluster)
			BFS(clusterIndex,netWork);
		//寻找最大连通分支，其余连通分支失效
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
//对原网络中每个被攻击的分支进行广度优先搜索
	public void BFS(int clusterNum,NetWork netWork)
	{
		ArrayList<Integer> indexList = netWork.cluster_nodeList.get(clusterNum);
		netWork.cluster_nodeList.remove(clusterNum);//删除旧分支
		HashMap<Integer,Boolean> visited = new HashMap<Integer,Boolean>();
		for(int index:indexList)
			visited.put(index,false);
		LinkedList<Integer> queue = new LinkedList<Integer>();//初始化队列
		for(int v:indexList)
		{
			if(!visited.get(v))
			{
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				netWork.cluster_nodeList.put(v, tempList);//建立一个新分支
				visited.put(v, true);//将v标记为已被访问
				queue.add(v);
				while(queue.size()>0)
				{
					int nodeIndex = queue.removeFirst();
					netWork.points[nodeIndex].branchNumber=v;//更改节点所属连通分支号
					netWork.cluster_nodeList.get(v).add(nodeIndex);
					for(Point point:netWork.points[nodeIndex].adjVector)//将其邻接节点加入队列
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
