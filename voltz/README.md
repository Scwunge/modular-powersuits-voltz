# Voltz integration notes (Phase 2)

Target: the "Ai Voltz 1.21.1" pack (NeoForge 1.21.1, Mekanism 10.7.19, AE2 19.2, Create 6.0, JEI 19.51, Jade 15.10,
almostunified, Open Parties and Claims, ...). Private: nothing here is pushed or published.

## Pack files

`pack/defaultconfigs/` mirrors the pack's `minecraft/defaultconfigs/`. FML copies these files into every **new** world's
`serverconfig/`; for an existing world, copy them into `<world>/serverconfig/` by hand. All gameplay configs are SERVER
configs (stored per world, synced to clients), so the server's values apply to everyone.

Only files that differ from the mod defaults are shipped:

| File | Change from mod default | Why |
|---|---|---|
| `powersuits/items/modules/weapon.toml` | Lightning Summoner `isAllowed = false` | Each use spawns a real lightning bolt (fire, charged creepers) up to 64 blocks away. |

Already safe by default, nothing shipped: plasma explosions don't break blocks (`Plasma_Cannon.explosionsDestroyBlocks =
false`, and even when true they also need the mobGriefing gamerule); area mining fires `BlockEvent.BreakEvent` per block,
so claims apply (Phase 1). Blink Drive raytraces to the first block, so it can't pass through walls. Dimensional Rift only
moves between the Overworld and the Nether, so it doesn't skip Ad Astra progression.

Mekanism integration settings are a COMMON config (`config/lehjr/powersuits/common/compat/mekanism.toml`); defaults are
fine and it is not shipped.

How the shipped file was made and checked: defaults were generated from the mod's own `ModConfigSpec`s, then edited, then
loaded through the spec with FML-style correction; it loads with zero value corrections, so FML will not reset or rewrite it.

## Done and how it was checked

| Area | What | Checked by |
|---|---|---|
| Energy (FE) | Armor, power fist, batteries and the Charging Base expose `Capabilities.EnergyStorage`. `ModularItemEnergyWrapper` fixed: no energy created or voided when a stack has no modular capability, no int overflow in totals, no write-back on simulated transfers. | Code read + compile. **Not tested in-game with a Mekanism/AE2/Create charger.** |
| Recipes | Netherite plating component recipe was never saved (module uncraftable): fixed. With Mekanism loaded, control circuits 1-4 use `c:circuits/basic..ultimate` and the tier 2-4 upgrade templates use `c:alloys/advanced..ultimate`; the vanilla-material originals are conditioned on Mekanism being absent. Smithing upgrade materials and the combustion generator's furnace use `c:` tags. | `runData`: 140 recipes, 133 results; the only results with two recipes are the 7 mutually exclusive Mekanism/non-Mekanism pairs; every item has a recipe except the lux capacitor block and the plasma ball model (not craftable by design). **Conditions not yet loaded in a running game.** |
| Mekanism | Radiation shielding capability on all 16 armor pieces (hazmat split 0.25/0.4/0.2/0.15 per piece, scaled by tier). Mekanism's `RadiationUtil.getRadiationResistance` sums this capability over the armor slots. | Disassembly of the pack's Mekanism jar + compile. **Not tested in-game.** |
| JEI | Info pages (Tinker Table, Charging Base, armor, fist), plugin uid `powersuits:jei`. Upstream's Numina plugin (battery subtypes, smithing extension) is separate; no overlap. | Compile. |
| Jade | Nothing needed: Jade shows FE for blocks exposing the standard energy capability (Charging Base does). | Reasoning only. |
| almostunified | Nothing needed: recipes use `c:` tags. | Reasoning only. |
| Pack conflicts | No mod id or namespace collisions across the 130 jars; our mixins don't overlap grapplemod/warfare_wings/militaristic_armor; `Z` is unbound in the instance. Power armor, create_jetpack, warfare_wings and militaristic_armor share armor slots, so they are exclusive by slot. | Static analysis. **Runtime flight interplay (MPS flight + grapplemod) untested.** |
| Config defaults | Lightning Summoner energy default (4.9M) exceeded its max (100k), so FML rewrote it each load: fixed. | Every spec's defaults run through FML correction. Remaining: ArmorConfig tier-4 helm/legs/boots `inventorySlots` default 12 > max 10 (reported to Phase 1). |

## Dev builds

Upstream's `build.gradle` skips the author's private `libs/` jars when absent and takes JEI from maven. Generated
resources are git-ignored, so always run `runData` before building jars: `gradlew runData jar powersuitsJar`. House
limits: `-Xmx2G`, 2 workers, no daemon, BelowNormal priority (`tools/gradle-low.cmd` in the repo root).

## Still open

- In-game verification of everything marked above (client launches belong to Phase 1).
- Dedicated-server boot test with the pack: needs the user to accept the Minecraft EULA for the test server.
