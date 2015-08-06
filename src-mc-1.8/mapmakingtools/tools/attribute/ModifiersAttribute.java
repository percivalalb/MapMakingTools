package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class ModifiersAttribute extends IItemAttribute {
	
	private List<GuiTextField> fld_attack = new ArrayList<GuiTextField>();
	private List<GuiButton> btn_attack = new ArrayList<GuiButton>();
	private GuiButton[] btn_remove;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(data >= 0 && data < MODIFIERS.length) {
			if(!Strings.isNullOrEmpty(this.fld_attack.get(data).getText()) && NumberParse.isDouble(this.fld_attack.get(data).getText())) {
				if(!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
	
				if(!stack.getTagCompound().hasKey("AttributeModifiers", 9))
					stack.getTagCompound().setTag("AttributeModifiers", new NBTTagList());
				else {
					for(int k = 0; k < stack.getTagCompound().getTagList("AttributeModifiers", 10).tagCount(); k++) {
						NBTTagCompound nbt = stack.getTagCompound().getTagList("AttributeModifiers", 10).getCompoundTagAt(k);
						if(nbt.getString("AttributeName").equals(MODIFIERS[data].attributeName))
								stack.getTagCompound().getTagList("AttributeModifiers", 10).removeTag(k);
					}
				}
					
				stack.getTagCompound().getTagList("AttributeModifiers", 10).appendTag(MODIFIERS[data].writeToNBT(NumberParse.getDouble(this.fld_attack.get(data).getText())));
			}
		}
		if(data >= MODIFIERS.length && data < MODIFIERS.length * 2) {
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AttributeModifiers", 9)) {
				for(int k = 0; k < stack.getTagCompound().getTagList("AttributeModifiers", 10).tagCount(); k++) {
					NBTTagCompound nbt = stack.getTagCompound().getTagList("AttributeModifiers", 10).getCompoundTagAt(k);
					if(nbt.getString("AttributeName").equals(MODIFIERS[data - MODIFIERS.length].attributeName)) {
						stack.getTagCompound().getTagList("AttributeModifiers", 10).removeTag(k);
					}
				}
			}
		}
		else if(data == -1) {
			
			
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
		else if(data == -2) {
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
		for(int i = 0; i < MODIFIERS.length; i++) {
			if(!modifiers.isEmpty()) {
	        	
				Iterator iterator = modifiers.entries().iterator();
	            
				boolean foundAtAll = false;
				while(iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
	            	
					String key = (String)entry.getKey();
					AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
	            
	           
	            	if(key.equals(MODIFIERS[i].attributeName)) {
	            		this.btn_attack.get(i).displayString = "Set";
	            		if(first)
	            			this.fld_attack.get(i).setText("" + (attributemodifier.getAmount() * (attributemodifier.getOperation() > 0 ? 100 : 1)));
	            		if(this.btn_remove[i] == null) {
		            		this.btn_remove[i] = new GuiSmallButton(i + MODIFIERS.length, this.x + 210, this.y + 16 + i * 17, 16, 16, "-");
		        			itemEditor.getButtonList().add(this.btn_remove[i]);
	            		}
	        			foundAtAll = true;
	            	}
	    
				}
	        
				if(!foundAtAll) {
					this.btn_attack.get(i).displayString = "Add";
					if(first)
						this.fld_attack.get(i).setText("");
					if(this.btn_remove[i] != null) {
						itemEditor.getButtonList().remove(this.btn_remove[i]);
						this.btn_remove[i] = null;
					}
					//this.btn_remove[i]
				}
			}
			else {
				this.btn_attack.get(i).displayString = "Add";
				if(first)
					this.fld_attack.get(i).setText("");
				if(this.btn_remove[i] != null) {
					itemEditor.getButtonList().remove(this.btn_remove[i]);
					this.btn_remove[i] = null;
				}
			}
	    }
		
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
		
		for(int i = 0; i < MODIFIERS.length; i++)
			itemEditor.getFontRenderer().drawString(StatCollector.translateToLocal("attribute.name." + MODIFIERS[i].attributeName), x + 6, y + 20 + i * 17, 16777120);
	}

	int x;
	int y;
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		fld_attack.clear();
		btn_attack.clear();
		btn_remove = new GuiButton[MODIFIERS.length];
		for(int i = 0; i < MODIFIERS.length; i++) {
			this.btn_attack.add(new GuiSmallButton(i, x + 170, y + 16 + i * 17, 40, 16, "Add"));
			itemEditor.getButtonList().add(this.btn_attack.get(i));
			this.fld_attack.add(new GuiTextField(i, itemEditor.getFontRenderer(), x + 122, y + 17 + i * 17, 40, 14));
			itemEditor.getTextBoxList().add(this.fld_attack.get(i));
		}

		
		GuiButton btn_convert = new GuiButton(-1, x + 10, y + height - 44, 130, 20, "Convert default to nbt");
		itemEditor.getButtonList().add(btn_convert);
		
		GuiButton btn_remove = new GuiButton(-2, x + 10, y + height - 23, 130, 20, "Remove all Modifiers");
		itemEditor.getButtonList().add(btn_remove);
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id >= 0 && button.id < MODIFIERS.length) {
			itemEditor.sendUpdateToServer(button.id);
		}
		else if(button.id >= MODIFIERS.length && button.id < MODIFIERS.length * 2) {
			itemEditor.sendUpdateToServer(button.id);
		}
		else if(button.id == -1) {
			itemEditor.sendUpdateToServer(-1);
		}
		else if(button.id == -2) {
			itemEditor.sendUpdateToServer(-2);
		}
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		
	}
	
	public static int ADD_OPERATION = 0;
	public static int MULT_PERCENTAGE_OPERATION = 1;
	public static int ADD_PERCENTAGE_OPERATION = 2;
	
	private static final UUID itemModifierUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	private Modifier ATTACK_DAMAGE = new Modifier(SharedMonsterAttributes.attackDamage, "Weapon modifier", ADD_OPERATION, itemModifierUUID);
	private Modifier KNOCKBACK_RESISTANCE = new Modifier(SharedMonsterAttributes.knockbackResistance, "Weapon modifier", ADD_OPERATION, itemModifierUUID);
	private Modifier MAX_HEALTH = new Modifier(SharedMonsterAttributes.maxHealth, "Weapon modifier", MULT_PERCENTAGE_OPERATION, itemModifierUUID);
	private Modifier MOVEMENT_SPEED = new Modifier(SharedMonsterAttributes.movementSpeed, "Weapon modifier", MULT_PERCENTAGE_OPERATION, itemModifierUUID);
	private Modifier FOLLOW_RANGE = new Modifier(SharedMonsterAttributes.followRange, "Weapon modifier", MULT_PERCENTAGE_OPERATION, itemModifierUUID);
	//private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    //private static Modifier sprintingSpeedBoostModifier = new Modifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
	
	private final Modifier[] MODIFIERS = new Modifier[] {ATTACK_DAMAGE, KNOCKBACK_RESISTANCE, MAX_HEALTH, MOVEMENT_SPEED, FOLLOW_RANGE};
	
	public static class Modifier {
		
		public String attributeName;
		public String name;
		public int operation;
		public UUID uuid;
		
		public Modifier(IAttribute attribute, String name, int operation, UUID uuid) {
			this(attribute.getAttributeUnlocalizedName(), name, operation, uuid);
		}
		
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
            nbttagcompound.setDouble("Amount", this.operation > 0 ? amount / 100 : amount);
            nbttagcompound.setInteger("Operation", this.operation);
            nbttagcompound.setLong("UUIDMost", this.uuid.getMostSignificantBits());
            nbttagcompound.setLong("UUIDLeast", this.uuid.getLeastSignificantBits());
            
            return nbttagcompound;
		}
	}
	
	
}
