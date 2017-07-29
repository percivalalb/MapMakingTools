package mapmakingtools.tools.datareader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Stream;

import mapmakingtools.MapMakingTools;

public class DataReader {

	/** Returns a Stream of all the lines that aren't empty and don't start in # **/
	public static Stream<String> loadResource(String resourceName) {
		BufferedReader paramReader = new BufferedReader(new InputStreamReader(MapMakingTools.class.getResourceAsStream(resourceName))); 
		Stream<String> lines = paramReader.lines();
		lines = lines.filter(line -> !line.startsWith("#"));
		lines = lines.filter(line -> !line.replaceAll("\\s", "").isEmpty());
		return lines;
	}
}
