package com.multiLayer.ER;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class TripleCommNet {
	
	Point[] points;
	public int nodeCount;//网络尺度
	private int commNum;//社区个数
	ArrayList<Community> community;
    ArrayList<ArrayList<Double>> param;//社团之间的连接参数
    int netWorkIndex;//网络编号
    public int giantComponentIndex;
    public HashMap<Integer,ArrayList<Integer>> cluster_nodeList;//记录每个集团对应的顶点编号
    public ArrayList<Integer> currentAll;//记录当前系统中functional的顶点标号
    
	public TripleCommNet(int nodeCount,ArrayList<ArrayList<Double>> param,int netWorkIndex,int commNums)
	{
		points = new Point[nodeCount];
		this.nodeCount = nodeCount;
		this.commNum = commNums;
		this.netWorkIndex = netWorkIndex;
		this.param = param;
		cluster_nodeList=new HashMap<Integer,ArrayList<Integer>>();
		currentAll = new ArrayList<Integer>();
		community = new ArrayList<Community>();
		//给每个社区编号(均分)
		for(int i=0;i<commNum;i++)
		{
			int beginCount = i*nodeCount/commNum;
			int endCount = (i+1)*nodeCount/commNum;
			community.add(new Community(beginCount,endCount));
		}
		int communityNum=0;
		for(int i=0;i<nodeCount;i++)
		{
			for(int j=0;j<commNum;j++)
			{
				int beginCount = community.get(j).beginCount;
				int endCount = community.get(j).endCount;
				if(i<endCount&&i>=beginCount)
					communityNum=j;
			}
			points[i] = new Point(i,i,communityNum);//建立一个顶点，并对顶点进行标号以及其所属连通分支号
			cluster_nodeList.put(i, new ArrayList<Integer>());
			cluster_nodeList.get(i).add(i);
			currentAll.add(i);
		}
	}
	public HashMap<Integer,Double> Degree_distribution() throws IOException
	{
		HashMap<Integer,Double> degree_prob=new HashMap<Integer,Double>();
		HashMap<Integer,Integer> degree_map=new HashMap<Integer,Integer>();
		for(int i=0;i<nodeCount;i++)
		{
			int degreeNum=points[i].adjVector.size();
			if(degree_map.containsKey(degreeNum))
			{
				int currentValue=degree_map.get(degreeNum)+1;
				degree_map.put(degreeNum, currentValue);
			}
			else
			degree_map.put(degreeNum, 1);
		}
		Entry<Integer,Integer> entry;
		for(Iterator<Entry<Integer, Integer>> it=degree_map.entrySet().iterator();it.hasNext();)
		{
			entry=it.next();
			int degree=entry.getKey();
			double prob=1.0*entry.getValue()/(nodeCount);
			degree_prob.put(degree, prob);
		}
		return degree_prob;
	}
	public void displayStruct(BufferedWriter bw_log) throws IOException
	{
		for(Point point:points)
		{
			for(Point adjPoint:point.adjVector)
			{
				bw_log.write(point.number+"\t"+adjPoint.number+"\n");
				bw_log.flush();
			}
		}
	}
}
