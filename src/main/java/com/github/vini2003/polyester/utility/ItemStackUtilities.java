package com.github.vini2003.polyester.utility;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;

import java.util.*;

/**
 * Utilities for dealing
 * with ItemStacks.
 */
public class ItemStackUtilities {
	/**
	 * Sorts an inventory based on stack names and count.
	 *
	 * @param inventory the specified inventory.
	 */
	public static void sort(Inventory inventory) {
		TreeMap<String, ArrayList<ItemStack>> byType = new TreeMap<>();

		for (int i = 0; i < inventory.getInvSize(); ++i) {
			ItemStack stack = inventory.getInvStack(i);

			if (!byType.containsKey(stack.getName().asString()) && !stack.isEmpty()) {
				byType.put(stack.getName().asString(), new ArrayList<>());
			}

			if (!stack.isEmpty()) {
				byType.get(stack.getName().asString()).add(stack.copy());
			}

			inventory.setInvStack(i, ItemStack.EMPTY);
		}

		int i = 0;
		for (Map.Entry<String, ArrayList<ItemStack>> type : byType.entrySet()) {
			ArrayList<ItemStack> stacks = type.getValue();
			boolean finished = false;
			for (int k = 0; k < 128 && !finished; ++k) {
				for (int l = 0; l < stacks.size(); ++l) {
					boolean moved = false;
					if (l < stacks.size() - 1) {
						ItemStack current = stacks.get(l);
						ItemStack next = stacks.get(l + 1);

						if (current.getCount() < next.getCount()) {
							stacks.set(l + 1, current);
							stacks.set(l, next);
							moved = true;
						}
					}
					if (l == stacks.size() - 1 && !moved) {
						finished = true;
					}
				}
			}

			for (ItemStack stack : stacks) {
				inventory.setInvStack(i, stack);
				++i;
			}
		}
	}

	public static ItemStack withLore(ItemStack stack, Collection<Text> texts) {
		List<Text> entries = (List<Text>) texts;

		ListTag loreListTag = new ListTag();

		entries.forEach(text -> loreListTag.add(StringTag.of(Text.Serializer.toJson(text))));

		CompoundTag displayTag = stack.getOrCreateTag().getCompound("display");

		displayTag.put("Lore", loreListTag);

		CompoundTag stackTag = stack.getOrCreateTag();

		stackTag.put("display", displayTag);

		stack.setTag(stackTag);

		return stack;
	}
}
