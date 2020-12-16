/*
 * Copyright (c) 2019-2020 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.connector.network.translators.inventory;

import com.nukkitx.protocol.bedrock.data.inventory.ContainerSlotType;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerType;
import com.nukkitx.protocol.bedrock.data.inventory.StackRequestSlotInfoData;
import org.geysermc.connector.inventory.Inventory;
import org.geysermc.connector.network.session.GeyserSession;
import org.geysermc.connector.network.translators.inventory.holder.BlockInventoryHolder;
import org.geysermc.connector.network.translators.inventory.holder.InventoryHolder;
import org.geysermc.connector.network.translators.inventory.updater.CursorInventoryUpdater;
import org.geysermc.connector.network.translators.inventory.updater.InventoryUpdater;
import org.geysermc.connector.network.translators.world.block.BlockTranslator;

public class CraftingInventoryTranslator extends BaseInventoryTranslator {
    private final InventoryHolder holder;
    private final InventoryUpdater updater;

    CraftingInventoryTranslator() {
        super(10);
        int javaBlockState = BlockTranslator.getJavaBlockState("minecraft:crafting_table");
        this.holder = new BlockInventoryHolder(BlockTranslator.getBedrockBlockId(javaBlockState), ContainerType.WORKBENCH);
        this.updater = new CursorInventoryUpdater();
    }

    @Override
    public void prepareInventory(GeyserSession session, Inventory inventory) {
        holder.prepareInventory(this, session, inventory);
    }

    @Override
    public void openInventory(GeyserSession session, Inventory inventory) {
        holder.openInventory(this, session, inventory);
    }

    @Override
    public void closeInventory(GeyserSession session, Inventory inventory) {
        holder.closeInventory(this, session, inventory);
    }

    @Override
    public void updateInventory(GeyserSession session, Inventory inventory) {
        updater.updateInventory(this, session, inventory);
    }

    @Override
    public void updateSlot(GeyserSession session, Inventory inventory, int slot) {
        updater.updateSlot(this, session, inventory, slot);
    }

    @Override
    public SlotType getSlotType(int javaSlot) {
        if (javaSlot == 0)
            return SlotType.OUTPUT;
        return SlotType.NORMAL;
    }


    @Override
    public int javaSlotToBedrock(int slot) {
        if (slot < size) {
            return slot == 0 ? 50 : slot + 31;
        }
        return super.javaSlotToBedrock(slot);
    }

    @Override
    public BedrockContainerSlot javaSlotToBedrockContainer(int slot) {
        if (slot >= 1 && slot <= 9) {
            return new BedrockContainerSlot(ContainerSlotType.CRAFTING_INPUT, slot + 31);
        }
        if (slot == 0) {
            return new BedrockContainerSlot(ContainerSlotType.CRAFTING_OUTPUT, 0);
        }
        return super.javaSlotToBedrockContainer(slot);
    }

    @Override
    public int bedrockSlotToJava(StackRequestSlotInfoData slotInfoData) {
        if (slotInfoData.getContainer() == ContainerSlotType.CRAFTING_INPUT) {
            // Java goes from 1 - 9, left to right then up to down
            // Bedrock is the same, but it starts from 32.
            return slotInfoData.getSlot() - 31;
        }
        if (slotInfoData.getContainer() == ContainerSlotType.CRAFTING_OUTPUT || slotInfoData.getContainer() == ContainerSlotType.CREATIVE_OUTPUT) {
            return 0;
        }
        return super.bedrockSlotToJava(slotInfoData);
    }
}
