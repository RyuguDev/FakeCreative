/*
 * FakeCreative
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.fakecreative.handlers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.StringUtils;
import me.RockinChaos.fakecreative.utils.api.LegacyAPI;

public class ItemHandler {
	
   /**
    * Creates a new ItemStack with the specified material, count, 
    * adding a invisible glowing enchant, custom name, and lore.
    * 
    * @param material - The material name and data value of the ItemStack, Example: "WOOL:14".
    * @param count - The stack size of the ItemStack.
    * @param glowing - If the ItemStack should visually glow.
    * @param name - The custom name to be added to the ItemStack.
    * @param lores - The custom lore to be added to the ItemStack.
    */
    public static ItemStack getItem(String material, final int count, final boolean glowing, boolean hideAttributes, String name, final String... lores) { ///
        ItemStack tempItem; 
        String refMat = "";
        if (!ServerUtils.hasSpecificUpdate("1_8") && material.equals("BARRIER")) { material = "WOOL:14"; }
        if (material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("AIR:0")) { material = "GLASS_PANE"; }
        if (material.equalsIgnoreCase("WATER_BOTTLE")) { refMat = material; material = "POTION"; }
        if (getMaterial(material, null) == null) { material = "STONE"; } 
        if (ServerUtils.hasSpecificUpdate("1_13")) { tempItem = new ItemStack(getMaterial(material, null), count); } 
        else { short dataValue = 0; if (material.contains(":")) { String[] parts = material.split(":"); material = parts[0]; dataValue = (short) Integer.parseInt(parts[1]); } 
        tempItem = LegacyAPI.newItemStack(getMaterial(material, null), count, dataValue); }
        if (glowing) { tempItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1); }
        ItemMeta tempMeta = tempItem.getItemMeta();
        if (ServerUtils.hasSpecificUpdate("1_8")) { tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS); }
        if (name != null) { name = StringUtils.colorFormat(name); tempMeta.setDisplayName(name); }
        if (lores != null && lores.length != 0) {
        	ArrayList<String> loreList = new ArrayList<String>();
        	for (String loreString: lores) { 
        		if (!loreString.isEmpty()) {
        			if (loreString.contains("/n")) {
        				String[] loreSplit = loreString.split(" /n ");
        				for (String loreStringSplit : loreSplit) { loreList.add(StringUtils.colorFormat(loreStringSplit)); }
        			} else { loreList.add(StringUtils.colorFormat(loreString)); }
        		} 
        	}
        	tempMeta.setLore(loreList);
        }
		if (ServerUtils.hasSpecificUpdate("1_8") && hideAttributes) {
			tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
			tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
			tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
			tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_PLACED_ON);
			tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_POTION_EFFECTS);
			tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
		}
		if (refMat.equalsIgnoreCase("WATER_BOTTLE")) { 
			((PotionMeta)tempMeta).setBasePotionData(new PotionData(PotionType.WATER));
		}
        tempItem.setItemMeta(tempMeta);
        return tempItem;
    }
    
   /**
    * Gets the Bukkit Material instance of the specified String material name and data value.
    * 
    * @param material - The item ID or Bukkit Material String name.
    * @param data - The data value of the item, usually this is zero.
    * @return The proper Bukkit Material instance.
    */
	public static Material getMaterial(String material, String data) {
		try {
			boolean isLegacy = (data != null);
			if (material.contains(":")) { String[] parts = material.split(":"); material = parts[0]; if (!parts[1].equalsIgnoreCase("0")) { data = parts[1]; isLegacy = true; } }
			if (StringUtils.isInt(material) && !ServerUtils.hasSpecificUpdate("1_13")) {
				return LegacyAPI.findMaterial(Integer.parseInt(material));
			} else if (StringUtils.isInt(material) && ServerUtils.hasSpecificUpdate("1_13") || isLegacy && ServerUtils.hasSpecificUpdate("1_13")) {
				int dataValue;
				if (!StringUtils.isInt(material)) { material = "LEGACY_" + material; }
				if (data != null) { dataValue = Integer.parseInt(data); } else { dataValue = 0; }
				if (!StringUtils.isInt(material)) { return LegacyAPI.getMaterial(Material.getMaterial(material.toUpperCase()), (byte) dataValue); } 
				else { return LegacyAPI.getMaterial(Integer.parseInt(material), (byte) dataValue); }
			} else if (!ServerUtils.hasSpecificUpdate("1_13")) {
				return Material.getMaterial(material.toUpperCase());
			} else {
				return Material.matchMaterial(material.toUpperCase());
			}
		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		return null;
	}
    
   /**
    * Checks if the ItemStack contents are NULL or empty.
    * 
    * @param contents - The ItemStack contents to be checked.
    * @return If the contents do not exist.
    */
    public static boolean isContentsEmpty(final ItemStack[] contents) {
    	int size = 0; 
    	for (ItemStack itemStack: contents) { 
    		if (itemStack == null || itemStack.getType().equals(Material.AIR)) { 
    			size++; 
    		} 
    	}
    	return (size == contents.length);
    }
    
   /**
    * Copies the specified ItemStack contents.
    * 
    * @param contents - The ItemStack contents to be copied.
    * @return The copied ItemStack contents.
    */
    public static ItemStack[] cloneContents(final ItemStack[] contents) {
    	int itr = 0;
    	ItemStack[] copyContents = contents;
    	for (ItemStack itemStack: contents) {
    		if (copyContents[itr] != null) {
    			copyContents[itr] = itemStack.clone();
    		}
    		itr++;
    	}
    	return copyContents;
    }
    
    
   /**
    * Converts the Inventory to a Base64 String.
    * This is a way of encrypting a Inventory to be decrypted and referenced later.
    * 
    * @param inventory - The Inventory to be converted.
    * @return The Base64 String of the Inventory.
    */
	public static String serializeInventory(final Inventory inventory) {
	    try {
	    	java.io.ByteArrayOutputStream str = new java.io.ByteArrayOutputStream();
	        org.bukkit.util.io.BukkitObjectOutputStream data = new org.bukkit.util.io.BukkitObjectOutputStream(str);
	        data.writeInt(inventory.getSize());
	        for (int i = 0; i < inventory.getSize(); i++) {
	            data.writeObject(inventory.getItem(i));
	        }
	        data.close();
	        return Base64.getEncoder().encodeToString(str.toByteArray());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "";
	}

   /**
    * Converts the Base64 String to a Inventory.
    * This is a way of decrypting an encrypted Inventory to be referenced.
    * 
    * @param inventoryData - The Base64 String to be converted to an Inventory.
    * @return The Inventory instance that has been deserialized.
    */
	public static Inventory deserializeInventory(final String inventoryData) {
	    try {
	    	java.io.ByteArrayInputStream stream = new java.io.ByteArrayInputStream(Base64.getDecoder().decode(inventoryData));
	        org.bukkit.util.io.BukkitObjectInputStream data = new org.bukkit.util.io.BukkitObjectInputStream(stream);
	        Inventory inventory = Bukkit.createInventory(null, data.readInt());
	        for (int i = 0; i < inventory.getSize(); i++) {
	            inventory.setItem(i, (ItemStack) data.readObject());
	        }
	        data.close();
	        return inventory;
	    } catch (EOFException e) {
	    	return null;
	    } catch (Exception e) {
	        ServerUtils.sendDebugTrace(e);
	    }
	    return null;
	}
}