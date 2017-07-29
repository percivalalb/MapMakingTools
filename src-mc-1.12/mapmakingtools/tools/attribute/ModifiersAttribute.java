package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.client.gui.button.GuiSmallButtonData;
import mapmakingtools.client.render.RenderUtil;
import mapmakingtools.helper.Numbers;
import mapmakingtools.helper.ReflectionHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

/**
 * @author ProPercivalalb
 */
public class ModifiersAttribute extends IItemAttribute {
	
	private List<GuiTextField> fld_attack = new ArrayList<GuiTextField>();
	private List<GuiSmallButtonData> btn_operation = new ArrayList<GuiSmallButtonData>();
	private List<GuiButton> but_add = new ArrayList<GuiButton>();
	private GuiButton[] btn_remove;
	private GuiButton btn_convert;
	private GuiButton btn_removeall;
	private GuiButtonData btn_slot;
	private int slotSelected = 0;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(data >= 0 && data < MODIFIERS.length) {
			if(!Strings.isNullOrEmpty(this.fld_attack.get(data).getText()) && Numbers.isDouble(this.fld_attack.get(data).getText())) {
				EntityEquipmentSlot equipmentSlot = EntityEquipmentSlot.values()[this.btn_slot.getData()];
				
				this.onItemCreation(stack, -1);
				if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AttributeModifiers", 9))
		        {
			        NBTTagList nbttaglist = stack.getTagCompound().getTagList("AttributeModifiers", 10);
			        for(int i = 0; i < nbttaglist.tagCount(); i++) {
			        	NBTTagCompound compound = nbttaglist.getCompoundTagAt(i);
			        	if(compound.hasKey("AttributeName", 8))
			        		if(compound.getString("AttributeName").equals(MODIFIERS[data].attributeName) && (!compound.hasKey("Slot", 8) || compound.getString("Slot").equals(equipmentSlot.getName())))
			        			nbttaglist.removeTag(i);
			        }
		        }

				double amount = Numbers.getDouble(this.fld_attack.get(data).getText());
				int operation = this.btn_operation.get(data).getData();
				amount /= (operation > 0 ? 100 : 1);
				stack.addAttributeModifier(MODIFIERS[data].attributeName, MODIFIERS[data].getEdited(amount, operation), equipmentSlot);
					
			}
		}
		else if(data >= MODIFIERS.length && data < MODIFIERS.length * 2) {
			this.onItemCreation(stack, -1);
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AttributeModifiers", 9)) {
	            NBTTagList nbttaglist = stack.getTagCompound().getTagList("AttributeModifiers", 10);
				
				for(int k = 0; k < nbttaglist.tagCount(); k++) {					
					NBTTagCompound nbt = nbttaglist.getCompoundTagAt(k);
					if(nbt.getString("AttributeName").equals(MODIFIERS[data - MODIFIERS.length].attributeName))
						nbttaglist.removeTag(k);
				}
			}
		}
		else if(data == -1) {
			
			if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("AttributeModifiers", 9)) {

				for(EntityEquipmentSlot equipmentSlot : EntityEquipmentSlot.values()) {
					Multimap<String, AttributeModifier> builtIn = stack.getAttributeModifiers(equipmentSlot);
					
					 for(Entry<String, AttributeModifier> entry : builtIn.entries()) {
						 stack.addAttributeModifier(entry.getKey(), entry.getValue(), equipmentSlot);
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
		Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers(EntityEquipmentSlot.values()[this.btn_slot.getData()]);
		for(int i = 0; i < MODIFIERS.length; i++) {
			boolean foundAtAll = false;
			
			 for(Entry<String, AttributeModifier> entry : modifiers.entries()) {
				 String key = (String)entry.getKey();
				 AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
	            
	           
				 if(key.equals(MODIFIERS[i].attributeName)) {
					 this.but_add.get(i).displayString = "Set";
	            	
					 this.fld_attack.get(i).setText("" + (attributemodifier.getAmount() * (attributemodifier.getOperation() > 0 ? 100 : 1)));
					 this.btn_operation.get(i).setData(attributemodifier.getOperation());
					 this.btn_operation.get(i).displayString = "" + this.btn_operation.get(i).getData();
					 if(this.btn_remove[i] == null) {
						 this.btn_remove[i] = new GuiSmallButton(i + MODIFIERS.length, this.x + 310, this.y + 38 + i * 17, 16, 16, "-");
						 itemEditor.getButtonList().add(this.btn_remove[i]);
					 }
					 foundAtAll = true;
				 }
	    
			}

			if(!foundAtAll) {
				this.but_add.get(i).displayString = "Add";
						
				this.fld_attack.get(i).setText("");
				this.btn_operation.get(i).setData(0);
				this.btn_operation.get(i).displayString = "" + this.btn_operation.get(i).getData();
				if(this.btn_remove[i] != null) {
					itemEditor.getButtonList().remove(this.btn_remove[i]);
					this.btn_remove[i] = null;
				}
				
			}	
			 
		}
		
		this.btn_convert.enabled = !stack.hasTagCompound() || !stack.getTagCompound().hasKey("AttributeModifiers", 9);
		this.btn_removeall.enabled = !this.btn_convert.enabled;
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
		itemEditor.getFontRenderer().drawString("OP", x + 250, y + 25, 16777120);
		for(int i = 0; i < MODIFIERS.length; i++)
			itemEditor.getFontRenderer().drawString(I18n.translateToLocal("attribute.name." + MODIFIERS[i].attributeName), x + 6, y + 42 + i * 17, 16777120);
	}

	int x;
	int y;
	int width;
	int height;
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.fld_attack.clear();
		this.btn_operation.clear();
		this.but_add.clear();
		this.btn_remove = new GuiButton[MODIFIERS.length];
		this.btn_slot = new GuiButtonData(-3, x + 2, y + 16, 80, 20, EntityEquipmentSlot.MAINHAND.getName());
		this.btn_slot.setData(this.slotSelected);
		itemEditor.getButtonList().add(this.btn_slot);
		
		for(int i = 0; i < MODIFIERS.length; i++) {
			this.btn_operation.add(new GuiSmallButtonData(i + 2 * this.MODIFIERS.length, this.x + 247, this.y + 38 + i * 17, 16, 16, "0"));
			itemEditor.getButtonList().add(this.btn_operation.get(i));
			this.but_add.add(new GuiSmallButton(i, x + 275, y + 38 + i * 17, 40, 16, "Add"));
			itemEditor.getButtonList().add(this.but_add.get(i));
			this.fld_attack.add(new GuiTextField(i, itemEditor.getFontRenderer(), x + 122, y + 39 + i * 17, 120, 14));
			itemEditor.getTextBoxList().add(this.fld_attack.get(i));
		}

		
		this.btn_convert = new GuiButton(-1, x + 10, y + height - 44, 130, 20, "Convert default to nbt");
		itemEditor.getButtonList().add(this.btn_convert);
		
		this.btn_removeall = new GuiButton(-2, x + 10, y + height - 23, 130, 20, "Remove all Modifiers");
		itemEditor.getButtonList().add(this.btn_removeall);
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id >= 0 && button.id < MODIFIERS.length) {
			itemEditor.sendUpdateToServer(button.id);
		}
		else if(button.id >= MODIFIERS.length && button.id < MODIFIERS.length * 2) {
			this.fld_attack.get(button.id - MODIFIERS.length).setText("");
			itemEditor.sendUpdateToServer(button.id);
		}
		
		else if(button.id >= MODIFIERS.length * 2 && button.id < MODIFIERS.length * 3) {
			GuiSmallButtonData btn = this.btn_operation.get(button.id - MODIFIERS.length * 2);
			btn.setData((btn.getData() + 1) % 3);
			btn.displayString = "" + btn.getData();
			//itemEditor.sendUpdateToServer(button.id);
		}
		else if(button.id == -1) {
			itemEditor.sendUpdateToServer(-1);
		}
		else if(button.id == -2) {
			itemEditor.sendUpdateToServer(-2);
		}
		else if(button.id == -3) {
			this.btn_slot.setData((this.btn_slot.getData() + 1) % EntityEquipmentSlot.values().length);
			this.slotSelected = this.btn_slot.getData();
			this.btn_slot.displayString = EntityEquipmentSlot.values()[this.btn_slot.getData()].getName();
			itemEditor.sendUpdateToServer(-3);
		}
	}
	
	@Override
	public void drawToolTips(IGuiItemEditor guiItemEditor, int xMouse, int yMouse) {
		if(xMouse >= x + 250 && xMouse <= x + 260 && yMouse >= y + 25 && yMouse <= y + 25 + guiItemEditor.getFontRenderer().FONT_HEIGHT) {
			List<String> list = new ArrayList<String>();
			list.add(TextFormatting.GREEN + "Operation");
			list.add(" 0: Adds the modifiers' amount to the current");
			list.add("    value of the attribute");
			list.add(" 1: Multiplies the current value of the attribute");
			list.add("    by the sum of all the modifiers' amount");
			list.add(" 2: Multiplies the current value of the attribute");
			list.add("    by the product of all the modifiers' amount");
			RenderUtil.drawHoveringText(list, xMouse, yMouse, this.width, this.height, guiItemEditor.getFontRenderer(), true);
		}
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		
	}
	
	public static int ADD_OPERATION = 0;
	public static int MULT_PERCENTAGE_OPERATION = 1;
	public static int ADD_PERCENTAGE_OPERATION = 2;
	
    protected static final UUID ATTACK_DAMAGE_MODIFIER = ReflectionHelper.getField(Item.class, UUID.class, null, 7);
    protected static final UUID ATTACK_SPEED_MODIFIER = ReflectionHelper.getField(Item.class, UUID.class, null, 8);
	private Modifier ATTACK_DAMAGE = new Modifier(SharedMonsterAttributes.ATTACK_DAMAGE, new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));
	private Modifier ATTACK_SPEED = new Modifier(SharedMonsterAttributes.ATTACK_SPEED, new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));
	private Modifier KNOCKBACK_RESISTANCE = new Modifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));
	private Modifier MAX_HEALTH = new Modifier(SharedMonsterAttributes.MAX_HEALTH, new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));
	private Modifier MOVEMENT_SPEED = new Modifier(SharedMonsterAttributes.MOVEMENT_SPEED, new AttributeModifier("Weapon modifier", 0.0D, MULT_PERCENTAGE_OPERATION));
	private Modifier FOLLOW_RANGE = new Modifier(SharedMonsterAttributes.FOLLOW_RANGE, new AttributeModifier("Weapon modifier", 0.0D, MULT_PERCENTAGE_OPERATION));
	private Modifier ARMOR = new Modifier(SharedMonsterAttributes.ARMOR, new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));
	private Modifier ARMOR_TOUGHNESS = new Modifier(SharedMonsterAttributes.ARMOR_TOUGHNESS, new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));
	private Modifier LUCK = new Modifier(SharedMonsterAttributes.LUCK, new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));
	private Modifier SPAWN_REINFORCEMENTS = new Modifier("zombie.spawnReinforcements", new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));
	private Modifier HORSE_JUMP_STRENGTH = new Modifier("horse.jumpStrength", new AttributeModifier("Weapon modifier", 0.0D, ADD_OPERATION));

	private final Modifier[] MODIFIERS = new Modifier[] {ATTACK_DAMAGE, ATTACK_SPEED, KNOCKBACK_RESISTANCE, MAX_HEALTH, MOVEMENT_SPEED, FOLLOW_RANGE, ARMOR, ARMOR_TOUGHNESS, LUCK};
	
	public static class Modifier {
		
		public String attributeName;
		public AttributeModifier modifier;
		
		public Modifier(IAttribute attribute, AttributeModifier modifier) {
			this(attribute.getName(), modifier);
		}
		
		public Modifier(String attributeName, AttributeModifier modifier) {
			this.attributeName = attributeName;
			this.modifier = modifier;
		}
		
		public AttributeModifier getEdited(double amountIn, int operationIn) {
			return new AttributeModifier(this.modifier.getName(), amountIn, operationIn);
		}
	}
	
	
}
