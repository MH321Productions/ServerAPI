package io.github.mh321productions.serverapi.module.npc.metadata

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer
import io.github.mh321productions.serverapi.module.npc.NPC

/**
 * Diese Klasse beinhaltet die (veränderten) Metadaten eines NPCs.
 *
 * Diese können manuell oder über die [MetadataRegistry] gesetzt werden
 */
class MetadataStorage(private val npc: NPC) {

    data class MetadataEntry(val serializer: Serializer, val value: Any?)

    private val entries = mutableMapOf<Int, MetadataEntry>()
    private var dirty = false

    fun setEntry(index: Int, serializer: Serializer, value: Any?) {
        entries[index] = MetadataEntry(serializer, value)
        dirty = true
    }

    fun setEntry(entry: MetadataRegistry.RegistryEntry, value: Any? = entry.defaultValue) {
        entries[entry.index] = MetadataEntry(entry.serializer, value)
        dirty = true
    }

    fun getEntries() = entries.toMap()

    fun getEntry(index: Int) = entries[index]

    /**
     * Entfernt einen Eintrag, setzt ihn also auf den Standardwert zurück
     */
    fun removeEntry(index: Int) {
        entries.remove(index)
        dirty = true
    }

    fun isDirty() = dirty

    /**
     * Packt alle Metadaten in ein [Metadata Packet](https://wiki.vg/Protocol#Set_Entity_Metadata)
     */
    fun generatePacket(clearDirtyState : Boolean = true) : PacketContainer {
        val metadata = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        metadata.modifier.writeDefaults()
        metadata.integers.write(0, npc.entityId)
        metadata.dataValueCollectionModifier.write(0, entries
            .map { (index, entry) -> WrappedDataValue(index, entry.serializer, entry.value) }
            .toList()
        )

        if (clearDirtyState) dirty = false

        return metadata
    }
}