package edu.ncsu.csc;
import java.io.*;

public class MyObject implements Serializable

{

	String name;

	int count;

	MyObject() // constructor

	{

		setName();

	}

	public void setName()

	{

		count++;

		name = "MyObject " + count;

	}

	public String toString()

	{

		return name;

	}

}
