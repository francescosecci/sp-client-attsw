package com.pufose.client.guitest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.io.IOException;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.pufose.client.IClient;
import com.pufose.client.gui.GUI;
import com.pufose.client.gui.GUIpanel;

@RunWith(JUnit4.class)
public class ITEndToEnd  {
	private FrameFixture window;
	private GUI frame;
	private IClient cl;
	private String host="attsw-server.herokuapp.com";
	private String port="80";
	private HtmlUnitDriver maindriver;
	private WebElement mainPointerToResetDb;
	@Before
	public void runGui() {
		frame = GuiActionRunner.execute(()-> GUI.createGui(false));
		cl=Mockito.spy(frame.getClient());
		frame.mockClient(cl);
		window=new FrameFixture(frame.getFrame());
		window.show();
		window.textBox("serverField").setText(host);
		window.textBox("portField").setText(port);
		maindriver = new HtmlUnitDriver() {
			protected WebClient modifyWebClient(WebClient client) {
			DefaultCredentialsProvider creds=new DefaultCredentialsProvider();
			creds.addCredentials("user","password");
			client.setCredentialsProvider(creds);
			return client;
			}
		};
		mainPointerToResetDb=addOneTableInTheServer(maindriver);
	}
	@After
	public void tearDownGui() {
	
		String generatedId=window.comboBox("gridCombo").selectedItem();
		maindriver.findElementByName("content").sendKeys(generatedId);
		mainPointerToResetDb.click();
		maindriver.quit();
		window.cleanUp();
	}
	@Test
	public void testCanRetrieveAllGrids() throws IOException {
		window.button("btnCreateConn").click();
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
		verify(cl,times(1)).getAllTables();
		
		
	}
	@Test
	public void testCanRequestOneGrid() throws IOException {
		
		window.button("btnCreateConn").click();
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		window.comboBox("actionsCombo").selectItem(1);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
		verify(cl,times(1)).retrieveGrid(window.comboBox("gridCombo").target().getSelectedItem().toString());
		
	}
	@Test
	public void testCanRequestShortestPath() throws IOException {
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
		verify(cl,times(1)).getShortestPath("0_0", "0_1", window.comboBox("gridCombo").target().getSelectedItem().toString());
		
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
	@Test
	public void testCompleteScenario() {
		
		window.button("btnCreateConn").click();
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		assertReceivedCorrectly();
		window.comboBox("actionsCombo").selectItem(1);
		window.button("btnPerform").click();
		assertViewedCorrectly();
		window.textBox("sourceField").setText("0_0");
		window.textBox("sinkField").setText("0_2");
		window.comboBox("actionsCombo").selectItem(2);
		window.button("btnPerform").click();
		assertCorrectPath();
		
		
		
	}
	private void assertCorrectPath() {
		GUIpanel pan=frame.getGuiPanel();
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(0, 0));
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(0, 1));
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(0, 2));
		assertEquals(Color.RED,pan.getColorInPoint(1, 0));
		assertEquals(Color.RED,pan.getColorInPoint(1, 1));
		assertEquals(Color.BLACK,pan.getColorInPoint(1, 2));
		assertEquals(Color.RED,pan.getColorInPoint(2, 0));
		assertEquals(Color.BLACK,pan.getColorInPoint(2, 1));
		assertEquals(Color.BLACK,pan.getColorInPoint(2, 2));
		
	}
	private void assertViewedCorrectly() {
		GUIpanel pan=frame.getGuiPanel();
		assertEquals(Color.RED,pan.getColorInPoint(0, 0));
		assertEquals(Color.RED,pan.getColorInPoint(0, 1));
		assertEquals(Color.RED,pan.getColorInPoint(0, 2));
		assertEquals(Color.RED,pan.getColorInPoint(1, 0));
		assertEquals(Color.RED,pan.getColorInPoint(1, 1));
		assertEquals(Color.BLACK,pan.getColorInPoint(1, 2));
		assertEquals(Color.RED,pan.getColorInPoint(2, 0));
		assertEquals(Color.BLACK,pan.getColorInPoint(2, 1));
		assertEquals(Color.BLACK,pan.getColorInPoint(2, 2));
		
	}
	private void assertReceivedCorrectly() {
		window.comboBox("gridCombo").requireItemCount(1);
		
	}
	private WebElement addOneTableInTheServer(HtmlUnitDriver driver) {
		
		driver.get("http://"+host);
		driver.findElementsByLinkText("Add table").get(0).click();
		driver.findElementByName("number").sendKeys("3");
		driver.findElementByName("content").sendKeys("111110100");
		driver.findElementByName("submit").submit();
		driver.findElementsByLinkText("Remove table").get(0).click();
		return driver.findElementByName("submit");
	}
}
