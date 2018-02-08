package com.pufose.client;

import java.util.Arrays;

public class GridFromServer {

	private int[][] matrix;
	private int n;

	public GridFromServer(int n,int[][] matrix) {
		this.matrix = matrix;
		this.n=n;
	}

	public GridFromServer() {
		this.matrix=new int[0][0];
		this.n=0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + n;
		result = prime * result + Arrays.deepHashCode(matrix);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridFromServer other = (GridFromServer) obj;
		if (n != other.n)
			return false;
		return (Arrays.deepEquals(matrix, other.matrix));
			
	}
	
	public int getN() {
		return matrix.length;
	}
	
	public boolean isEnabled(int i, int j) {
		try {
			return matrix[i][j]>0;
		}catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}
	
	public String getName(int i, int j) {
		return i+"_"+j;
	}

}
