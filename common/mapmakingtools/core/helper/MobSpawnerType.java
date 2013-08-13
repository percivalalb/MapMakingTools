package mapmakingtools.core.helper;

import java.util.ArrayList;
import java.util.List;

public class MobSpawnerType {

	public static List<String> validMobs = new ArrayList<String>();
	
	public static void add(String mobId) {
		if(!validMobs.contains(mobId)) {
			validMobs.add(mobId);
		}
	}
	
	public static void addToList(List<String> list) {
		for(String str : validMobs) {
			list.add(str);
		}
	}
}
