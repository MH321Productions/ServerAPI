package io.github.mh321productions.serverapi.module.npc.metadata

import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose
import com.comphenix.protocol.wrappers.EnumWrappers.Particle
import com.comphenix.protocol.wrappers.Vector3F
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import com.comphenix.protocol.wrappers.nbt.NbtFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import java.util.Optional
import java.util.UUID

/**
 * Diese Registry beinhaltet alle Metadaten für alle Entities
 *
 * (Stand 17.9.2023, Version 1.20).
 *
 * Diese können vollständig bei [Wiki.vg](https://wiki.vg/Entity_metadata) eingesehen werden
 */
class MetadataRegistry {

    data class RegistryEntry(val index: Int, val serializer: WrappedDataWatcher.Serializer, val defaultValue: Any?)

    companion object {
        private fun getBoolSerializer(opt: Boolean = false) = Registry.get(Class.forName("java.lang.Boolean"), opt)
        private fun getByteSerializer(opt: Boolean = false) = Registry.get(Class.forName("java.lang.Byte"), opt)
        private fun getIntSerializer(opt: Boolean = false) = Registry.get(Class.forName("java.lang.Integer"), opt)
        private fun getFloatSerializer(opt: Boolean = false) = Registry.get(Class.forName("java.lang.Float"), opt)
        private fun getStringSerializer(opt: Boolean = false) = Registry.get(Class.forName("java.lang.String"), opt)


        private val emptyVector3F = Vector3F(0.0f, 0.0f, 0.0f)
        private val emptyItemSlot : Optional<ItemStack> = Optional.empty()
        private val emptyChat : Optional<WrappedChatComponent> = Optional.empty()
        private val emptyBlockPosition = BlockPosition(0, 0, 0)
        private val emptyOptBlockPosition : Optional<BlockPosition> = Optional.empty()
        private val emptyOptInt : Optional<Int> = Optional.empty()
        private val emptyOptUUID : Optional<UUID> = Optional.empty()
        private val emptyNbtCompound = NbtFactory.ofCompound("")
    }

    /**
     * Die Basisklasse
     * @see <a href="https://wiki.vg/Entity_metadata#Entity">Wiki.vg</a>
     */
    object Entity {
        val flags = RegistryEntry(0, getByteSerializer(), 0.toByte())
        val airTicks = RegistryEntry(1, getIntSerializer(), 300)
        val customName = RegistryEntry(2, Registry.getChatComponentSerializer(true), emptyChat)
        val isCustomNameVisible = RegistryEntry(3, getBoolSerializer(), false)
        val isSilent = RegistryEntry(4, getBoolSerializer(), false)
        val hasNoGravity = RegistryEntry(5, getBoolSerializer(), false)
        val pose = RegistryEntry(6, Registry.get(EntityPose::class.java), EntityPose.STANDING)
        val ticksFrozenInPowderedSnow = RegistryEntry(7, getIntSerializer(), 0)

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Interaction">Wiki.vg</a>
         */
        object Interaction {
            val width = RegistryEntry(8, getFloatSerializer(), 1.0f)
            val height = RegistryEntry(9, getFloatSerializer(), 1.0f)
            val responsive = RegistryEntry(10, getBoolSerializer(), false)
        }

        /**
         * Display Entity (ab 1.20)
         * @see <a href="https://wiki.vg/Entity_metadata#Display">Wiki.vg</a>
         */
        object Display {
            val interpolationDelay = RegistryEntry(8, getIntSerializer(), 0)
            val interpolationDuration = RegistryEntry(9, getIntSerializer(), 0)
            val translation = RegistryEntry(10, Registry.getVectorSerializer(), emptyVector3F)
            val scale = RegistryEntry(11, Registry.getVectorSerializer(), emptyVector3F)
            //val rotationLeft = RegistryEntry(12, Registry.get()) //Quaternion
            //val rotationRight = Registry(13)
            val billboardConstraints = RegistryEntry(14, getByteSerializer(), 0.toByte())
            val brightnessOverride = RegistryEntry(15, getIntSerializer(), -1)
            val viewRange = RegistryEntry(16, getFloatSerializer(), 1.0f)
            val shadowRadius = RegistryEntry(17, getFloatSerializer(), 0.0f)
            val shadowStrength = RegistryEntry(18, getFloatSerializer(), 1.0f)
            val width = RegistryEntry(19, getFloatSerializer(), 0.0f)
            val height = RegistryEntry(20, getFloatSerializer(), 0.0f)
            val glowColorOverride = RegistryEntry(21, getIntSerializer(), -1)

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Block_Display">Wiki.vg</a>
             */
            object BlockDisplay {
                /**
                 * BlockId: VarInt
                 */
                val blockId = RegistryEntry(22, getIntSerializer(), 0)
            }

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Item_Display">Wiki.vg</a>
             */
            object ItemDisplay {
                val displayedItem = RegistryEntry(22, Registry.getItemStackSerializer(true), emptyItemSlot)
                val displayType = RegistryEntry(23, getByteSerializer(), 0.toByte())
            }

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Text_Display">Wiki.vg</a>
             */
            object TextDisplay {
                val text = RegistryEntry(22, Registry.getChatComponentSerializer(), emptyChat)
                val lineWidth = RegistryEntry(23, getIntSerializer(), 200)
                val backgroundColor = RegistryEntry(24, getIntSerializer(), 0x40000000)
                val textOpacity = RegistryEntry(25, getByteSerializer(), (-1).toByte())
                val properties = RegistryEntry(26, getByteSerializer(), 0.toByte())
            }
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Thrown_Item_Projectile">Wiki.vg</a>
         */
        object ThrownItemProjectile {
            val item = RegistryEntry(8, Registry.getItemStackSerializer(true), emptyItemSlot)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Eye_of_Ender">Wiki.vg</a>
         */
        object EyeOfEnder {
            val item = RegistryEntry(8, Registry.getItemStackSerializer(true), emptyItemSlot)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Falling_Block">Wiki.vg</a>
         */
        object FallingBlock {
            val spawnPosition = RegistryEntry(8, Registry.getBlockPositionSerializer(false), emptyBlockPosition)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Area_Effect_Cloud">Wiki.vg</a>
         */
        object AreaEffectCloud {
            val radius = RegistryEntry(8, getFloatSerializer(), 0.5f)
            val color = RegistryEntry(9, getIntSerializer(), 0)
            val ignoreRadius = RegistryEntry(10, getBoolSerializer(), false)
            val particle = RegistryEntry(11, Registry.get(Particle::class.java), Particle.CLOUD)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Fishing_Hook">Wiki.vg</a>
         */
        object FishingHook {
            val hookedEntityId = RegistryEntry(8, getIntSerializer(), 0)
            val isCatchable = RegistryEntry(9, getBoolSerializer(), false)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Abstract_Arrow">Wiki.vg</a>
         */
        object AbstractArrow {
            val flags = RegistryEntry(8, getByteSerializer(), 0.toByte())
            val piercingLevel = RegistryEntry(9, getByteSerializer(), 0.toByte())

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Arrow">Wiki.vg</a>
             */
            object Arrow {
                val color = RegistryEntry(10, getIntSerializer(), -1)
            }

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Thrown_Trident">Wiki.vg</a>
             */
            object ThrownTrident {
                val loyaltyLevel = RegistryEntry(10, getIntSerializer(), 0)
                val hasEnchantmentGlint = RegistryEntry(11, getBoolSerializer(), false)
            }
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Boat">Wiki.vg</a>
         */
        object Boat {
            val timeSinceLastHit = RegistryEntry(8, getIntSerializer(), 0)
            val forwardDirection = RegistryEntry(9, getIntSerializer(), 1)
            val damageTaken = RegistryEntry(10, getFloatSerializer(), 0.0f)
            val type = RegistryEntry(11, getIntSerializer(), 0)
            val isLeftPaddleTurning = RegistryEntry(12, getBoolSerializer(), false)
            val isRightPaddleTurning = RegistryEntry(13, getBoolSerializer(), false)
            val splashTimer = RegistryEntry(14, getIntSerializer(), 0)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#End_Crystal">Wiki.vg</a>
         */
        object EndCrystal {
            val beamTarget = RegistryEntry(8, Registry.getBlockPositionSerializer(true), emptyOptBlockPosition)
            val showBottom = RegistryEntry(9, getBoolSerializer(), true)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Small_Fireball">Wiki.vg</a>
         */
        object SmallFireball {
            val item = RegistryEntry(8, Registry.getItemStackSerializer(true), emptyItemSlot)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Fireball">Wiki.vg</a>
         */
        object Fireball {
            val item = RegistryEntry(8, Registry.getItemStackSerializer(true), emptyItemSlot)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Wither_Skull">Wiki.vg</a>
         */
        object WitherSkull {
            val isInvulnerable = RegistryEntry(8, getBoolSerializer(), false)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Firework_Rocket_Entity">Wiki.vg</a>
         */
        object FireworkRocketEntity {
            val fireworkInfo = RegistryEntry(8, Registry.getItemStackSerializer(true), emptyItemSlot)
            val entityIdOfSender = RegistryEntry(9, getIntSerializer(true), emptyOptInt)
            val isShotAtAngle = RegistryEntry(10, getBoolSerializer(), false)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Item_Frame">Wiki.vg</a>
         */
        object ItemFrame {
            val item = RegistryEntry(8, Registry.getItemStackSerializer(true), emptyItemSlot)
            val rotation = RegistryEntry(9, getIntSerializer(), 0)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Painting">Wiki.vg</a>
         */
        object Painting {
            val paintingType = RegistryEntry(8, getIntSerializer(), 0)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Item_Entity">Wiki.vg</a>
         */
        object ItemEntity {
            val item = RegistryEntry(8, Registry.getItemStackSerializer(true), emptyItemSlot)
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Living_Entity">Wiki.vg</a>
         */
        object LivingEntity {
            val handStates = RegistryEntry(8, getByteSerializer(), 0.toByte())
            val health = RegistryEntry(9, getFloatSerializer(), 1.0f)
            val potionEffectColor = RegistryEntry(10, getIntSerializer(), 0)
            val isPotionEffectAmbient = RegistryEntry(11, getBoolSerializer(), false)
            val numberOfArrowsInEntity = RegistryEntry(12, getIntSerializer(), 0)
            val numberOfBeeStingersInEntity = RegistryEntry(13, getIntSerializer(), 0)
            val currentlyUsedBed = RegistryEntry(14, Registry.getBlockPositionSerializer(true), emptyOptBlockPosition)

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Player">Wiki.vg</a>
             */
            object Player {
                val additionalHearts = RegistryEntry(15, getFloatSerializer(), 0.0f)
                val score = RegistryEntry(16, getIntSerializer(), 0)
                val skinParts = RegistryEntry(17, getByteSerializer(), 0.toByte())
                val mainHand = RegistryEntry(18, getByteSerializer(), 1.toByte())
                val leftShoulderEntityData = RegistryEntry(19, Registry.getNBTCompoundSerializer(), emptyNbtCompound)
                val rightShoulderEntityData = RegistryEntry(20, Registry.getNBTCompoundSerializer(), emptyNbtCompound)
            }

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Armor_Stand">Wiki.vg</a>
             */
            object ArmorStand {
                val flags = RegistryEntry(15, getByteSerializer(), 0.toByte())
                val headRotation = RegistryEntry(16, Registry.getVectorSerializer(), emptyVector3F)
                val bodyRotation = RegistryEntry(17, Registry.getVectorSerializer(), emptyVector3F)
                val leftArmRotation = RegistryEntry(18, Registry.getVectorSerializer(), Vector3F(-10.0f, 0.0f, -10.0f))
                val rightArmRotation = RegistryEntry(19, Registry.getVectorSerializer(), Vector3F(-15.0f, 0.0f, 10.0f))
                val leftLegRotation = RegistryEntry(20, Registry.getVectorSerializer(), Vector3F(-1.0f, 0.0f, -1.0f))
                val rightLegRotation = RegistryEntry(21, Registry.getVectorSerializer(), Vector3F(1.0f, 0.0f, 1.0f))
            }

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Mob">Wiki.vg</a>
             */
            object Mob {
                val flags = RegistryEntry(15, getByteSerializer(), 0.toByte())

                /**
                 * @see <a href="https://wiki.vg/Entity_metadata#Ambient_Creature">Wiki.vg</a>
                 */
                object AmbientCreature {
                    /**
                     * @see <a href="https://wiki.vg/Entity_metadata#Bat">Wiki.vg</a>
                     */
                    object Bat {
                        val flags = RegistryEntry(16, getByteSerializer(), 0.toByte())
                    }
                }

                /**
                 * @see <a href="https://wiki.vg/Entity_metadata#Pathfinder_Mob">Wiki.vg</a>
                 */
                object PathfinderMob {
                    /**
                     * @see <a href="https://wiki.vg/Entity_metadata#Water_Animal">Wiki.vg</a>
                     */
                    object WaterAnimal {
                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Dolphin">Wiki.vg</a>
                         */
                        object Dolphin {
                            val treasurePosition = RegistryEntry(16, Registry.getBlockPositionSerializer(false), emptyBlockPosition)
                            val hasFish = RegistryEntry(17, getBoolSerializer(), false)
                            val moistureLevel = RegistryEntry(18, getIntSerializer(), 2400)
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Abstract_fish">Wiki.vg</a>
                         */
                        object AbstractFish {
                            val fromBucket = RegistryEntry(16, getBoolSerializer(), false)

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Puffer_fish">Wiki.vg</a>
                             */
                            object PufferFish {
                                val puffState = RegistryEntry(17, getIntSerializer(), 0)
                            }
                        }
                    }

                    /**
                     * @see <a href="https://wiki.vg/Entity_metadata#Ageable_Mob">Wiki.vg</a>
                     */
                    object AgeableMob {
                        val isBaby = RegistryEntry(16, getBoolSerializer(), false)

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Animal">Wiki.vg</a>
                         */
                        object Animal {
                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Sniffer">Wiki.vg</a>
                             */
                            object Sniffer {
                                val snifferState = RegistryEntry(17, getIntSerializer(), 0)
                                val dropSeedAtTick = RegistryEntry(18, getIntSerializer(), 0)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Abstract_Horse">Wiki.vg</a>
                             */
                            object AbstractHorse {
                                val flags = RegistryEntry(17, getByteSerializer(), 0.toByte())

                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Horse">Wiki.vg</a>
                                 */
                                object Horse {
                                    val variant = RegistryEntry(18, getIntSerializer(), 0)
                                }

                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Camel">Wiki.vg</a>
                                 */
                                object Camel {
                                    val isDashing = RegistryEntry(18, getBoolSerializer(), false)
                                    val lastPoseChangeTick = RegistryEntry(19, getIntSerializer(), 0)
                                }

                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Chested_Horse">Wiki.vg</a>
                                 */
                                object ChestedHorse {
                                    val hasChest = RegistryEntry(18, getBoolSerializer(), false)

                                    /**
                                     * @see <a href="https://wiki.vg/Entity_metadata#Llama">Wiki.vg</a>
                                     */
                                    object Llama {
                                        val strength = RegistryEntry(19, getIntSerializer(), 0)
                                        val carpetColor = RegistryEntry(20, getIntSerializer(), -1)
                                        val variant = RegistryEntry(21, getIntSerializer(), 0)
                                    }
                                }
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Axolotl">Wiki.vg</a>
                             */
                            object Axolotl {
                                val variant = RegistryEntry(17, getIntSerializer(), 0)
                                val isPlayingDead = RegistryEntry(18, getBoolSerializer(), false)
                                val isSpawnedFromBucket = RegistryEntry(19, getBoolSerializer(), false)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Bee">Wiki.vg</a>
                             */
                            object Bee {
                                val flags = RegistryEntry(17, getByteSerializer(), 0.toByte())
                                val angerTime = RegistryEntry(18, getIntSerializer(), 0)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Fox">Wiki.vg</a>
                             */
                            object Fox {
                                val type = RegistryEntry(17, getIntSerializer(), 0)
                                val flags = RegistryEntry(18, getByteSerializer(), 0.toByte())
                                val firstUUID = RegistryEntry(19, Registry.getUUIDSerializer(true), emptyOptUUID)
                                val secondUUID = RegistryEntry(20, Registry.getUUIDSerializer(true), emptyOptUUID)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Frog">Wiki.vg</a>
                             */
                            object Frog {
                                val variant = RegistryEntry(17, getIntSerializer(), 0)
                                val tongueTarget = RegistryEntry(18, getIntSerializer(true), 0)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Ocelot">Wiki.vg</a>
                             */
                            object Ocelot {
                                val isTrusting = RegistryEntry(17, getBoolSerializer(), false)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Panda">Wiki.vg</a>
                             */
                            object Panda {
                                val breedTimer = RegistryEntry(17, getIntSerializer(), 0)
                                val sneezeTimer = RegistryEntry(18, getIntSerializer(), 0)
                                val eatTimer = RegistryEntry(19, getIntSerializer(), 0)
                                val mainGene = RegistryEntry(20, getByteSerializer(), 0.toByte())
                                val hiddenGene = RegistryEntry(21, getByteSerializer(), 0.toByte())
                                val flags = RegistryEntry(22, getByteSerializer(), 0.toByte())
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Pig">Wiki.vg</a>
                             */
                            object Pig {
                                val hasSaddle = RegistryEntry(17, getBoolSerializer(), false)
                                val boostTime = RegistryEntry(18, getIntSerializer(), 0)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Rabbit">Wiki.vg</a>
                             */
                            object Rabbit {
                                val type = RegistryEntry(17, getIntSerializer(), 0)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Turtle">Wiki.vg</a>
                             */
                            object Turtle {
                                val homePos = RegistryEntry(17, Registry.getBlockPositionSerializer(false), emptyBlockPosition)
                                val hasEgg = RegistryEntry(18, getBoolSerializer(), false)
                                val isLayingEgg = RegistryEntry(19, getBoolSerializer(), false)
                                val travelPos = RegistryEntry(20, Registry.getBlockPositionSerializer(false), emptyBlockPosition)
                                val isGoingHome = RegistryEntry(21, getBoolSerializer(), false)
                                val isTraveling = RegistryEntry(22, getBoolSerializer(), false)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Polar_Bear">Wiki.vg</a>
                             */
                            object PolarBear {
                                val isStandingUp = RegistryEntry(17, getBoolSerializer(), false)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Hoglin">Wiki.vg</a>
                             */
                            object Hoglin {
                                val isImmuneToZombification = RegistryEntry(17, getBoolSerializer(), false)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Cow">Wiki.vg</a>
                             */
                            object Cow {
                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Mooshroom">Wiki.vg</a>
                                 */
                                object Mooshroom {
                                    val variant = RegistryEntry(17, getStringSerializer(), "red")
                                }
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Sheep">Wiki.vg</a>
                             */
                            object Sheep {
                                val flags = RegistryEntry(17, getByteSerializer(), 0.toByte())
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Strider">Wiki.vg</a>
                             */
                            object Strider {
                                val boostTime = RegistryEntry(17, getIntSerializer(), 0)
                                val isShaking = RegistryEntry(18, getBoolSerializer(), false)
                                val hasSaddle = RegistryEntry(19, getBoolSerializer(), false)
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Tameable_Animal">Wiki.vg</a>
                             */
                            object TameableAnimal {
                                val flags = RegistryEntry(17, getByteSerializer(), 0.toByte())
                                val owner = RegistryEntry(18, Registry.getUUIDSerializer(true), emptyOptUUID)

                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Cat">Wiki.vg</a>
                                 */
                                object Cat {
                                    val variant = RegistryEntry(19, getIntSerializer(), 0)
                                    val isLying = RegistryEntry(20, getBoolSerializer(), false)
                                    val isRelaxed = RegistryEntry(21, getBoolSerializer(), false)
                                    val collarColor = RegistryEntry(22, getIntSerializer(), 14)
                                }

                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Wolf">Wiki.vg</a>
                                 */
                                object Wolf {
                                    val isBegging = RegistryEntry(19, getBoolSerializer(), false)
                                    val collarColor = RegistryEntry(20, getIntSerializer(), 14)
                                    val angerTime = RegistryEntry(21, getIntSerializer(), 0)
                                }

                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Parrot">Wiki.vg</a>
                                 */
                                object Parrot {
                                    val variant = RegistryEntry(19, getIntSerializer(), 0)
                                }
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Goat">Wiki.vg</a>
                             */
                            object Goat {
                                val isScreaming = RegistryEntry(17, getBoolSerializer(), false)
                                val hasLeftHorn = RegistryEntry(18, getBoolSerializer(), true)
                                val hasRightHorn = RegistryEntry(19, getBoolSerializer(), true)
                            }
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Abstract_Villager">Wiki.vg</a>
                         */
                        object AbstractVillager {
                            val headShakeTimer = RegistryEntry(17, getIntSerializer(), 0)

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Villager">Wiki.vg</a>
                             */
                            object Villager {
                                //val villagerData = RegistryEntry(18, Registry.)
                            }
                        }
                    }

                    /**
                     * @see <a href="https://wiki.vg/Entity_metadata#Abstract_Golem">Wiki.vg</a>
                     */
                    object AbstractGolem {
                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Iron_Golem">Wiki.vg</a>
                         */
                        object IronGolem {
                            val flags = RegistryEntry(16, getByteSerializer(), 0.toByte())
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Snow_Golem">Wiki.vg</a>
                         */
                        object SnowGolem {
                            val flags = RegistryEntry(16, getByteSerializer(), 0.toByte())
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Shulker">Wiki.vg</a>
                         */
                        object Shulker {
                            val attachFace = RegistryEntry(16, getIntSerializer(), 0)
                            val attachPosition = RegistryEntry(17, Registry.getBlockPositionSerializer(true), emptyOptBlockPosition)
                            val shieldHeight = RegistryEntry(18, getByteSerializer(), 0.toByte())
                            val color = RegistryEntry(19, getByteSerializer(), 10.toByte())
                        }
                    }

                    /**
                     * @see <a href="https://wiki.vg/Entity_metadata#Monster">Wiki.vg</a>
                     */
                    object Monster {
                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Base_Piglin">Wiki.vg</a>
                         */
                        object BasePiglin {
                            val isImmuneToZombification = RegistryEntry(16, getBoolSerializer(), false)

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Piglin">Wiki.vg</a>
                             */
                            object Piglin {
                                val isBaby = RegistryEntry(17, getBoolSerializer(), false)
                                val isChargingCrossbow = RegistryEntry(18, getBoolSerializer(), false)
                                val isDancing = RegistryEntry(19, getBoolSerializer(), false)
                            }
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Blaze">Wiki.vg</a>
                         */
                        object Blaze {
                            val flags = RegistryEntry(16, getByteSerializer(), 0.toByte())
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Creeper">Wiki.vg</a>
                         */
                        object Creeper {
                            val state = RegistryEntry(16, getIntSerializer(), -1)
                            val isCharged = RegistryEntry(17, getBoolSerializer(), false)
                            val isIgnited = RegistryEntry(18, getBoolSerializer(), false)
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Guardian">Wiki.vg</a>
                         */
                        object Guardian {
                            val isRetractingSpikes = RegistryEntry(16, getBoolSerializer(), false)
                            val targetEID = RegistryEntry(17, getIntSerializer(), 0)
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Raider">Wiki.vg</a>
                         */
                        object Raider {
                            val isCelebrating = RegistryEntry(16, getBoolSerializer(), false)

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Abstract_Illager">Wiki.vg</a>
                             */
                            object AbstractIllager {
                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Pillager">Wiki.vg</a>
                                 */
                                object Pillager {
                                    val isCharging = RegistryEntry(17, getBoolSerializer(), false)
                                }

                                /**
                                 * @see <a href="https://wiki.vg/Entity_metadata#Spellcaster_Illager">Wiki.vg</a>
                                 */
                                object SpellcasterIllager {
                                    val spell = RegistryEntry(17, getByteSerializer(), 0.toByte())
                                }
                            }

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Witch">Wiki.vg</a>
                             */
                            object Witch {
                                val isDrinkingPotion = RegistryEntry(17, getBoolSerializer(), false)
                            }
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Vex">Wiki.vg</a>
                         */
                        object Vex {
                            val flags = RegistryEntry(16, getByteSerializer(), 0.toByte())
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Spider">Wiki.vg</a>
                         */
                        object Spider {
                            val flags = RegistryEntry(16, getByteSerializer(), 0.toByte())
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Warden">Wiki.vg</a>
                         */
                        object Warden {
                            val angerLevel = RegistryEntry(16, getIntSerializer(), 0)
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Wither">Wiki.vg</a>
                         */
                        object Wither {
                            val centerHeadsTarget = RegistryEntry(16, getIntSerializer(), 0)
                            val leftHeadsTarget = RegistryEntry(17, getIntSerializer(), 0)
                            val rightHeadsTarget = RegistryEntry(18, getIntSerializer(), 0)
                            val invulnerableTime = RegistryEntry(19, getIntSerializer(), 0)
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Zoglin">Wiki.vg</a>
                         */
                        object Zoglin {
                            val isBaby = RegistryEntry(16, getBoolSerializer(), false)
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Zombie">Wiki.vg</a>
                         */
                        object Zombie {
                            val isBaby = RegistryEntry(16, getBoolSerializer(), false)
                            val unused = RegistryEntry(17, getIntSerializer(), 0)
                            val isBecomingADrowned = RegistryEntry(18, getBoolSerializer(), false)

                            /**
                             * @see <a href="https://wiki.vg/Entity_metadata#Zombie_Villager">Wiki.vg</a>
                             */
                            object ZombieVillager {
                                val isConverting = RegistryEntry(19, getBoolSerializer(), false)
                                //val villagerData
                            }
                        }

                        /**
                         * @see <a href="https://wiki.vg/Entity_metadata#Enderman">Wiki.vg</a>
                         */
                        object Enderman {
                            val carriedBlock = RegistryEntry(16, getIntSerializer(true), emptyOptInt)
                            val isScreaming = RegistryEntry(17, getBoolSerializer(), false)
                            val isStaring = RegistryEntry(18, getBoolSerializer(), false)
                        }
                    }
                }

                /**
                 * @see <a href="https://wiki.vg/Entity_metadata#Ender_Dragon">Wiki.vg</a>
                 */
                object EnderDragon {
                    val dragonPhase = RegistryEntry(16, getIntSerializer(), 10)
                }

                /**
                 * @see <a href="https://wiki.vg/Entity_metadata#Flying">Wiki.vg</a>
                 */
                object Flying {
                    /**
                     * @see <a href="https://wiki.vg/Entity_metadata#Ghast">Wiki.vg</a>
                     */
                    object Ghast {
                        val isAttacking = RegistryEntry(16, getBoolSerializer(), false)
                    }

                    /**
                     * @see <a href="https://wiki.vg/Entity_metadata#Phantom">Wiki.vg</a>
                     */
                    object Phantom {
                        val size = RegistryEntry(16, getIntSerializer(), 0)
                    }
                }

                /**
                 * @see <a href="https://wiki.vg/Entity_metadata#Slime">Wiki.vg</a>
                 */
                object Slime {
                    val size = RegistryEntry(16, getIntSerializer(), 1)
                }
            }
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Abstract_Minecart">Wiki.vg</a>
         */
        object AbstractMinecart {
            val shakingPower = RegistryEntry(8, getIntSerializer(), 0)
            val shakingDirection = RegistryEntry(9, getIntSerializer(), 1)
            val shakingMultiplier = RegistryEntry(10, getFloatSerializer(), 0.0f)
            val customBlockIdAndDamage = RegistryEntry(11, getIntSerializer(), 0)
            val customBlockYPosition = RegistryEntry(12, getIntSerializer(), 6)
            val showCustomBlock = RegistryEntry(13, getBoolSerializer(), false)

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Minecart_Furnace">Wiki.vg</a>
             */
            object MinecartFurnace {
                val hasFuel = RegistryEntry(14, getBoolSerializer(), false)
            }

            /**
             * @see <a href="https://wiki.vg/Entity_metadata#Minecart_Command_Block">Wiki.vg</a>
             */
            object MinecartCommandBlock {
                val command = RegistryEntry(14, getStringSerializer(), "")
                val lastOutput = RegistryEntry(15, Registry.getChatComponentSerializer(), WrappedChatComponent.fromText(""))
            }
        }

        /**
         * @see <a href="https://wiki.vg/Entity_metadata#Primed_Tnt">Wiki.vg</a>
         */
        object PrimedTNT {
            val fuseTime = RegistryEntry(8, getIntSerializer(), 80)
        }
    }
}