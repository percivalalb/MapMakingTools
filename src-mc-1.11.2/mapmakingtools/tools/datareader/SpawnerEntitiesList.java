package mapmakingtools.tools.datareader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mapmakingtools.MapMakingTools;

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
		try {
			BufferedReader paramReader = new BufferedReader(new InputStreamReader(MapMakingTools.class.getResourceAsStream("/assets/mapmakingtools/data/spawnerentities.txt"))); 
			String line = "";
			while((line = paramReader.readLine()) != null) {
				
				if(line.isEmpty() || line.startsWith("#"))
					continue;
				
				String[] dataParts = line.split(" ~~~ ");
				
				String entity = dataParts[0];
				
				addEntity(entity);
				
				if(dataParts.length >= 2) {
					String nbtData = dataParts[1];
					addNBTForEntity(entity, nbtData);
				}
			}
	    }
		catch(Exception e) {
			e.printStackTrace();
	    }
		finally {
			Collections.sort(entityList);
		}
	}
}
