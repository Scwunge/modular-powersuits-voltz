# Voltz integration notes (Phase 2)

Target: the "Ai Voltz 1.21.1" pack (NeoForge 1.21.1, Mekanism 10.7.19, AE2 19.2, Create 6.0, JEI 19.51, Jade 15.10,
almostunified, Open Parties and Claims, ...). Private: nothing here is pushed or published.

## Done and how it was checked

| Area | What | Checked by |
|---|---|---|
| Energy (FE) | Armor, power fist, batteries and the Charging Base all expose `Capabilities.EnergyStorage` (item/block). `ModularItemEnergyWrapper` fixed: no energy created or voided when a stack has no modular capability, no int overflow in stored/max totals, no module write-back on simulated transfers. | Code read + compile. **Not tested in-game against a Mekanism/AE2/Create charger.** |
| Recipes | `numina:component_plating_netherite` had no `.save()` (netherite plating module was uncraftable) - fixed. Control circuits 1-4 get Mekanism variants (`c:circuits/basic..ultimate`) when Mekanism is loaded; the vanilla-material originals are conditioned on Mekanism being absent, so there is exactly one active recipe per circuit. Stale checked-in JSON for stone pickaxe/shovel/rototiller modules regenerated. | `runData` output reviewed; every builder in both generators calls `save`; no two recipes share a result. **Condition handling not yet loaded in a running game.** |
| Mekanism | Radiation shielding capability on all 16 armor pieces (hazmat split 0.25/0.4/0.2/0.15, scaled per tier by `config/lehjr/powersuits/common/compat/mekanism.toml`). Mekanism's `RadiationUtil.getRadiationResistance` sums this capability over the armor slots, so registration alone is enough. | Disassembly of the pack's Mekanism jar + compile. **Not tested in-game.** |
| JEI | Info pages (Tinker Table, Charging Base, armor, fist). All crafting recipes already show in JEI because the custom recipe classes extend `ShapedRecipe`. | Compile only. |
| Jade | No custom provider needed: Jade shows FE for any block exposing the standard block energy capability, which the Charging Base does. | Reasoning only. |
| almostunified | Nothing to do: recipes use `c:` tags, and none of our items belong to a unified tag group. | Reasoning only. |
| Pack conflicts | No mod id or `numina` / `powersuits` namespace collisions across the 130 jars. Our mixins (`RangedWrapper`, `UseOnContext`) do not overlap those of grapplemod, warfare_wings or militaristic_armor. `Z` (go down) is not bound by anything in the instance. create_jetpack, warfare_wings and militaristic_armor use the same chest/armor slots as the power armor, so they are mutually exclusive by slot. | Static analysis only. **Runtime flight interplay (MPS flight control + grapplemod motion) is untested.** |

## Findings for Phase 1 (not changed by Phase 2)

- **Claim bypass:** `VeinMinerModule`, `TunnelBoreModule` and `SelectiveMiner` break extra blocks with
  `level.destroyBlock(pos, false, player, 512)`, which never fires `BlockEvent.BreakEvent`. Open Parties and Claims
  (and every protection mod) hooks that event, so everything except the first block bypasses claims. Post a
  `BreakEvent` per extra block and skip when cancelled. `SpinningBladeEntity` has the same pattern for shearable blocks.
- **Griefing explosion:** `PlasmaBallEntity` explodes with `ExplosionInteraction.TNT` whenever mobGriefing is on.
  Needs a "plasma destroys blocks" config, default false.
- `MovementModuleConfig`: the Dimensional Rift heat key reuses `MPSConstants.RANGE_MULTIPLIER` as its name.
- `EnergyGenerationModuleConfig` exists but is not registered in `MPSConfigurations` (listed as TODO there).
- `NuminaFOVUpdateEventHandler`: key name `"key..numina.fovfixtoggle"` has a doubled dot.
- `KeymappingKeyHandler` builds key names with `Component.translatable(..).getString()` at class-init time, which may
  resolve before the language is loaded.
- `Lux Capacitor` block item has no recipe (creative tab only; the module places it). Probably intended.

## Dev builds

`runData`, `runClient` and `runServer` need the upstream author's private `libs/` jars (`localRuntime "blank:..."`).
On any other checkout use:

    gradlew --init-script voltz/dev/skip-local-libs.init.gradle runData

House limits: `-Xmx2G`, 2 workers, no daemon, BelowNormal priority.

## Still to do (waiting on Phase 1)

- Shipped server config: waiting for the final config file names, paths and COMMON/SERVER types.
- Claim-safe breaking and plasma block damage config (files owned by Phase 1).
- Voltz-material rework of the new generator/coolant recipes once Phase 1 commits them.
- Copy the final jars into the instance and boot-test. A dedicated-server boot also needs the Minecraft EULA accepted
  by the user.
