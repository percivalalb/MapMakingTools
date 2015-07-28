package mapmakingtools.tools.attribute;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.helper.NumberParse;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class ModifiersAttribute extends IItemAttribute {
	
	private GuiTextField fld_attack;
	private GuiButton btn_attack;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(!Strings.isNullOrEmpty(this.fld_attack.getText()) && data == 0) {
			
			if(NumberParse.isDouble(this.fld_attack.getText())) {
				if(!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				
				/**
				if(!stack.getTagCompound().hasKey("AttributeModifiers", 9)) {
					Multimap modifiers = stack.getAttributeModifiers();
		
			        if(!modifiers.isEmpty()) {
			        	stack.getTagCompound().setTag("AttributeModifiers", new NBTTagList());
			        	
			            Iterator iterator = modifiers.entries().iterator();
			            
			            while(iterator.hasNext()) {
			            	Entry entry = (Entry)iterator.next();
			            	
			            	String key = (String)entry.getKey();
			                AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
			                
			                FMLLog.info(key + " " + attributemodifier);
			                
			                NBTTagCompound nbttagcompound = new NBTTagCompound();
			                nbttagcompound.setString("AttributeName", key);
			                
			                nbttagcompound.setString("Name", attributemodifier.getName());
			                nbttagcompound.setDouble("Amount", attributemodifier.getAmount());
			                nbttagcompound.setInteger("Operation", attributemodifier.getOperation());
			                nbttagcompound.setLong("UUIDMost", attributemodifier.getID().getMostSignificantBits());
			                nbttagcompound.setLong("UUIDLeast", attributemodifier.getID().getLeastSignificantBits());
			                
			                stack.getTagCompound().getTagList("AttributeModifiers", 10).appendTag(nbttagcompound);
			            }
			        }
				}**/
				
				if(!stack.getTagCompound().hasKey("AttributeModifiers", 9))
					stack.getTagCompound().setTag("AttributeModifiers", new NBTTagList());
				else
					for(int i = 0; i < stack.getTagCompound().getTagList("AttributeModifiers", 10).tagCount(); i++) {
						NBTTagCompound nbt = stack.getTagCompound().getTagList("AttributeModifiers", 10).getCompoundTagAt(i);
						if(nbt.getString("AttributeName").equals(ATTACK_DAMAGE.attributeName))
							stack.getTagCompound().getTagList("AttributeModifiers", 10).removeTag(i);
					}
				stack.getTagCompound().getTagList("AttributeModifiers", 10).appendTag(ATTACK_DAMAGE.writeToNBT(NumberParse.getDouble(this.fld_attack.getText())));
			}
		}
		else if(data == 1) {
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			
			if(!stack.getTagCompound().hasKey("AttributeModifiers", 9)) {
				Multimap modifiers = stack.getAttributeModifiers();
	
		        if(!modifiers.isEmpty()) {
		        	stack.getTagCompound().setTag("AttributeModifiers", new NBTTagList());
		        	
		            Iterator iterator = modifiers.entries().iterator();
		            
		            while(iterator.hasNext()) {
		            	Entry entry = (Entry)iterator.next();
		            	
		            	String key = (String)entry.getKey();
		                AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
		                
		                NBTTagCompound nbttagcompound = new NBTTagCompound();
		                nbttagcompound.setString("AttributeName", key);
		                
		                nbttagcompound.setString("Name", attributemodifier.getName());
		                nbttagcompound.setDouble("Amount", attributemodifier.getAmount());
		                nbttagcompound.setInteger("Operation", attributemodifier.getOperation());
		                nbttagcompound.setLong("UUIDMost", attributemodifier.getID().getMostSignificantBits());
		                nbttagcompound.setLong("UUIDLeast", attributemodifier.getID().getLeastSignificantBits());
		                
		                stack.getTagCompound().getTagList("AttributeModifiers", 10).appendTag(nbttagcompound);
		            }
		        }
			}
		}
		else if(data == 2) {
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AttributeModifiers", 9))
				stack.getTagCompound().removeTag("AttributeModifiers");
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.modifiers.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		Multimap modifiers = stack.getAttributeModifiers();
			
		if(!modifiers.isEmpty()) {
	        	
	        Iterator iterator = modifiers.entries().iterator();
	            
	        while(iterator.hasNext()) {
	        	Entry entry = (Entry)iterator.next();
	            	
	            String key = (String)entry.getKey();
	            AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
	            if(key.equals(ATTACK_DAMAGE.attributeName))
	            	this.fld_attack.setText("" + attributemodifier.getAmount());
	        }
		}
		else {
			this.fld_attack.setText("");
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
		
		itemEditor.getFontRenderer().drawString("Attack Damage", x + 6, y + 20, 16777120);
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.btn_attack = new GuiSmallButton(0, x + 130, y + 16, 40, 16, "Set");
		itemEditor.getButtonList().add(this.btn_attack);
		
		
		GuiButton btn_convert = new GuiButton(1, x + 10, y + height - 44, 130, 20, "Convert default to nbt");
		itemEditor.getButtonList().add(btn_convert);
		
		GuiButton btn_remove = new GuiButton(2, x + 10, y + height - 23, 130, 20, "Remove all Modifiers");
		itemEditor.getButtonList().add(btn_remove);
		
		this.fld_attack = new GuiTextField(0, itemEditor.getFontRenderer(), x + 82, y + 17, 40, 14);
		itemEditor.getTextBoxList().add(this.fld_attack);
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(0);
		}
		else if(button.id == 1) {
			itemEditor.sendUpdateToServer(1);
		}
		else if(button.id == 2) {
			itemEditor.sendUpdateToServer(2);
		}
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		
	}
	
	private static final UUID itemModifierUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	private Modifier ATTACK_DAMAGE = new Modifier(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), "Weapon modifier", 0, itemModifierUUID);
	private Modifier OTHER_TEST = new Modifier("generic.test", "Test", 0, itemModifierUUID);
	
	public static class Modifier {
		
		public String attributeName;
		public String name;
		public int operation;
		public UUID uuid;
		
		public Modifier(String attributeName, String name, int operation, UUID uuid) {
			this.attributeName = attributeName;
			this.name = name;
			this.operation = operation;
			this.uuid = uuid;
		}
		
		public NBTTagCompound writeToNBT(double amount) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("AttributeName", this.attributeName);
            
            nbttagcompound.setString("Name", this.name);
            nbttagcompound.setDouble("Amount", amount);
            nbttagcompound.setInteger("Operation", this.operation);
            nbttagcompound.setLong("UUIDMost", this.uuid.getMostSignificantBits());
            nbttagcompound.setLong("UUIDLeast", this.uuid.getLeastSignificantBits());
            
            return nbttagcompound;
		}
	}
	
	
}
