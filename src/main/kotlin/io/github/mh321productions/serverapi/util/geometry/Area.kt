package io.github.mh321productions.serverapi.util.geometry

import org.bukkit.Location
import org.bukkit.util.Vector

class Area(v1: Vector, v2: Vector) {

    constructor(center: Vector, dx: Double, dy: Double, dz: Double) : this(center - Vector(dx, dy, dz), center + Vector(dx, dy, dz))
    constructor(center: Vector, radius: Double) : this(center, radius, radius, radius)

    val v1 = Vector.getMinimum(v1, v2)
    val v2 = Vector.getMaximum(v1, v2)

    infix operator fun contains(vec: Vector): Boolean {
        return (
            vec.x in v1.x .. v2.x &&
            vec.y in v1.y .. v2.y &&
            vec.z in v1.z .. v2.z
        )
    }

    infix operator fun contains(loc: Location) = contains(loc.toVector())
}