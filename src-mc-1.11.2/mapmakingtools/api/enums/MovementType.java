package mapmakingtools.api.enums;

/**
 * @author ProPercivalalb
 */
public enum MovementType {

	_000_("0"),
	_090_("90"),
	_180_("180"),
	_270_("270"),
	_X_("x"),
	_Y_("y"),
	_Z_("z");
	
	private String marker;
	
	MovementType(String marker) {
		this.marker = marker;
	}
	
	public String getMarker() {
		return this.marker;
	}
	
	public static MovementType getRotation(String rot) {
		for(int i = 0; i < values().length; ++i)
			if(rot.equals(values()[i].marker))
				return values()[i];
		return null;
	}
}
