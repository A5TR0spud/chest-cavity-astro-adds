package net.astrospud.ccastroadds.registration;

import net.astrospud.ccastroadds.recipes.EditTumor;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.tigereye.chestcavity.ChestCavity;
import net.tigereye.chestcavity.recipes.InfuseVenomGland;
import net.tigereye.chestcavity.recipes.SalvageRecipe;
import net.tigereye.chestcavity.recipes.json.SalvageRecipeSerializer;

public class CCAARecipes {
    public static SpecialRecipeSerializer<EditTumor> EDIT_TUMOR;

    public static void register() {
        EDIT_TUMOR = (SpecialRecipeSerializer<EditTumor>) Registry.register(Registry.RECIPE_SERIALIZER, "crafting_special_edit_tumor", new SpecialRecipeSerializer<EditTumor>(EditTumor::new));
    }

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(Identifier id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, id, serializer);
    }
}