package mapmakingtools.core.util;

import java.util.ArrayList;

import mapmakingtools.core.helper.LogHelper;
import net.minecraft.inventory.Slot;

public class ArrayListSlot extends ArrayList<Slot> {

	@Override
	public Slot get(int index) {
		if(index < 0 || index >= size()) {
			LogHelper.logDebug("Saved from crash.");
			return null;
		}
		return (Slot)super.get(index);
	}
}
