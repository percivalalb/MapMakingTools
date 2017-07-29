package mapmakingtools.tools.datareader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.Numbers;

/**
 * @author ProPercivalalb
 */
public class SpawnerEntitiesList {

	private static List<String> entityList = new ArrayList<String>();
	private static Map<String, String> entityNBTMap = new Hashtable<String, String>();
	
	public static List<String> getEntities() {
		return entityList;
	}
	
	public static void addEntity(String entity) {
		if(entityList.contains(entity))
			return;
		entityList.add(entity);
	}
	
	public static void addNBTForEntity(String entity, String nbtData) {
		if(entityNBTMap.containsKey(entityList))
			return;
		entityNBTMap.put(entity, nbtData);
	}
	
	public static void readDataFromFile() {
		Stream<String> lines = DataReader.loadResource("/assets/mapmakingtools/data/spawnerentities.txt");
		Stream<String[]> parts = lines.map(line -> line.split(" ~~~ ")).filter(arr -> arr.length > 1);
		parts.forEach(arr -> addEntity(arr[0]));
		
		Collections.sort(entityList);
	}
}
