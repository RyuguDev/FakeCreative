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
package me.RockinChaos.fakecreative.utils.api;

import java.util.EnumSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.StringUtils;

/**
 * Welcome to the magical land of make-believe.
 * These are Deprecated Legacy Methods and/or non-functioning methods
 * that exist to support legacy versions of Minecraft.
 * 
 */
@SuppressWarnings("deprecation")
public class LegacyAPI {
   /**
    * Updates the Players Inventory.
    * 
    * @param player - The Player to have their Inventory updated.
    */
    public static void updateInventory(final Player player) {
    	player.updateInventory();
    }
	
   /**
    * Creates a new ItemStack.
    * 
    * @param material - The Material to be set to the ItemStack.
    * @param count - The ItemStack size.
    * @param dataValue - The Data Value to set to the ItemStack.
    * @return The new ItemStack.
    */
    public static ItemStack newItemStack(final Material material, final int count, final short dataValue) {
    	return new ItemStack(material, count, dataValue);
    }

   /**
    * Matches the Material from its Bukkit Material and Data Value.
    * 
    * @param typeID - The ID of the Material to be fetched.
    * @param dataValue - The Data value to be matched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material getMaterial(final int typeID, final byte dataValue) {
		return FakeCreative.getInstance().getServer().getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(findMaterial(typeID), dataValue));
    }
    
   /**
    * Matches the Material from its Bukkit Material and Data Value.
    * 
    * @param material - The Material to be matched.
    * @param dataValue - The Data value to be matched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material getMaterial(final Material material, final byte dataValue) {
  		return FakeCreative.getInstance().getServer().getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(material, dataValue));
    }
    
   /**
    * Gets the Material from its corresponding ID.
    * 
    * @param typeID - The ID of the Material to be fetched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material findMaterial(final int typeID) {
        final Material[] foundMaterial = new Material[1];
        EnumSet.allOf(Material.class).forEach(material -> { try { if (StringUtils.containsIgnoreCase(material.toString(), "LEGACY_") && material.getId() == typeID || !ServerUtils.hasSpecificUpdate("1_13") && material.getId() == typeID) { foundMaterial[0] = material; } } catch (Exception e) { }});
        return foundMaterial[0];
    }
	
   /**
    * Gets the Bukkit Player from their String name.
    * 
    * @param playerName - The String name of the Bukkit Player.
    * @return The found Player.
    */
	public static Player getPlayer(final String playerName) {
		return Bukkit.getPlayer(playerName);
	}
}