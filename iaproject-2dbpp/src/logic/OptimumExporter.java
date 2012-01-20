package logic;

import gui.GUIBin;
import gui.GUIOptimum;
import gui.GUIPacket;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class OptimumExporter {
	
	private static final String ROOT_ELEMENT = "BPP-2D_OptimumFile";
	private static final String NIT_ELM = "nIterations";
	private static final String TIME_ELM = "elapsedTime";
	private static final String FITNESS_ELM = "fitnessValue";
	private static final String BINS_LIST_ELM = "bins";
	private static final String BIN_ELM = "bin";
	private static final String PKT_ELM = "packet";
	
	
	public void saveOptimum(File file, GUIOptimum opt) throws IOException {
		Element root = new Element(ROOT_ELEMENT);
		Document optimumDoc = new Document(root);
		
		Element nitElm = new Element(NIT_ELM);
		nitElm.setText(Integer.toString(opt.getNIterations()));
		root.addContent(nitElm);
		
		Element timeElm = new Element(TIME_ELM);
		timeElm.setText(Long.toString(opt.getElapsedTime()));
		root.addContent(timeElm);
		
		Element fitnessElm = new Element(FITNESS_ELM);
		fitnessElm.setText(Float.toString(opt.getFitness()));
		root.addContent(fitnessElm);
		
		Element binsElm = binList2Element(opt.getBins());
		root.addContent(binsElm);
		
		XMLOutputter writer = new XMLOutputter(Format.getPrettyFormat());
		FileOutputStream fos = new FileOutputStream(file);
		writer.output(optimumDoc, fos);
		fos.close();
	}
	
	private Element binList2Element(List<GUIBin> bins) {
		Element binListElm = new Element(BINS_LIST_ELM);
		binListElm.setAttribute("nBins", Integer.toString(bins.size()));
		
		for (GUIBin guiBin : bins) {
			Element binElm = new Element(BIN_ELM);
			binElm.setAttribute("id", guiBin.getID());
			binElm.setAttribute("density", Float.toString(guiBin.getDensity()));
			binElm.setAttribute("width", Integer.toString(guiBin.getBinConfiguration().getWidth()));
			binElm.setAttribute("height", Integer.toString(guiBin.getBinConfiguration().getHeight()));
			
			for (GUIPacket pkt : guiBin.getPackets()) {
				Element pktElm = new Element(PKT_ELM);
				pktElm.setAttribute("id", Integer.toString(pkt.getID()));
				pktElm.setAttribute("x", Integer.toString(pkt.getPointX()));
				pktElm.setAttribute("y", Integer.toString(pkt.getPointY()));
				pktElm.setAttribute("width", Integer.toString(pkt.getWidth()));
				pktElm.setAttribute("height", Integer.toString(pkt.getHeight()));
				pktElm.setAttribute("color", Integer.toString(pkt.getColor().getRGB()));
				
				binElm.addContent(pktElm);
			}
			
			binListElm.addContent(binElm);
		}
		
		return binListElm;
	}
	
	public GUIOptimum loadOptimum(File file) throws JDOMException, IOException {
		SAXBuilder parser = new SAXBuilder();
		Document doc = parser.build(file);
		
		Element root = doc.getRootElement();
		if (!ROOT_ELEMENT.equals(root.getName())) {
			throw new JDOMException("Not a " + ROOT_ELEMENT);
		}
		
		Element nItElm = root.getChild(NIT_ELM);
		if (nItElm == null) {
			throw new JDOMException("no nIt");
		}
		final int nIt = Integer.valueOf(nItElm.getText()).intValue();
		
		Element timeElm = root.getChild(TIME_ELM);
		if (timeElm == null) {
			throw new JDOMException("no timeElm");
		}
		final long time = Long.valueOf(timeElm.getText()).longValue();
		
		Element fitnessElm = root.getChild(FITNESS_ELM);
		if (fitnessElm == null) {
			throw new JDOMException("no fitnessElm");
		}
		final float fitness = Float.valueOf(fitnessElm.getText()).floatValue();
		
		Element binsListElm = root.getChild(BINS_LIST_ELM);
		if (binsListElm == null) {
			throw new JDOMException("no binsListElm");
		}
		final List<GUIBin> binsList = element2GUIBinList(binsListElm);
		
		return new GUIOptimum() {
			
			@Override
			public int getNIterations() {
				return nIt;
			}
			
			@Override
			public float getFitness() {
				return fitness;
			}
			
			@Override
			public long getElapsedTime() {
				return time;
			}
			
			@Override
			public List<GUIBin> getBins() {
				return binsList;
			}
		};
	}
	
	private List<GUIBin> element2GUIBinList(Element binListElm) throws DataConversionException {
		List<GUIBin> toRet = new ArrayList<GUIBin>();
		
		List<?> bins = binListElm.getChildren();
		for (Object binObj : bins) {
			Element binElm = (Element) binObj;
			
			Attribute widthAtt = binElm.getAttribute("width");
			int width = widthAtt.getIntValue();
			
			Attribute heightAtt = binElm.getAttribute("height");
			int height = heightAtt.getIntValue();
			
			Attribute IDAtt = binElm.getAttribute("id");
			int ID = IDAtt.getIntValue();
			
			Attribute densityAtt = binElm.getAttribute("density");
			float density = densityAtt.getFloatValue();
			
			List<GUIPacket> packets = new ArrayList<GUIPacket>();
			List<?> packetElms = binElm.getChildren();
			for (Object pktObj : packetElms) {
				Element pktElm = (Element) pktObj;
				
				Attribute pktIDAtt = pktElm.getAttribute("id");
				int pktID = pktIDAtt.getIntValue();
				
				Attribute pktXAtt = pktElm.getAttribute("x");
				int pktX = pktXAtt.getIntValue();
				
				Attribute pktYAtt = pktElm.getAttribute("y");
				int pktY = pktYAtt.getIntValue();
				
				Attribute pktWidthAtt = pktElm.getAttribute("width");
				int pktWidth = pktWidthAtt.getIntValue();
				
				Attribute pktHeightAtt = pktElm.getAttribute("height");
				int pktHeight = pktHeightAtt.getIntValue();
				
				Attribute pktColorAtt = pktElm.getAttribute("color");
				Color pktColor = new Color(pktColorAtt.getIntValue());
				
				GUIPacket pkt = new GUIPacket(
						new PacketDescriptor(pktID, pktWidth, pktHeight, pktColor), 
						new Point(pktX, pktY), false);
				packets.add(pkt);
			}
			
			GUIBin readBin = new GUIBin(new BinConfiguration(width, height), ID, density, packets);
			toRet.add(readBin);
		}
		
		return toRet;
	}
}
