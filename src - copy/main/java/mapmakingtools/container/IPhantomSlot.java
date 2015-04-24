package mapmakingtools.container;

/**
 * @author ProPercivalalb
 */
public interface IPhantomSlot {

	boolean canAdjust();
	
	public boolean canBeUnlimited();
	public void setIsUnlimited(boolean isUnlimited);
	public boolean isUnlimited();
}