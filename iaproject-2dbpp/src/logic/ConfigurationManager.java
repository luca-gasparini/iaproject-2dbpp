package logic;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ConfigurationManager {
	
	private static final String ROOT_STRING = "BPP-2D_ConfigurationFile";
	private static final String CORE_NAME_STR = "CoreName";
	private static final String CORE_CONF_STR = "CoreConfiguration";
	private static final String PROBLEM_CONF_STR = "ProblemConfiguration";
	
	private String coreName;
	private Object coreConfiguration;
	private ProblemConfiguration problemConfiguration;
	
	public void loadFromFile(File confFile) throws JDOMException, IOException {
		SAXBuilder parser = new SAXBuilder();
		Document doc = parser.build(confFile);
		
		Element root = doc.getRootElement();
		if (!ROOT_STRING.equals(root.getName())) {
			throw new JDOMException("Not a " + ROOT_STRING + " config file");
		}
		
		Element coreNameElm = root.getChild(CORE_NAME_STR);
		if (coreNameElm == null) {
			throw new JDOMException("No CORE NAME specified");
		}
		this.coreName = coreNameElm.getText();
		
		Element coreConfElm = root.getChild(CORE_CONF_STR);
		if (coreConfElm != null) {
			// build configuration object
			BASE64Decoder b64dec = new BASE64Decoder();
			String b64Conf = coreConfElm.getText();
			ByteArrayInputStream bais = new ByteArrayInputStream(b64dec.decodeBuffer(b64Conf));
			ObjectInputStream ois = new ObjectInputStream(bais);
			try {
				this.coreConfiguration = ois.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException(e);
			}
		} // else -> do nothing because null-coreConfigs are allowed
		
		Element problemConf = root.getChild(PROBLEM_CONF_STR);
		if (problemConf == null) {
			throw new JDOMException("No PROBLEM CONF specified");
		}
		this.problemConfiguration = XML2ProblemConf(problemConf);
	}
	
	public void saveToFile(File configFile) throws IOException {
		if (coreName == null) {
			throw new IllegalStateException("Core name not set");
		}
		if (coreConfiguration == null) {
			throw new IllegalStateException("Core configuration not set");
		}
		
		Element root = new Element(ROOT_STRING);
		Document configDoc = new Document(root);
		
		Element coreNameElm = new Element(CORE_NAME_STR);
		coreNameElm.setText(this.coreName);
		root.addContent(coreNameElm);
		
		if (coreConfiguration != null) {
			Element coreConfElm = new Element(CORE_CONF_STR);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(coreConfiguration);
			oos.close();
			BASE64Encoder b64enc = new BASE64Encoder();
			String coreConf = b64enc.encode(baos.toByteArray());
			coreConfElm.setText(coreConf);
			root.addContent(coreConfElm);
		}
		
		Element problemConfElm = problemConf2XML(PROBLEM_CONF_STR, problemConfiguration);
		root.addContent(problemConfElm);
		
		XMLOutputter writer = new XMLOutputter(Format.getPrettyFormat());
		FileOutputStream fos = new FileOutputStream(configFile);
		writer.output(configDoc, fos);
		fos.close();
	}
	
	private Element problemConf2XML(String childName, ProblemConfiguration conf) {
		Element confRoot = new Element(childName);
		
		Element binConf = new Element("bin");
		Attribute widthAtt = new Attribute("width", Integer.toString(conf.getBin().getWidth()));
		Attribute heightAtt = new Attribute("height", Integer.toString(conf.getBin().getHeight()));
		binConf.setAttributes(Arrays.asList(widthAtt, heightAtt));
		confRoot.addContent(binConf);
		
		Element pktConf = new Element("packets");
		for (PacketConfiguration packet : conf.getPackets()) {
			Element pktElm = new Element("packet");
			Attribute pktWidth = new Attribute("width", Integer.toString(packet.getWidth()));
			Attribute pktHeight = new Attribute("height", Integer.toString(packet.getHeight()));
			Attribute pktMolteplicity = new Attribute("molteplicity", Integer.toString(packet.getMolteplicity()));
			Attribute pktColor = new Attribute("color", Integer.toString(packet.getColor().getRGB()));
			pktElm.setAttributes(Arrays.asList(pktWidth, pktHeight, pktMolteplicity, pktColor));
			pktConf.addContent(pktElm);
		}
		confRoot.addContent(pktConf);
		
		return confRoot;
	}
	
	private ProblemConfiguration XML2ProblemConf(Element problemConf) throws JDOMException {
		Element binConf = problemConf.getChild("bin");
		if (binConf == null) {
			throw new JDOMException("No BIN specified");
		}
		String widthStr = binConf.getAttributeValue("width");
		String heightStr = binConf.getAttributeValue("height");
		BinConfiguration binc;
		try {
			int width = Integer.parseInt(widthStr);
			int height = Integer.parseInt(heightStr);
			binc = new BinConfiguration(width, height);
		} catch (NumberFormatException nfe) {
			throw new JDOMException("Malformed BIN width/height");
		}
		
		Element pktsElm = problemConf.getChild("packets");
		if (pktsElm == null) {
			throw new JDOMException("No PACKETS specified");
		}
		List<PacketConfiguration> packets = new LinkedList<PacketConfiguration>();
		for (Object pktObj : pktsElm.getChildren()) {
			Element pktElm = (Element) pktObj;
			String pktWidthStr = pktElm.getAttributeValue("width");
			String pktHeightStr = pktElm.getAttributeValue("height");
			String pktMolteplicityStr = pktElm.getAttributeValue("molteplicity");
			String pktColorStr = pktElm.getAttributeValue("color");
			
			try {
				int pktWidth = Integer.parseInt(pktWidthStr);
				int pktHeight = Integer.parseInt(pktHeightStr);
				int pktMolteplicity = Integer.parseInt(pktMolteplicityStr);
				int pktColor = Integer.parseInt(pktColorStr);
				
				PacketConfiguration pc = new PacketConfiguration(pktWidth, 
						pktHeight, pktMolteplicity, new Color(pktColor));
				packets.add(pc);
			} catch (NumberFormatException nfe) {
				throw new JDOMException("Malformed packet");
			}
		}
		
		return new ProblemConfiguration(binc, packets);
	}
	
	public String getCoreName() {
		return coreName;
	}
	public void setCoreName(String coreName) {
		this.coreName = coreName;
	}
	
	/**
	 * 
	 * @return can return <code>null</code>
	 */
	public Object getCoreConfiguration() {
		return coreConfiguration;
	}
	
	/**
	 * 
	 * @param coreConfiguration accept <code>null</code> configurations
	 */
	public void setCoreConfiguration(Object coreConfiguration) {
		this.coreConfiguration = coreConfiguration;
	}
	public ProblemConfiguration getProblemConfiguration() {
		return problemConfiguration;
	}
	public void setProblemConfiguration(ProblemConfiguration problemConfiguration) {
		this.problemConfiguration = problemConfiguration;
	}
	
}
