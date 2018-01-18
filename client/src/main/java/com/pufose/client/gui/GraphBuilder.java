package com.pufose.client.gui;

import com.pufose.client.GridFromServer;

public class GraphBuilder {
	private GraphBuilder() {}
	public static final GraphBuilder instance=new GraphBuilder();
	public void makeGraph(GridFromServer mat, GUIpanel p) {
		int n=mat.getN();
		for(int i=0; i<n;i++)
		{
			
			for(int j=0; j<n;j++) {
				if(mat.isEnabled(i, j))p.enablePoint(mat.getName(i,j),i,j);
				else p.enableNotHighlightablePoint("", i, j);
			}
			
		}
	    
	}
	
	
}