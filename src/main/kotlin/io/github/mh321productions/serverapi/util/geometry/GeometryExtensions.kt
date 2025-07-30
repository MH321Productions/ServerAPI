package io.github.mh321productions.serverapi.util.geometry

import org.bukkit.util.Vector

infix operator fun Vector.plus(other: Vector) = add(other)
infix operator fun Vector.minus(other: Vector) = subtract(other)
infix operator fun Vector.times(other: Vector) = multiply(other)
infix operator fun Vector.div(other: Vector) = divide(other)

infix operator fun Vector.times(other: Int) = multiply(other)
infix operator fun Vector.times(other: Float) = multiply(other)
infix operator fun Vector.times(other: Double) = multiply(other)

infix operator fun Vector.div(other: Int) = multiply(other)
infix operator fun Vector.div(other: Float) = multiply(other)
infix operator fun Vector.div(other: Double) = multiply(other)