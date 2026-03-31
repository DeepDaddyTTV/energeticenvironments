package com.deepdaddyttv.energeticenvironments.gametest;

import com.deepdaddyttv.energeticenvironments.EnergeticEnvironments;
import com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorControllerBlockEntity;
import com.deepdaddyttv.energeticenvironments.common.menu.WindGeneratorControllerMenu;
import com.deepdaddyttv.energeticenvironments.registry.EEBlocks;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(EnergeticEnvironments.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WindGeneratorGameTests {
    private static final String TEMPLATE = "wind_test_pad";
    private static final String BATCH = "wind_regression";

    private WindGeneratorGameTests() {}

    @GameTest(template = TEMPLATE, batch = BATCH)
    public static void validateFormsAndLinksConnector(final GameTestHelper helper) {
        EEGameTestSupport.placeCompletedTierOneStructure(helper);

        final IEnergyStorage unformedConnectorCapability = EEGameTestSupport.getBlockEnergyStorage(helper, EEGameTestSupport.CONNECTOR_POS, Direction.NORTH);
        helper.assertTrue(unformedConnectorCapability == null, "Unformed connectors must not expose energy.");

        helper.runAtTickTime(1L, () -> {
            final WindGeneratorControllerBlockEntity controller = EEGameTestSupport.getWindController(helper);
            helper.assertTrue(
                    controller.handleMenuButton(helper.makeMockPlayer(GameType.CREATIVE), WindGeneratorControllerMenu.BUTTON_VALIDATE),
                    "Expected the completed tier 1 structure to validate. " + EEGameTestSupport.describeTierOneScan(helper, controller)
            );
        });

        helper.succeedWhen(() -> {
            final WindGeneratorControllerBlockEntity currentController = EEGameTestSupport.getWindController(helper);
            helper.assertTrue(currentController.isFormed(), "Controller should be formed after validation.");
            helper.assertBlockEntityData(
                    EEGameTestSupport.CONNECTOR_POS,
                    (com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorConnectorBlockEntity connector) -> connector.getController() != null,
                    () -> "Connector should be linked to the formed controller."
            );

            final IEnergyStorage controllerCapability = EEGameTestSupport.getBlockEnergyStorage(helper, EEGameTestSupport.CONTROLLER_POS, Direction.NORTH);
            helper.assertTrue(controllerCapability == null, "Controller must not expose direct energy capability.");

            final IEnergyStorage connectorCapability = EEGameTestSupport.getBlockEnergyStorage(helper, EEGameTestSupport.CONNECTOR_POS, Direction.NORTH);
            helper.assertTrue(connectorCapability != null, "Connector should expose energy after formation.");
            helper.assertTrue(connectorCapability.canExtract(), "Connector energy capability should allow extraction.");
            helper.assertFalse(connectorCapability.canReceive(), "Connector energy capability should not allow insertion.");
        });
    }

    @GameTest(template = TEMPLATE, batch = BATCH)
    public static void autoBuildPlacesBlocksAndConsumesInventory(final GameTestHelper helper) {
        final WindGeneratorControllerBlockEntity controller = EEGameTestSupport.placeController(helper);
        EEGameTestSupport.fillTierOneAutoBuildInventory(controller);

        helper.runAtTickTime(1L, () -> {
            final WindGeneratorControllerBlockEntity currentController = EEGameTestSupport.getWindController(helper);
            helper.assertTrue(
                    currentController.handleMenuButton(helper.makeMockPlayer(GameType.CREATIVE), WindGeneratorControllerMenu.BUTTON_AUTOBUILD),
                    "Expected autobuild to place the tier 1 wind structure. " + EEGameTestSupport.describeTierOneScan(helper, currentController)
            );
        });

        helper.succeedWhen(() -> {
            final WindGeneratorControllerBlockEntity currentController = EEGameTestSupport.getWindController(helper);
            helper.assertTrue(currentController.isFormed(), "Autobuild should complete and form the generator.");
            helper.assertBlockPresent(EEBlocks.WIND_GENERATOR_CONNECTOR.get(), EEGameTestSupport.CONNECTOR_POS);
            helper.assertBlockPresent(Blocks.SMOOTH_STONE, EEGameTestSupport.TOWER_POS);
            helper.assertBlockPresent(Blocks.GOLD_BLOCK, EEGameTestSupport.HUB_POS);
            helper.assertTrue(EEGameTestSupport.countItems(currentController) == 0, "Autobuild should consume the required tier 1 materials.");
        });
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 120)
    public static void formedGeneratorChargesAdjacentEnergyCell(final GameTestHelper helper) {
        EEGameTestSupport.placeCompletedTierOneStructure(helper);
        EEGameTestSupport.placeEnergyCell(helper);

        helper.runAtTickTime(1L, () -> {
            final WindGeneratorControllerBlockEntity controller = EEGameTestSupport.getWindController(helper);
            helper.assertTrue(
                    controller.handleMenuButton(helper.makeMockPlayer(GameType.CREATIVE), WindGeneratorControllerMenu.BUTTON_VALIDATE),
                    "Expected the completed tier 1 structure to validate. " + EEGameTestSupport.describeTierOneScan(helper, controller)
            );
        });

        helper.succeedWhen(() -> {
            helper.assertTrue(
                    EEGameTestSupport.getEnergyCell(helper).getStoredEnergy() > 0,
                    "Energy cell should charge from the formed wind generator."
            );

            final IEnergyStorage connectorCapability = EEGameTestSupport.getBlockEnergyStorage(helper, EEGameTestSupport.CONNECTOR_POS, Direction.SOUTH);
            helper.assertTrue(connectorCapability != null, "Connector should expose stored energy.");
        });
    }

    @GameTest(template = TEMPLATE, batch = BATCH, timeoutTicks = 120)
    public static void breakingStructureInvalidatesControllerAndDisconnectsConnector(final GameTestHelper helper) {
        EEGameTestSupport.placeCompletedTierOneStructure(helper);

        helper.runAtTickTime(1L, () -> {
            final WindGeneratorControllerBlockEntity controller = EEGameTestSupport.getWindController(helper);
            helper.assertTrue(
                    controller.handleMenuButton(helper.makeMockPlayer(GameType.CREATIVE), WindGeneratorControllerMenu.BUTTON_VALIDATE),
                    "Expected the completed tier 1 structure to validate. " + EEGameTestSupport.describeTierOneScan(helper, controller)
            );
        });

        helper.runAtTickTime(3L, () -> helper.destroyBlock(EEGameTestSupport.TOWER_POS));
        helper.runAtTickTime(30L, () -> {
            final WindGeneratorControllerBlockEntity currentController = EEGameTestSupport.getWindController(helper);
            helper.assertFalse(currentController.isFormed(), "Controller should invalidate after the multiblock is broken.");
            helper.assertBlockEntityData(
                    EEGameTestSupport.CONNECTOR_POS,
                    (com.deepdaddyttv.energeticenvironments.common.blockentity.WindGeneratorConnectorBlockEntity connector) -> connector.getController() == null,
                    () -> "Connector should clear its controller reference after invalidation."
            );

            final IEnergyStorage connectorCapability = EEGameTestSupport.getBlockEnergyStorage(helper, EEGameTestSupport.CONNECTOR_POS, Direction.NORTH);
            helper.assertTrue(connectorCapability == null, "Broken multiblock connectors must not expose energy.");
            helper.succeed();
        });
    }
}
