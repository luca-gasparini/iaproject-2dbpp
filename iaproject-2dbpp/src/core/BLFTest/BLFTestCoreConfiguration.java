package core.BLFTest;

import java.io.Serializable;

public class BLFTestCoreConfiguration implements Serializable {

	private static final long serialVersionUID = 1064916421235126471L;

	private boolean isSelected;
	
	public BLFTestCoreConfiguration(boolean sel) {
		this.isSelected = sel;
	}
	
	public boolean getSelected() {
		return this.isSelected;
	}
}
