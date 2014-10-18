package com.multiLayer;
import java.util.Scanner;
import java.io.*;
public class demo {

	public void display(int[] a)
	{
		for(int num:a)
		{
			System.out.println(num);
		}
	}
	public static void main(String[] args) throws IOException
	{
		System.out.println("test");
		Scanner inScanner = new Scanner(System.in);
		float a = inScanner.nextFloat();
		BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
	    String testString = buf.readLine();
		System.out.println(testString);
	}

}
