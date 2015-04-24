package mapmakingtools.helper;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;

/**
 * @author ProPercivalalb
 */
public class TextHelper {

	public static ArrayList<String> splitInto(int maxLength, FontRenderer font, String... text) {
		ArrayList<String> list = new ArrayList<String>(); 
		
		String temp = "";
		for(String line : text) {
			String[] split = line.split(" ");
			
			for(int i = 0; i < split.length; ++i) {
				String str = split[i];
				int length = font.getStringWidth(temp + str);
				
				if(length > maxLength) {
					list.add(temp);
					temp = "";
				}
				
				temp += str;
				
				if(i == split.length - 1) {
					list.add(temp);
					temp = "";
				}
				else
					temp += " ";
			}
		}

	    return list;
	}
}
