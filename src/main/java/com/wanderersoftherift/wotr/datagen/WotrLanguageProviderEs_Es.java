package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrEntities;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftLayoutLayers;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import com.wanderersoftherift.wotr.util.listedit.Append;
import com.wanderersoftherift.wotr.util.listedit.Clear;
import com.wanderersoftherift.wotr.util.listedit.Drop;
import com.wanderersoftherift.wotr.util.listedit.DropLast;
import com.wanderersoftherift.wotr.util.listedit.Prepend;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/* Handles Data Generation for I18n of the locale 'es_es' of the Wotr mod */
public class WotrLanguageProviderEs_Es extends LanguageProvider {

    public WotrLanguageProviderEs_Es(PackOutput output) {
        super(output, WanderersOfTheRift.MODID, "es_es");
    }

    @Override
    protected void addTranslations() {
        // Helpers are available for various common object types. Every helper has two variants: an add() variant
        // for the object itself, and an addTypeHere() variant that accepts a supplier for the object.
        // The different names for the supplier variants are required due to generic type erasure.
        // All following examples assume the existence of the values as suppliers of the needed type.
        // See https://docs.neoforged.net/docs/1.21.1/resources/client/i18n/ for translation of other types.

        // Adds a block translation.
        addBlock(WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK, "Yunque Rúnico");
        addBlock(WotrBlocks.RIFT_CHEST, "Cofre de la Grieta");
        addBlock(WotrBlocks.RIFT_SPAWNER, "Generador de Grietas");
        addBlock(WotrBlocks.NPC, "Bloque de PNG");
        addBlock(WotrBlocks.KEY_FORGE, "Forja de Llaves");
        addBlock(WotrBlocks.DITTO_BLOCK, "Bloque Ditto");
        addBlock(WotrBlocks.SPRING_BLOCK, "Bloque Muelle");
        addBlock(WotrBlocks.TRAP_BLOCK, "Bloque Trampa");
        addBlock(WotrBlocks.PLAYER_TRAP_BLOCK, "Bloque Trampa de Jugadore");
        addBlock(WotrBlocks.MOB_TRAP_BLOCK, "Bloque Trampa de Monstruo");
        addBlock(WotrBlocks.ABILITY_BENCH, "Banco de Habilidades");
        addBlock(WotrBlocks.RIFT_MOB_SPAWNER, "Generador de Monstruos de Grieta");
        addBlock(WotrBlocks.ANOMALY, "Anomalía");

        // Adds an item translation.
        addItem(WotrItems.BUILDER_GLASSES, "Gafas de Constructore");
        addItem(WotrItems.RUNEGEM, "Gema Rúnica");
        addItem(WotrItems.RIFT_KEY, "Llave de Grieta");
        addItem(WotrItems.RAW_RUNEGEM_GEODE, "Geoda de Gema Rúnica (En Bruto)");
        addItem(WotrItems.SHAPED_RUNEGEM_GEODE, "Geoda de Gema Rúnica (Formada)");
        addItem(WotrItems.CUT_RUNEGEM_GEODE, "Geoda de Gema Rúnica (Cortada)");
        addItem(WotrItems.POLISHED_RUNEGEM_GEODE, "Geoda de Gema Rúnica (Pulida)");
        addItem(WotrItems.FRAMED_RUNEGEM_GEODE, "Geoda de Gema Rúnica (Enmarcada)");
        addItem(WotrItems.RAW_RUNEGEM_MONSTER, "Geoda de Monstruo (En Bruto)");
        addItem(WotrItems.SHAPED_RUNEGEM_MONSTER, "Geoda de Monstruo (Formada)");
        addItem(WotrItems.CUT_RUNEGEM_MONSTER, "Geoda de Monstruo (Cortada)");
        addItem(WotrItems.POLISHED_RUNEGEM_MONSTER, "Geoda de Monstruo (Pulida)");
        addItem(WotrItems.FRAMED_RUNEGEM_MONSTER, "Geoda de Monstruo (Enmarcada)");
        addItem(WotrItems.ABILITY_HOLDER, "Habilidad Vacía");
        addItem(WotrItems.SKILL_THREAD, "Hilo de Habilidad");
        addItem(WotrItems.CURRENCY_BAG, "Bolsa de Monedas");

        addItem(WotrItems.NOIR_HELMET, "Fedora");
        addItem(WotrItems.COLOR_HELMET, "Payase");

        addEntityType(WotrEntities.RIFT_ENTRANCE, "Entrada a la Grieta");
        addEntityType(WotrEntities.RIFT_EXIT, "Salida de la Grieta");
        addEntityType(WotrEntities.SIMPLE_EFFECT_PROJECTILE, "Proyectil");
        addEntityType(WotrEntities.RIFT_ZOMBIE, "Zombie de la Grieta");
        addEntityType(WotrEntities.RIFT_SKELETON, "Esqueleto de la Grieta");

        addEssenceType("void", "Vacío");
        addEssenceType("flow", "Flujo");
        addEssenceType("form", "Forma");
        addEssenceType("order", "Orden");
        addEssenceType("chaos", "Kaos");

        addEssenceType("earth", "Tierra");
        addEssenceType("fire", "Fuego");
        addEssenceType("water", "Agua");
        addEssenceType("air", "Aire");
        addEssenceType("life", "Vida");
        addEssenceType("death", "Muerte");
        addEssenceType("light", "Luz");
        addEssenceType("dark", "Oscuridad");

        addEssenceType("animal", "Animal");
        addEssenceType("plant", "Planta");
        addEssenceType("mushroom", "Seta");
        addEssenceType("honey", "Miel");
        addEssenceType("food", "Comida");
        addEssenceType("slime", "Limo");
        addEssenceType("mechanical", "Mecánica");
        addEssenceType("metal", "Metal");
        addEssenceType("fabric", "Tejido");
        addEssenceType("crystal", "Cristal");
        addEssenceType("energy", "Energía");
        addEssenceType("mind", "Mente");
        addEssenceType("nether", "Nether");
        addEssenceType("end", "End");
        addEssenceType("processor", "Procesador");

        addTheme("buzzy_bees", "Abejas Zumbadoras");
        addTheme("cave", "Cueva");
        addTheme("color", "Color");
        addTheme("deepfrost", "Escarcha Profunda");
        addTheme("desert", "Desierto");
        addTheme("forest", "Bosque");
        addTheme("jungle", "Jungla");
        addTheme("meadow", "Prado");
        addTheme("mesa", "Mesa");
        addTheme("mushroom", "Seta");
        addTheme("nether", "Nether");
        addTheme("noir", "Noir");
        addTheme("processor", "Procesador");
        addTheme("swamp", "Pantano");

        WotrBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> {
            // addBlock(helper.getBlock(), getTranslationString(helper.getBlock().get()));
            helper.getVariants().forEach((variant, block) -> addBlock(block, getTranslationString(block.get())));
            helper.getModVariants().forEach((variant, block) -> addBlock(block, getTranslationString(block.get())));
        });

        add("block." + WanderersOfTheRift.MODID + ".processor_block_1", "Bloque Procesador 1 [Muro]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_2", "Bloque Procesador 2 [Camino]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_3", "Bloque Procesador 3 [Suelo]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_4", "Bloque Procesador 4 [Muro Alt]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_5", "Bloque Procesador 5 [Suelo Alt]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_6", "Bloque Procesador 6 [Tablones]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_7", "Bloque Procesador 7 [Ladrillos]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_8", "Bloque Procesador 8 [Camino Alt]");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_9", "Bloque Procesador 9");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_10", "Bloque Procesador 10");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_11", "Bloque Procesador 11");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_12", "Bloque Procesador 12");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_13", "Bloque Procesador 13");
        add("block." + WanderersOfTheRift.MODID + ".processor_block_14", "Bloque Procesador 14");

        // Adds a generic translation
        add("itemGroup." + WanderersOfTheRift.MODID, "Wanderers of the Rift");
        add(WanderersOfTheRift.translationId("itemGroup", "npc"), "PNJs");
        add(WanderersOfTheRift.translationId("itemGroup", "ability"), "Habilidades");
        add(WanderersOfTheRift.translationId("itemGroup", "runegem"), "Gemas Rúnicas");
        add(WanderersOfTheRift.translationId("itemGroup", "dev"), "Builders of the Rift");

        add("item." + WanderersOfTheRift.MODID + ".rift_key.themed", "Llave de Grieta (%s)");

        add("container." + WanderersOfTheRift.MODID + ".rune_anvil", "Yunque Rúnico");
        add("container." + WanderersOfTheRift.MODID + ".rune_anvil.apply", "Aplicar");
        add("container." + WanderersOfTheRift.MODID + ".rift_chest", "Cofre de Grieta");
        add("container." + WanderersOfTheRift.MODID + ".key_forge", "Forja de Llaves");
        add("container." + WanderersOfTheRift.MODID + ".ability_bench", "Banco de Habilidades");
        add(WanderersOfTheRift.translationId("container", "trading"), "Comerciante de %s");
        add(WanderersOfTheRift.translationId("container", "quest.selection"), "Seleccionar Misión");
        add(WanderersOfTheRift.translationId("container", "quest.complete"), "Completar");
        add(WanderersOfTheRift.translationId("container", "quest.handin"), "Entregar");
        add(WanderersOfTheRift.translationId("container", "quest.goals"), "Objetivo");
        add(WanderersOfTheRift.translationId("container", "quest.rewards"), "Recompensa");
        add(WanderersOfTheRift.translationId("container", "quest.abandon"), "Abandonar");
        add(WanderersOfTheRift.translationId("container", "quest.are_you_sure"), "¿Segure que quieres abandonar?");
        add(WanderersOfTheRift.translationId("container", "quest.hand_in_to"), "Entregar a: %s");
        add(WanderersOfTheRift.translationId("container", "quest.goal.give"), "Llevar a %s/%s ");
        add(WanderersOfTheRift.translationId("container", "quest.goal.kill"), "Derrotar %s (%s/%s)");
        add(WanderersOfTheRift.translationId("container", "quest.goal.complete_rifts"), "%s %sgrietas (%s/%s)");
        add(WanderersOfTheRift.translationId("container", "quest.accept"), "Aceptar");
        add(WanderersOfTheRift.translationId("container", "quests"), "Misiones");
        add(WanderersOfTheRift.translationId("container", "quest_complete"), "¡Misión Completa!");
        add(WanderersOfTheRift.translationId("container", "guild_rank_up"), "¡El Gremio ha subido de Rango!");
        add(WanderersOfTheRift.translationId("container", "guilds.claim_reward"), "¡Reclama la recompensa!");

        add("container." + WanderersOfTheRift.MODID + ".ability_bench.upgrade", "Mejoras");
        add("container." + WanderersOfTheRift.MODID + ".ability_bench.unlock", "Desbloque la siguiente selección");
        add(WanderersOfTheRift.translationId("container", "rift_complete"), "Descripción de la Grieta");
        add(WanderersOfTheRift.translationId("container", "rift_complete.reward"), "Recompensas");

        add(WanderersOfTheRift.translationId("container", "guilds"), "Gremios");
        add(WanderersOfTheRift.translationId("container", "guild.rank"), "Rango: %s");
        add(WanderersOfTheRift.translationId("container", "guild.reputation"), "Reputación: %s/%s");
        add(WanderersOfTheRift.translationId("container", "guild.reputation.max"), "Reputación: MAX");

        add(WanderersOfTheRift.translationId("container", "wallet"), "Cartera");

        add(WanderersOfTheRift.translationId("stat", "result"), "Resultado: ");
        add(WanderersOfTheRift.translationId("stat", "result.success"), "Éxitoso");
        add(WanderersOfTheRift.translationId("stat", "result.survived"), "Huído");
        add(WanderersOfTheRift.translationId("stat", "result.failed"), "Fallido");
        add(WanderersOfTheRift.translationId("stat", "time"), "Tiempo en la Grieta: ");
        add(WanderersOfTheRift.translationId("stat", "mobs_killed"), "Monstruos matados: ");
        add(WanderersOfTheRift.translationId("stat", "chests_opened"), "Cofres abiertos: ");

        add(WanderersOfTheRift.translationId("screen", "configure_hud"), "Configurar HUD");

        add(WanderersOfTheRift.translationId("command", "total"), "Total: %s");
        add("command." + WanderersOfTheRift.MODID + ".dev_world_set",
                "Se han aplicado las siguientes configuraciones:\n - %1$s: Desactivado\n - %2$s: Desactivado\n - %3$s: Desactivado\n - %4$s: Desactivado\n - %5$s: Desactivado\n - %6$s: Desactivado");
        add("command." + WanderersOfTheRift.MODID + ".invalid_item", "¡El objeto está vacío!");
        add(WanderersOfTheRift.translationId("commands", "ability.invalid"), "No hay una habilidad '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_player", "¡Le Jugadore no existe!");
        add("command." + WanderersOfTheRift.MODID + ".get_item_stack_components.success",
                "Componentes disponibles para '%1$s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_tier", "El Nivel de la Llave es ahora %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_preset", "La Configuración de la Llave es ahora %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_theme", "El Tema de la Llave es ahora %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_objective", "El Objetivo de la Llave es ahora %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.set_seed", "La Semilla de la Llave es ahora %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.layout_layers.add",
                "Modificación de diseño añadida: %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.layout_layers.undo",
                "Modificación de diseño eliminada: %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.layout_layers.clear",
                "Eliminadas todas las modificaciones");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.bake", "Configuración propia creada");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.export",
                "Configuración de la Generación de la Grieta guardada como %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.export.output_contains_dot",
                "el nombre no puede contener puntos");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.export.not_custom",
                "La Configuración tiene que ser personalizada para poder exportarla, ¿te has olvidado de usar `/wotr riftKey generator bake`?`");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.generator.export.encode_failed",
                "No se ha podido codificar la Configuración");
        add("command." + WanderersOfTheRift.MODID + ".invalid_theme", "Tema no válido '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_objective", "Objetivo no válido '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_generator_preset", "Configuración no válida '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_template_pool", "Grupo de Plantillas no válido '%s'");
        add("command." + WanderersOfTheRift.MODID + ".invalid_rift_parameter", "Parámetros de Grieta no válidos %s");
        add("command." + WanderersOfTheRift.MODID + ".invalid_ability_resource", "Recurso de Habilidad no válido '%s'");
        add("command." + WanderersOfTheRift.MODID + ".rift_key.invalid_item",
                "'¡Tienes que tener una Llave de Grieta en la mano!");
        add("command." + WanderersOfTheRift.MODID + ".spawn_piece.generating", "Generando %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_parameter.get", "Parámetro actual: %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_parameter.set", "Parámetro actualizado: %s -> %s");
        add("command." + WanderersOfTheRift.MODID + ".rift_parameter.missing",
                "Este Parámetro no existe en este mundo");
        add(WanderersOfTheRift.translationId("command", "make_ability_item.success"),
                "Se han aplicado los Componentes a la Habilidad");
        add(WanderersOfTheRift.translationId("command", "stats.invalid"), "Estadística Primaria no válida");
        add(WanderersOfTheRift.translationId("command", "show_attribute"), "%s: %s");
        add(WanderersOfTheRift.translationId("command", "set_attribute"), "%s cambiado a %s");
        add(WanderersOfTheRift.translationId("command", "place.processor.invalid"), "Procesador no válido %s");

        add("ability." + WanderersOfTheRift.MODID + ".cannot_unlock",
                "Para tener acceso a esta mejora tienes que desbloquear: ");
        add("ability." + WanderersOfTheRift.MODID + ".fireball", "Bola de Fuego");
        add("ability." + WanderersOfTheRift.MODID + ".firebolts", "Rayos de Fuego");
        add("ability." + WanderersOfTheRift.MODID + ".strength", "Fuerza");
        add("ability." + WanderersOfTheRift.MODID + ".weak_strength", "Fuerza (Exhauste)");
        add("ability." + WanderersOfTheRift.MODID + ".test_chain_ability", "Cadena de Fuerza");
        add("ability." + WanderersOfTheRift.MODID + ".icicles", "Carámbanos");
        add("ability." + WanderersOfTheRift.MODID + ".mega_boost", "Mega Impulso");
        add("ability." + WanderersOfTheRift.MODID + ".dash", "Impulso");
        add("ability." + WanderersOfTheRift.MODID + ".summon_skeletons", "Invocar Esqueletos");
        add("ability." + WanderersOfTheRift.MODID + ".test_ability", "Habilidad de Prueba");
        add("ability." + WanderersOfTheRift.MODID + ".knockback", "Empuje");
        add("ability." + WanderersOfTheRift.MODID + ".pull", "Tirón");
        add("ability." + WanderersOfTheRift.MODID + ".heal", "Curación");
        add("ability." + WanderersOfTheRift.MODID + ".firetouch", "Habilidad experimental sin sentido");
        add("ability." + WanderersOfTheRift.MODID + ".veinminer", "Minado de Vetas");
        add("trigger." + WanderersOfTheRift.MODID + ".tick", "Tick");
        add("trigger." + WanderersOfTheRift.MODID + ".take_damage", "Recibir Daño");
        add("trigger." + WanderersOfTheRift.MODID + ".deal_damage", "Hacer Daño");
        add("trigger." + WanderersOfTheRift.MODID + ".break_block", "Romper Bloque");
        add("ability_conditions." + WanderersOfTheRift.MODID + ".fast_dash", "Impulso Veloz");

        add(WanderersOfTheRift.translationId("effect_marker", "fireshield"), "Escudo de Fuego");

        add("accessibility." + WanderersOfTheRift.MODID + ".screen.title",
                "Wanderers of the Rifts: Configuración de Accesibilidad");
        add("accessibility." + WanderersOfTheRift.MODID + ".menubutton", "Accesibilidad de WotR (tmp)");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.trypophobia", "Tripofobia Desactivada");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.arachnophobia", "Aracnofobia Desactivada");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.flashing_lights", "Estímulos Luminosos");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.misophonia", "Misofonia Desactivada");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.high_contrast", "Contraste Alto");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.hard_of_hearing", "Pérdida de Audición");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.reduced_motion", "Movimiento Reducido");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.trypophobia",
                "Elimina los aspectos que pueden causar Tripofobia");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.arachnophobia",
                "¡Cambia las arañas por tortugas!");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.flashing_lights",
                "Reduce los estímulos luminosos");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.misophonia",
                "Reemplaza algunos sonidos que pueden ser desencadenantes de Misofonía");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.high_contrast",
                "Mejora la Interfaz y los Elementos del HUD con mayor contraste para una mejor visibilidad");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.hard_of_hearing",
                "Mejora las señales auditivas para una mejor Accesibilidad");
        add("accessibility." + WanderersOfTheRift.MODID + ".screen.tooltip.reduced_motion",
                "Desactiva o reduce la velocidad de las animaciones, temblor de cámara o efectos de pantalla");

        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_generator_preset", "Configuración de Generación: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_tier", "Nivel: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_theme", "Tema: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_objective", "Objetivo: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".rift_key_parameter_entry", "%s: %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_value", "Esencia: %s %s");
        add("tooltip." + WanderersOfTheRift.MODID + ".essence_header", "Esencia:");
        add("tooltip." + WanderersOfTheRift.MODID + ".socket", "Hueco: ");
        add("tooltip." + WanderersOfTheRift.MODID + ".implicit", "Implícito: ");
        add("tooltip." + WanderersOfTheRift.MODID + ".empty_socket", "(Ranura Vacía)");
        add("tooltip." + WanderersOfTheRift.MODID + ".show_extra_info", "Mantén presionado [%s] para más info");
        add(WanderersOfTheRift.translationId("tooltip", "mana_bar"), "Maná: %s/%s");
        add(WanderersOfTheRift.translationId("tooltip", "rift_key_seed"), "Semilla: %s");
        add(WanderersOfTheRift.translationId("tooltip", "runegem.shape"), "Forma: %s");
        add(WanderersOfTheRift.translationId("tooltip", "runegem.modifiers"), "Modificadores:");
        add(WanderersOfTheRift.translationId("tooltip", "tier"), "N%s");
        add(WanderersOfTheRift.translationId("tooltip", "currency_bag"), "Ganas %s cuando lo consumes");
        add(WanderersOfTheRift.translationId("tooltip", "reward.reputation"), "%s Reputación");

        add(WanderersOfTheRift.translationId("itemname", "consolation1"), "*Sollozos*");
        add(WanderersOfTheRift.translationId("itemname", "consolation2"), "Pañuelo");
        add(WanderersOfTheRift.translationId("itemname", "consolation3"),
                "Los éxitos se construyen sobre una montaña de fallos");
        add(WanderersOfTheRift.translationId("itemname", "consolation4"),
                "Piensa en ello como una oportunidad para aprender");

        add("subtitles." + WanderersOfTheRift.MODID + ".rift_open", "La Grieta se abre");

        add("modifier." + WanderersOfTheRift.MODID + ".attribute.add.positive", "+%s %s");
        add("modifier." + WanderersOfTheRift.MODID + ".attribute.add.negative", "%s %s");
        add("modifier." + WanderersOfTheRift.MODID + ".attribute.multiply.positive", "+%s%% %s");
        add("modifier." + WanderersOfTheRift.MODID + ".attribute.multiply.negative", "%s%% %s");

        add(WanderersOfTheRift.MODID + ".rift.create.failed", "No se ha podido formar una Grieta");

        add(WanderersOfTheRift.translationId("ability_upgrade", "aoe.name"), "Área de Efecto");
        add(WanderersOfTheRift.translationId("ability_upgrade", "aoe.description"),
                "Aumenta el Área de Efecto en 1 bloque");
        add(WanderersOfTheRift.translationId("ability_upgrade", "cooldown.name"),
                "Disminuye el Tiempo de Enfriamiento");
        add(WanderersOfTheRift.translationId("ability_upgrade", "cooldown.description"),
                "Disminuye el Tiempo de Enfriamiento en un 3%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "damage.name"), "Aumena el Daño");
        add(WanderersOfTheRift.translationId("ability_upgrade", "damage.description"), "Aumenta el Daño en un 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "drain_life.name"), "Absorbe Vida");
        add(WanderersOfTheRift.translationId("ability_upgrade", "drain_life.description"),
                "Absorbe 1 punto de vida por golpe");
        add(WanderersOfTheRift.translationId("ability_upgrade", "mana_cost.name"), "Reduce el Coste de Maná");
        add(WanderersOfTheRift.translationId("ability_upgrade", "mana_cost.description"),
                "Reduce el Coste de Maná en un 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_count.name"), "Más Proyectiles");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_count.description"),
                "Añade un Proyectil adicional");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_speed.name"), "Velocidad de Proyectil");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_speed.description"),
                "Incrementa la velocidad del Proyectil en un 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_spread.name"),
                "Reduce la Dispersión de los Proyectiles");
        add(WanderersOfTheRift.translationId("ability_upgrade", "projectile_spread.description"),
                "Reduce la Dispersión de los Proyectiles en un 10%");
        add(WanderersOfTheRift.translationId("ability_upgrade", "healing_power.name"), "Más Curación");
        add(WanderersOfTheRift.translationId("ability_upgrade", "healing_power.description"), "Curas un corazón extra");

        add(WotrKeyMappings.ABILITY_CATEGORY, "Wanderers of the Rift: Habilidades");
        add(WotrKeyMappings.MENU_CATEGORY, "Wanderers of the Rift: Menús");
        add(WotrKeyMappings.MISC_CATEGORY, "Wanderers of the Rift: Misc");
        add(WotrKeyMappings.ABILITY_1_KEY.getName(), "Usar Habilidad 1");
        add(WotrKeyMappings.ABILITY_2_KEY.getName(), "Usar Habilidad 2");
        add(WotrKeyMappings.ABILITY_3_KEY.getName(), "Usar Habilidad 3");
        add(WotrKeyMappings.ABILITY_4_KEY.getName(), "Usar Habilidad 4");
        add(WotrKeyMappings.ABILITY_5_KEY.getName(), "Usar Habilidad 5");
        add(WotrKeyMappings.ABILITY_6_KEY.getName(), "Usar Habilidad 6");
        add(WotrKeyMappings.ABILITY_7_KEY.getName(), "Usar Habilidad 7");
        add(WotrKeyMappings.ABILITY_8_KEY.getName(), "Usar Habilidad 8");
        add(WotrKeyMappings.ABILITY_9_KEY.getName(), "Usar Habilidad 9");
        add(WotrKeyMappings.PREV_ABILITY_KEY.getName(), "Seleccionar anterior Habilidad");
        add(WotrKeyMappings.NEXT_ABILITY_KEY.getName(), "Seleccionar siguiente Habilidad");
        add(WotrKeyMappings.USE_ABILITY_KEY.getName(), "Usar Habilidad seleccionada");
        add(WotrKeyMappings.ACTIVATE_ABILITY_SCROLL.getName(), "Activar desplazamiento de la barra de Habilidades");
        add(WotrKeyMappings.SHOW_TOOLTIP_INFO.getName(), "Mostrar más información");
        add(WotrKeyMappings.JIGSAW_NAME_TOGGLE_KEY.getName(), "Mostrar más información del bloque rompecabezas");
        add(WotrKeyMappings.GUILD_MENU_KEY.getName(), "Abrir menú de Gremio");
        add(WotrKeyMappings.QUEST_MENU_KEY.getName(), "Abrir menú de Misiones");
        add(WotrKeyMappings.WALLET_MENU_KEY.getName(), "Abrir menú de Cartera");

        add(WanderersOfTheRift.translationId("keybinds", "l_alt"), "LAlt");
        add(WanderersOfTheRift.translationId("keybinds", "r_alt"), "RAlt");
        add(WanderersOfTheRift.translationId("keybinds", "l_ctrl"), "LCtrl");
        add(WanderersOfTheRift.translationId("keybinds", "r_ctrl"), "RCtrl");
        add(WanderersOfTheRift.translationId("keybinds", "mod_alt"), "Alt+");
        add(WanderersOfTheRift.translationId("keybinds", "mod_ctrl"), "Ctrl+");
        add(WanderersOfTheRift.translationId("keybinds", "mod_shift"), "Shi+");

        add(WanderersOfTheRift.translationId("rei", "rolls_label"), "Tiradas:");
        add(WanderersOfTheRift.translationId("rei", "percent.min"), "%s: > %s%%");
        add(WanderersOfTheRift.translationId("rei", "percent.max"), "%s: < %s%%");
        add(WanderersOfTheRift.translationId("rei", "absolute.min"), "%s: < %s");
        add(WanderersOfTheRift.translationId("rei", "absolute.max"), "%s: > %s");

        add(WanderersOfTheRift.translationId("objective", "kill.name"), "Mata Monstruos");
        add(WanderersOfTheRift.translationId("objective", "stealth.name"), "Sigilo");
        add(WanderersOfTheRift.translationId("objective", "nothing.name"), "Nada");
        add(WanderersOfTheRift.translationId("objective", "kill.description"), "Derrota %s Monstruos");
        add(WanderersOfTheRift.translationId("objective", "stealth.description"), "Derrota Monstruos sigilosamente");
        add(WanderersOfTheRift.translationId("objective", "nothing.description"), "No hagas nada");
        add(WanderersOfTheRift.translationId("gui", "objective_status.complete"), "Objetivo Completo");

        add(WanderersOfTheRift.translationId("button", "reset"), "Reiniciar");
        add(WanderersOfTheRift.translationId("button", "close"), "Cerrar");
        add(WanderersOfTheRift.translationId("button", "rotate"), "Girar");
        add(WanderersOfTheRift.translationId("button", "show"), "Mostrar");
        add(WanderersOfTheRift.translationId("button", "hide"), "Ocultar");
        add(WanderersOfTheRift.translationId("button", "hud_presets"), "Configuración");
        add(WanderersOfTheRift.translationId("button", "customize"), "Personalizar");
        add("hud.minecraft.hotbar", "Barra de Acceso Rápido");
        add("hud.minecraft.experience_bar", "Barra de Experiencia");
        add("hud.minecraft.health_armor", "Vida y Armadura");
        add("hud.minecraft.food_level", "Nivel de Comida");
        add("hud.minecraft.experience_level", "Nivel de Experiencia");
        add("hud.minecraft.air_level", "Nivel de Aire");
        add("hud.minecraft.effects", "Efectos");
        add(WanderersOfTheRift.translationId("hud", "ability_bar"), "Barra de Habilidades");
        add(WanderersOfTheRift.translationId("hud", "mana_bar"), "Barra de Maná");
        add(WanderersOfTheRift.translationId("hud", "effect_bar"), "Barra de Efectos de Habilidades");
        add(WanderersOfTheRift.translationId("hud", "objective"), "Objetivo");

        add(WanderersOfTheRift.translationId("hud_preset", "default"), "Por Defecto");
        add(WanderersOfTheRift.translationId("hud_preset", "minimal"), "Mínima");
        add(WanderersOfTheRift.translationId("hud_preset", "custom"), "Personalizada");

        add(WanderersOfTheRift.translationId("attribute", "ability.aoe"), "Área de Efecto");
        add(WanderersOfTheRift.translationId("attribute", "ability.raw_damage"), "Daño");
        add(WanderersOfTheRift.translationId("attribute", "ability.cooldown"), "Tiempo de Enfriamiento");
        add(WanderersOfTheRift.translationId("attribute", "ability.heal_amount"), "Cantidad de Curación");
        add(WanderersOfTheRift.translationId("attribute", "critical_chance"), "Probabilidad de Crítico");
        add(WanderersOfTheRift.translationId("attribute", "critical_avoidance"), "Posibilidad de evitar Críticos");
        add(WanderersOfTheRift.translationId("attribute", "critical_bonus"), "Críticos Extra");
        add(WanderersOfTheRift.translationId("attribute", "ability.mana_cost"), "Coste de Maná");
        add(WanderersOfTheRift.translationId("attribute", "thorns_chance"), "Proabilidad de Espinas");
        add(WanderersOfTheRift.translationId("attribute", "thorns_damage"), "Daño de Espinas");
        add(WanderersOfTheRift.translationId("attribute", "life_leech"), "Robo de Vida");
        add(WanderersOfTheRift.translationId("attribute", "projectile_spread"), "Dispersión de Proyectiles");
        add(WanderersOfTheRift.translationId("attribute", "projectile_count"), "Cantidad de Proyectiles");
        add(WanderersOfTheRift.translationId("attribute", "projectile_speed"), "Velocidad de Proyectiles");
        add(WanderersOfTheRift.translationId("attribute", "projectile_pierce"), "Perforación de Proyectiles");
        add(WanderersOfTheRift.translationId("attribute", "max_mana"), "Maná Máximo");
        add(WanderersOfTheRift.translationId("attribute", "mana_regen_rate"), "Regeneración de Maná");
        add(WanderersOfTheRift.translationId("attribute", "mana_degen_rate"), "Degeneración de Maná");
        add(WanderersOfTheRift.translationId("attribute", "strength"), "Fuerza");
        add(WanderersOfTheRift.translationId("attribute", "dexterity"), "Destreza");
        add(WanderersOfTheRift.translationId("attribute", "constitution"), "Constitución");
        add(WanderersOfTheRift.translationId("attribute", "intelligence"), "Inteligencia");
        add(WanderersOfTheRift.translationId("attribute", "wisdom"), "Sabiduría");
        add(WanderersOfTheRift.translationId("attribute", "charisma"), "Carisma");

        addRunegems();
        addModifiers();
        add(WanderersOfTheRift.translationId("modifier", "silk_touch_enchant"), "Toque de Seda");
        add(WanderersOfTheRift.translationId("modifier", "fast_dash_condition"), "Impulso Veloz");

        add(WanderersOfTheRift.translationId("message", "disabled_in_rifts"), "Desactivado en Grietas");
        add(WanderersOfTheRift.translationId("message", "currency_obtained"), "Se ha añadido %s %s a tu Cartera");
        add(WanderersOfTheRift.translationId("message", "quest_already_active"),
                "Tienes que completar tu misión activa antes de aceptar otra");

        add(WanderersOfTheRift.translationId("currency", "coin"), "Moneda");

        add(WanderersOfTheRift.translationId("guild", "cats_cradle"), "Cats Cradle");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.0"), "Damp Kitten");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.1"), "Pawprentis");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.2"), "Purrfessional");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.3"), "Journeynyan");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.4"), "Furrmidable");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.5"), "Meowster");
        add(WanderersOfTheRift.translationId("guild", "cats_cradle.rank.6"), "Gwand Meowster");

        add(WanderersOfTheRift.translationId("npc", "default"), "Bailey");
        add(WanderersOfTheRift.translationId("npc", "cats_cradle_merchant"), "Comerciante Cats Cradle");
        add(WanderersOfTheRift.translationId("npc", "cats_cradle_quest_giver"), "Proveedor de Misiones Cats Cradle");

        add(WanderersOfTheRift.translationId("goal", "rift.attempt"), "Intenta");
        add(WanderersOfTheRift.translationId("goal", "rift.survive"), "Sobrevive");
        add(WanderersOfTheRift.translationId("goal", "rift.complete"), "Completa");
        add(WanderersOfTheRift.translationId("goal", "rift.tier"), "nivel %s");
        add(WanderersOfTheRift.translationId("goal", "any"), "cualquiera");

        add(WanderersOfTheRift.translationId("quest", "fetch_quest.title"), "Misión de Búsqueda");
        add(WanderersOfTheRift.translationId("quest", "fetch_quest.description"),
                "Tráeme lo que necesito y haré que tu espera sea recompensada.");
        add(WanderersOfTheRift.translationId("quest", "skillthread.title"), "Entrega Hilo de Habilidad");
        add(WanderersOfTheRift.translationId("quest", "skillthread.description"),
                "He escuchado que hay un hilo extraño que se puede encontrar solo en las grietas y que posee la clave para desbloquear todo el potencial de las habilidades. ¿Podrías traerme una muestra?");
        add(WanderersOfTheRift.translationId("quest", "gold_and_iron.title"), "Prueba los Productos");
        add(WanderersOfTheRift.translationId("quest", "gold_and_iron.description"),
                "Psst. Pareces un cliente exigente. Si me traes algunos metales tengo para ti unos accesorios brillantes y poderosos.");
        add(WanderersOfTheRift.translationId("quest", "kill_skeletons.title"), "Derrota a Esqueletos");
        add(WanderersOfTheRift.translationId("quest", "kill_skeletons.description"),
                "Los huesos me aterran, ¡sálvame!");
        add(WanderersOfTheRift.translationId("quest", "complete_rift.title"), "Completa Grietas");
        add(WanderersOfTheRift.translationId("quest", "complete_rift.description"), "Prueba tu temple.");
        add(WanderersOfTheRift.translationId("quest", "bring_big_fish.title"), "Prueba tu valor");
        add(WanderersOfTheRift.translationId("quest", "bring_big_fish.description"),
                "Tengo una miausión para ti: tráeme algunos peces que me muero de hambre");
        add(WanderersOfTheRift.translationId("quest", "bring_fish.title"), "Pez del día");
        add(WanderersOfTheRift.translationId("quest", "bring_fish.description"),
                "¿Quizás podrías conseguirme otro pez?");
        add(WanderersOfTheRift.translationId("quest", "deliver_fish.title"), "Una entrega especial");
        add(WanderersOfTheRift.translationId("quest", "deliver_fish.description"),
                "El Mercader de Cats Cradle ha estado trabajando muy duro últimamente y me preocupa que se haya estado saltando la comida. ¿Podrías llevarle un pescado de mi parte?");

        add("mobgroup.minecraft.skeletons", "Esqueletos");
        add("modifier.wotr.projectile_count", "Número de Proyectiles");
        add("modifier.wotr.projectile_pierce", "Perforación de Proyectiles");
        add("modifier_effect.wotr.ability", "Usa %s cuando %s");

        add(Append.TYPE.translationKey(), "Añade %s");
        add(Prepend.TYPE.translationKey(), "Añade %s al inicio");
        add(Clear.TYPE.translationKey(), "Quita todo");
        add(Drop.TYPE.translationKey(), "Quita %s del inicio");
        add(DropLast.TYPE.translationKey(), "Quita %s");
        add(WotrRiftLayoutLayers.PREDEFINED_LAYER.getKey().location().toLanguageKey("layout_layer"), "%s Sala");
        add(WotrRiftLayoutLayers.RING_LAYER.getKey().location().toLanguageKey("layout_layer"), "Anillo de Salas %s");
        add(WotrRiftLayoutLayers.BOXED_LAYER.getKey().location().toLanguageKey("layout_layer"), "Grupo de Salas");
        add(WotrRiftLayoutLayers.CHAOS_LAYER.getKey().location().toLanguageKey("layout_layer"), "%s Salas");
        add("template_pool.wotr.rift.room_portal", "Portal");
        add("template_pool.wotr.rift.room_stable", "Estable");
        add("template_pool.wotr.rift.room_unstable", "Inestable");
        add("template_pool.wotr.rift.room_chaos", "Kaos");

        add(WanderersOfTheRift.translationId("toast", "quest.complete"), "Misión Completa");
        add(WanderersOfTheRift.translationId("toast", "guild.rank"), "Un Gremio ha subido de Nivel");
    }

    private void addRunegems() {
        WotrRuneGemDataProvider.DATA
                .forEach((key, value) -> add(WanderersOfTheRift.translationId("runegem", key.location().getPath()),
                        snakeCaseToCapitalizedCase(key.location().getPath()) + " Gema Rúnica"));
    }

    private void addModifiers() {
        WotrModifierProvider.DATA
                .forEach((key, value) -> add(WanderersOfTheRift.translationId("modifier", key.location().getPath()),
                        snakeCaseToCapitalizedCase(key.location().getPath())));
    }

    private void addEssenceType(String id, String value) {
        add(EssenceValue.ESSENCE_TYPE_PREFIX + "." + WanderersOfTheRift.MODID + "." + id, value);
    }

    private void addTheme(String id, String value) {
        add("rift_theme." + WanderersOfTheRift.MODID + "." + id, value);
    }

    private static @NotNull String getTranslationString(Block block) {
        String idString = BuiltInRegistries.BLOCK.getKey(block).getPath();
        return snakeCaseToCapitalizedCase(idString);
    }

    private static @NotNull String snakeCaseToCapitalizedCase(String idString) {
        StringBuilder sb = new StringBuilder();
        for (String word : idString.toLowerCase(Locale.ROOT).split("_")) {
            sb.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
            sb.append(word.substring(1));
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
