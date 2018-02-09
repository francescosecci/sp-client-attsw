package com.pufose.client.guitest;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.pufose.client.gui.GUI;

@RunWith(JUnit4.class)
public class ITEndToEnd  {
	private FrameFixture window;
	private GUI frame;
	private String host="attsw-server.herokuapp.com";
	private String port="80";
	@Before
	public void runGui() {
		frame = GuiActionRunner.execute(()-> GUI.createGui(false));
		window=new FrameFixture(frame.getFrame());
		window.show();
		window.textBox("serverField").setText(host);
		window.textBox("portField").setText(port);
	}
	@After
	public void tearDownGui() {
		window.cleanUp();
	}
	@Test
	public void testCanRetrieveAllGrids() {
		window.button("btnCreateConn").click();
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
		
	}
	@Test
	public void testCanRequestOneGrid() {
		
		window.button("btnCreateConn").click();
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		window.comboBox("actionsCombo").selectItem(1);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
		
	}
	@Test
	public void testCanRequestShortestPath() {
		window.button("btnCreateConn").click();
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		window.comboBox("actionsCombo").selectItem(1);
		window.button("btnPerform").click();
		window.textBox("sourceField").setText("0_0");
		window.textBox("sinkField").setText("0_1");
		window.comboBox("actionsCombo").selectItem(2);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
	}
	@Test
	public void testCanRequestShortestPathErrored() {
		window.button("btnCreateConn").click();
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		window.comboBox("actionsCombo").selectItem(1);
		window.button("btnPerform").click();
		window.textBox("sourceField").setText("0_0");
		window.textBox("sinkField").setText("0_3");
		window.comboBox("actionsCombo").selectItem(2);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.SERVER_ERROR);
	}

}
