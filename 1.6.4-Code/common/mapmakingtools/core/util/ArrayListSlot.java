package mapmakingtools.core.util;

import java.util.ArrayList;

import mapmakingtools.core.helper.LogHelper;
import net.minecraft.inventory.Slot;

/**
 * @author ProPercivalalb
 */
public class ArrayListSlot extends ArrayList<Slot> {

	@Override
	public Slot get(int index) {
		if(index < 0 || index >= size())
			return null;
		return (Slot)super.get(index);
	}
}
