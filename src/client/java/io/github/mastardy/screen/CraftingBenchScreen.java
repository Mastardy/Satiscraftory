package io.github.mastardy.screen;

import io.github.mastardy.Satiscraftory;
import io.github.mastardy.recipe.custom.CraftingBenchRecipe;
import io.github.mastardy.screen.custom.CraftingBenchScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class CraftingBenchScreen extends HandledScreen<CraftingBenchScreenHandler> {
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/villager/scroller_disabled");
    private static final Identifier RECIPE_SELECTED_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe_selected");
    private static final Identifier RECIPE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe_highlighted");
    private static final Identifier RECIPE_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe");
    private static final Identifier TEXTURE = Identifier.of(Satiscraftory.MOD_ID, "textures/gui/container/crafting_bench.png");

    /*private static final int SCROLLBAR_HEIGHT = 27;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_OFFSET_Y = 18;
    private static final int SCROLLBAR_OFFSET_X = 94;*/

    private float scrollAmount;
    private int scrollOffset;
    private boolean scrolling;

    public CraftingBenchScreen(CraftingBenchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    protected void init() {
        super.init();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
        int k = (int) (39.0F * this.scrollAmount);
        Identifier identifier = this.shouldScroll() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i + 80, j + 15 + k, 6, 15);

        int l = this.x + 7;
        int m = this.y + 14;
        int n = this.scrollOffset + 12;

        this.renderRecipeBackground(context, mouseX, mouseY, l, m, n);
        this.renderRecipeIcons(context, l, m, n);
        this.renderRecipe(context, this.x, this.y);
    }

    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        super.drawMouseoverTooltip(context, x, y);
    }

    private void renderRecipeBackground(DrawContext context, int mouseX, int mouseY, int x, int y, int scrollOffset) {
        for (int i = this.scrollOffset; i < scrollOffset && i < getRecipeCount(); i++) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 18;
            int l = j / 4;
            int m = y + l * 18 + 2;
            Identifier identifier;

            if (i == getSelectedRecipe()) {
                identifier = RECIPE_SELECTED_TEXTURE;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + 16 && mouseY < m + 18) {
                identifier = RECIPE_HIGHLIGHTED_TEXTURE;
            } else {
                identifier = RECIPE_TEXTURE;
            }

            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, k, m - 1, 18, 18);
        }
    }

    private void renderRecipeIcons(DrawContext context, int x, int y, int scrollOffset) {
        var recipes = getAvailableRecipes();

        if (this.client != null && this.client.world != null) {
            ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(this.client.world);

            for (int i = this.scrollOffset; i < scrollOffset && i < recipes.size(); i++) {
                int j = i - this.scrollOffset;
                int k = x + j % 4 * 18 + 1;
                int l = j / 4;
                int m = y + l * 18 + 2;

                SlotDisplay slotDisplay = new SlotDisplay.StackSlotDisplay(recipes.get(i).output());
                context.drawItem(slotDisplay.getFirst(contextParameterMap), k, m);
            }
        }
    }

    private void renderRecipe(DrawContext context, int x, int y) {
        int i = x + 95;
        int j = y + 29;

        int l = x + 144;
        int m = y + 29;

        var recipe = getAvailableRecipes().get(getSelectedRecipe());
        if (this.client != null && this.client.world != null) {
            var contextParameterMap = SlotDisplayContexts.createParameters(this.client.world);

            var slotDisplay = new SlotDisplay.StackSlotDisplay(getItemStack(recipe.inputItem().toDisplay()));
            var slotStack = slotDisplay.getFirst(contextParameterMap);
            context.drawItem(slotStack, i, j);
            context.drawStackOverlay(this.textRenderer, slotStack, i, j, String.valueOf(recipe.inputCount()));

            slotDisplay = new SlotDisplay.StackSlotDisplay(recipe.output());
            slotStack = slotDisplay.getFirst(contextParameterMap);

//            var matrices = context.getMatrices();
//            matrices.pushMatrix();
//            matrices.translate(l - 8, m - 8);
//            matrices.scale(2f, 2f);
            context.drawItem(slotStack, l, m);
//            matrices.popMatrix();

            context.drawStackOverlay(this.textRenderer, slotStack, l, m, String.valueOf(recipe.output().getCount()));
        }
    }

    private ItemStack getItemStack(SlotDisplay display) {
        if (display instanceof SlotDisplay.ItemSlotDisplay(net.minecraft.registry.entry.RegistryEntry<net.minecraft.item.Item> item)) {
            return new ItemStack(item.value());
        }

        if (display instanceof SlotDisplay.CompositeSlotDisplay(java.util.List<SlotDisplay> contents)) {
            for (SlotDisplay subDisplay : contents) {
                if (subDisplay instanceof SlotDisplay.ItemSlotDisplay(net.minecraft.registry.entry.RegistryEntry<net.minecraft.item.Item> item)) {
                    return new ItemStack(item);
                }
            }
        }

        return null;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;

        int i = this.x + 7;
        int j = this.y + 14;
        int k = this.scrollOffset + 12;

        for (int l = this.scrollOffset; l < k; l++) {
            int m = l - this.scrollOffset;
            double d = mouseX - (double) (i + m % 4 * 16);
            double e = mouseY - (double) (j + m / 4 * 18);
            if (d >= 0.0 && e >= 0.0 && d < 16.0 && e < 18.0 && this.client != null && this.handler.onButtonClick(this.client.player, l)) {
                assert this.client.interactionManager != null;
                this.client.interactionManager.clickButton(this.handler.syncId, l);
                return true;
            }
        }

        i = this.x + 80;
        j = this.y + 15;
        if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 54)) {
            this.scrolling = true;
        }

        i = this.x + 115;
        j = this.y + 53;

        if (mouseX >= (double) i && mouseX < (double) (i + 20) && mouseY >= (double) j && mouseY < (double) (j + 20) && this.client != null) {
            assert this.client.interactionManager != null;
            this.client.interactionManager.clickButton(this.handler.syncId, -1);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling && this.shouldScroll()) {
            int i = this.y + 15;
            int j = i + 54;

            this.scrollAmount = ((float) mouseY - (float) i - 7.5F / ((float) (j - i) - 15.0F));
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int) ((double) (this.scrollAmount * (float) this.getMaxScroll()) + (double) 0.5F) * 4;

            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount) && this.shouldScroll()) {
            int i = this.getMaxScroll();
            float f = (float) verticalAmount / (float) i;
            this.scrollAmount = MathHelper.clamp(this.scrollAmount - f, 0.0F, 1.0F);
            this.scrollOffset = (int) ((double) (this.scrollAmount * (float) i) + (double) 0.5F) * 4;
        }

        return true;
    }

    private ArrayList<CraftingBenchRecipe> getAvailableRecipes() {
        return this.handler.getAvailableRecipes();
    }

    private int getSelectedRecipe() {
        return this.handler.getSelectedRecipe();
    }

    private int getRecipeCount() {
        return this.handler.getAvailableRecipeCount();
    }

    private boolean shouldScroll() {
        return getRecipeCount() > 12;
    }

    protected int getMaxScroll() {
        return (getRecipeCount() + 3) / 4 - 3;
    }
}
