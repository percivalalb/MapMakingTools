package mapmakingtools.api;

/**
 * @author ProPercivalalb
 */
public enum Rotation {

	_000_(0),
	_090_(90),
	_180_(180),
	_270_(270);
	
	private int value;
	
	Rotation(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public int getMultiplier() {
		return this.getValue() / 90;
	}
	
	public static Rotation getRotation(int rot) {
		for(int i = 0; i < values().length; ++i)
			if(rot == values()[i].value)
				return values()[i];
		return null;
	}
}
