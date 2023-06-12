package io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property

import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

typealias PropertyMapper<T> = (String) -> T?
typealias PropertyDefaultValueSupplier<T> = () -> T?

class PropertyDelegate<T>(
    private val prefix: String?,
    private val defaultValue: PropertyDefaultValueSupplier<T>?,
    private val mapper: PropertyMapper<T>?
) : ReadWriteProperty<Any?, T> {

    private var _value: T? = null
    private var isInitialized: Long = Long.MIN_VALUE
    private val lock = ReentrantReadWriteLock()

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val needUpdate = lock.read {
            PropertyDelegateManager.isShouldUpdateProperty(isInitialized)
        }

        if (!needUpdate) {
            return _value as T
        }

        return lock.write {
            _value = internalGetValue(property)
            isInitialized = System.currentTimeMillis()
            _value as T
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        lock.write {
            _value = value
            isInitialized = System.currentTimeMillis()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun internalGetValue(property: KProperty<*>): T {
        val propertyName = transformPropertyName(property.name, property.returnType)
        val value = PropertyDelegateManager.getProperty(propertyName)
            ?: defaultValue?.invoke()

        if (value == null) {
            if (property.returnType.isMarkedNullable) {
                return null as T
            } else {
                error("Can not find any value for property with name $propertyName")
            }
        }

        if (value::class == property.returnType) {
            return value as T
        }

        val valueStr = value.toString()

        val notCastedResult: Any? = when (property.returnType.withNullability(false)) {
            String::class,
            String::class.starProjectedType -> valueStr

            Boolean::class,
            Boolean::class.starProjectedType -> valueStr.toBooleanStrictOrNull()

            Byte::class,
            Byte::class.starProjectedType -> valueStr.toByteOrNull()

            UByte::class,
            UByte::class.starProjectedType -> valueStr.toUByteOrNull()

            Char::class,
            Char::class.starProjectedType ->
                if (valueStr.length != 1) {
                    error("Can not cast ${value::class} to ${Char::class} class. Please change property value with name '$propertyName'")
                } else {
                    valueStr[0]
                }

            Short::class,
            Short::class.starProjectedType -> valueStr.toShortOrNull()

            UShort::class,
            UShort::class.starProjectedType -> valueStr.toUShortOrNull()

            Int::class,
            Int::class.starProjectedType -> valueStr.toIntOrNull()

            UInt::class,
            UInt::class.starProjectedType -> valueStr.toUIntOrNull()

            Long::class,
            Long::class.starProjectedType -> valueStr.toLongOrNull()

            ULong::class,
            ULong::class.starProjectedType -> valueStr.toULongOrNull()

            Float::class,
            Float::class.starProjectedType -> valueStr.toFloatOrNull()

            Double::class,
            Double::class.starProjectedType -> valueStr.toDoubleOrNull()

            BigInteger::class,
            BigInteger::class.starProjectedType -> BigInteger(valueStr)

            BigDecimal::class,
            BigDecimal::class.starProjectedType -> BigDecimal(valueStr)

            Path::class,
            Path::class.starProjectedType -> Path.of(valueStr)

            else -> {
                if (mapper == null) {
                    error("Can not cast '${value::class}' to '${property.returnType}'. Please change property value or add mapper for property with name '$propertyName'")
                }

                mapper.invoke(valueStr)
            }
        }

        return (notCastedResult as T?)
            ?: defaultValue?.invoke()
            ?: error("Can not get value for property with name $propertyName")
    }

    private fun transformPropertyName(propertyName: String, type: KType): String {
        val builder = StringBuilder()
        var hadWordBefore = false
        var hadUpperBefore = false

        if (!prefix.isNullOrEmpty()) {
            builder.append(prefix).append('.')
        }

        val preparedPropertyName: String =
            if (type == Boolean::class.starProjectedType || type == Boolean::class) {
                if (propertyName.startsWith("is", ignoreCase = true)) {
                    propertyName.substring(2)
                } else if (propertyName.startsWith("has", ignoreCase = true)) {
                    propertyName.substring(3)
                } else propertyName
            } else {
                propertyName
            }

        preparedPropertyName.forEach {
            when (it) {
                in 'a'..'z' -> {
                    builder.append(it)
                    hadWordBefore = true
                    hadUpperBefore = false
                }

                in 'A'..'Z' -> {
                    if (hadWordBefore && hadUpperBefore) {
                        builder.setRange(builder.length - 1, builder.length, builder.last().lowercase())
                        builder.append(it)
                    } else if (hadWordBefore) {
                        builder.append('.').append(it.lowercase())
                    } else {
                        builder.append(it.lowercase())
                    }
                    hadWordBefore = true
                    hadUpperBefore = true
                }

                else -> {
                    builder.append(it)
                    hadWordBefore = true
                    hadUpperBefore = false
                }
            }
        }

        return builder.toString()
    }
}
