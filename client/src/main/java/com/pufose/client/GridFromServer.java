package com.pufose.client;

import java.util.Arrays;

public class GridFromServer {

	private int[][] matrix;
	private int id;

	public GridFromServer(int[][] matrix, int id) {
		this.matrix = matrix;
		this.id=id;
	}

	public GridFromServer(int id) {
		this.matrix=new int[0][0];
		this.id=id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		if (id != other.id)
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
