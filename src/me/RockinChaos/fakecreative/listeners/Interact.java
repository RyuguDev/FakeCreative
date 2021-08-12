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
package me.RockinChaos.fakecreative.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.fakecreative.handlers.PlayerHandler;
import me.RockinChaos.fakecreative.utils.SchedulerUtils;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.api.LegacyAPI;

public class Interact implements Listener {
	
   /**
    * Refills the custom item to its original stack size when placing the item.
    * 
    * @param event - PlayerInteractEvent
    */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onCountLock(final PlayerInteractEvent event) {
    	final Player player = event.getPlayer();
    	final ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
    	final int slot = player.getInventory().getHeldItemSlot();
    	if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && item != null && item.getType() != Material.AIR && PlayerHandler.isCreativeMode((Player) event.getPlayer(), true)) {
    		SchedulerUtils.run(() -> {
    			if (player.getInventory().getHeldItemSlot() == slot) {
    				PlayerHandler.setMainHandItem(player, item);
    			} else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
    				player.getInventory().setItem(slot, item);
    			}
    		});
    	}
    }
   /**
    * Refills the custom item to its original stack size when placing the item into a itemframe.
    * 
    * @param event - PlayerInteractEntityEvent
    */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onFrameLock(final PlayerInteractEntityEvent event) {
    	if (event.getRightClicked() instanceof ItemFrame) {
    		try {
    			ItemStack item = null;
    			if (ServerUtils.hasSpecificUpdate("1_9")) {
    				item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString());
    			} else {
    				item = PlayerHandler.getPerfectHandItem(event.getPlayer(), "");
    			}
    			final ItemStack itemStack = (item != null ? item.clone() : item);
    			Player player = event.getPlayer();
    			if (PlayerHandler.isCreativeMode((Player) event.getPlayer(), true)) {
    				SchedulerUtils.run(() -> {
    					if (ServerUtils.hasSpecificUpdate("1_9")) {
    						if (event.getHand().equals(org.bukkit.inventory.EquipmentSlot.HAND)) {
    							PlayerHandler.setMainHandItem(player, itemStack);
    						} else if (event.getHand().equals(org.bukkit.inventory.EquipmentSlot.OFF_HAND)) {
    							PlayerHandler.setOffHandItem(player, itemStack);
    						}
    					} else {
    						PlayerHandler.setMainHandItem(player, itemStack);
    					}
    				});
    			}
    		} catch (Exception e) {
    			ServerUtils.sendDebugTrace(e);
    		}
    	}
    }
	 
   /**
	* Gives the player the item that they are attempting to pick block.
	* 
	* @param event - PlayerInteractEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onPickItem(final PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && PlayerHandler.isCreativeMode((Player) event.getPlayer(), true) && PlayerHandler.isCreativeItem(event.getItem(), "pickItem")) {
			Block block = null;
			try {
				block = event.getPlayer().getTargetBlock((Set < Material > ) null, 6);
			} catch (final Throwable t) {
				try {
					block = (Block) event.getPlayer().getClass().getMethod("getTargetBlock", HashSet.class, int.class).invoke(event.getPlayer(), (HashSet < Byte > ) null, 6);
				} catch (Exception e) {
					ServerUtils.sendSevereTrace(e);
				}
			}
			if (block != null && block.getType() != Material.AIR) {
				ItemStack item = null;
				if (ServerUtils.hasSpecificUpdate("1_13")) { 
					item = new ItemStack(block.getType());
				} else {
					item = LegacyAPI.newItemStack(block.getType(), 1, LegacyAPI.getBlockData(block));
				}
				item.setData(block.getState().getData());
				event.getPlayer().getInventory().addItem(item);
				PlayerHandler.updateInventory(event.getPlayer(), 0);
				event.setCancelled(true);
			}
		}
	}
}