package com.azg.alf_002_qrscan.style

import androidx.compose.runtime.Composable

// ---------- CharSequence: ifNotEmpty ----------
@Composable
fun <T : CharSequence> T.contentIfNotEmpty(block: @Composable (T) -> Unit) {
    if (isNotEmpty()) block(this)
}
@Composable
fun <T : CharSequence, R> T.contentIfNotEmpty(transform: @Composable (T) -> R): R? =
    if (isNotEmpty()) transform(this) else null
@Composable
fun <T : CharSequence, R> T.contentIfNotEmpty(
    transform: @Composable (T) -> R,
    defaultValue: @Composable () -> R
): R =
    if (isNotEmpty()) transform(this) else defaultValue()
// ---------- CharSequence: ifEmpty ----------
@Composable
fun <T : CharSequence> T.contentIfEmpty(block: @Composable (T) -> Unit) {
    if (isEmpty()) block(this)
}
@Composable
fun <T : CharSequence, R> T.contentIfEmpty(transform: @Composable (T) -> R): R? =
    if (isEmpty()) transform(this) else null
@Composable
fun <T : CharSequence, R> T.contentIfEmpty(
    transform: @Composable (T) -> R,
    defaultValue: @Composable () -> R
): R =
    if (isEmpty()) transform(this) else defaultValue()
// ---------- Collection: ifNotEmpty ----------
@Composable
fun <T> Collection<T>.contentIfNotEmpty(block: @Composable (Collection<T>) -> Unit) {
    if (isNotEmpty()) block(this)
}
@Composable
fun <T, R> Collection<T>.contentIfNotEmpty(transform: @Composable (Collection<T>) -> R): R? =
    if (isNotEmpty()) transform(this) else null
@Composable
fun <T, R> Collection<T>.contentIfNotEmpty(
    transform: @Composable (Collection<T>) -> R,
    defaultValue: @Composable () -> R
): R =
    if (isNotEmpty()) transform(this) else defaultValue()
// ---------- Collection: ifEmpty ----------
@Composable
fun <T> Collection<T>.contentIfEmpty(block: @Composable (Collection<T>) -> Unit) {
    if (isEmpty()) block(this)
}
@Composable
fun <T, R> Collection<T>.contentIfEmpty(transform: @Composable (Collection<T>) -> R): R? =
    if (isEmpty()) transform(this) else null
@Composable
fun <T, R> Collection<T>.contentIfEmpty(
    transform: @Composable (Collection<T>) -> R,
    defaultValue: @Composable () -> R
): R =
    if (isEmpty()) transform(this) else defaultValue()
// ---------- Map: ifNotEmpty ----------
@Composable
fun <K, V> Map<K, V>.contentIfNotEmpty(block: @Composable (Map<K, V>) -> Unit) {
    if (isNotEmpty()) block(this)
}
@Composable
fun <K, V, R> Map<K, V>.contentIfNotEmpty(transform: @Composable (Map<K, V>) -> R): R? =
    if (isNotEmpty()) transform(this) else null
@Composable
fun <K, V, R> Map<K, V>.contentIfNotEmpty(
    transform: @Composable (Map<K, V>) -> R,
    defaultValue: @Composable () -> R
): R =
    if (isNotEmpty()) transform(this) else defaultValue()
// ---------- Map: ifEmpty ----------
@Composable
fun <K, V> Map<K, V>.contentIfEmpty(block: @Composable (Map<K, V>) -> Unit) {
    if (isEmpty()) block(this)
}
@Composable
fun <K, V, R> Map<K, V>.contentIfEmpty(transform: @Composable (Map<K, V>) -> R): R? =
    if (isEmpty()) transform(this) else null
@Composable
fun <K, V, R> Map<K, V>.contentIfEmpty(
    transform: @Composable (Map<K, V>) -> R,
    defaultValue: @Composable () -> R
): R =
    if (isEmpty()) transform(this) else defaultValue()
// ---------- Boolean: contentIfTrue / contentIfFalse ----------

@Composable
fun Boolean.contentIfTrue(block: @Composable () -> Unit) {
    if (this) block()
}

@Composable
fun <R> Boolean.contentIfTrue(transform: @Composable () -> R): R? =
    if (this) transform() else null

@Composable
fun <R> Boolean.contentIfTrue(
    transform: @Composable () -> R,
    defaultValue: @Composable () -> R
): R = if (this) transform() else defaultValue()

@Composable
fun Boolean.contentIfFalse(block: @Composable () -> Unit) {
    if (!this) block()
}

@Composable
fun <R> Boolean.contentIfFalse(transform: @Composable () -> R): R? =
    if (!this) transform() else null

@Composable
fun <R> Boolean.contentIfFalse(
    transform: @Composable () -> R,
    defaultValue: @Composable () -> R
): R = if (!this) transform() else defaultValue()
// ---------- Boolean?: contentIfTrue / contentIfFalse (null treated as false) ----------

@Composable
fun Boolean?.contentIfTrue(block: @Composable () -> Unit) {
    if (this == true) block()
}

@Composable
fun <R> Boolean?.contentIfTrue(transform: @Composable () -> R): R? =
    if (this == true) transform() else null

@Composable
fun <R> Boolean?.contentIfTrue(
    transform: @Composable () -> R,
    defaultValue: @Composable () -> R
): R = if (this == true) transform() else defaultValue()

@Composable
fun Boolean?.contentIfFalse(block: @Composable () -> Unit) {
    if (this != true) block()
}

@Composable
fun <R> Boolean?.contentIfFalse(transform: @Composable () -> R): R? =
    if (this != true) transform() else null

@Composable
fun <R> Boolean?.contentIfFalse(
    transform: @Composable () -> R,
    defaultValue: @Composable () -> R
): R = if (this != true) transform() else defaultValue()
// ---------- T with predicate: contentIfTrue / contentIfFalse ----------

@Composable
fun <T> T.contentIfTrue(
    predicate: (T) -> Boolean,
    block: @Composable (T) -> Unit
) {
    if (predicate(this)) block(this)
}

@Composable
fun <T, R> T.contentIfTrue(
    predicate: (T) -> Boolean,
    transform: @Composable (T) -> R
): R? = if (predicate(this)) transform(this) else null

@Composable
fun <T, R> T.contentIfTrue(
    predicate: (T) -> Boolean,
    transform: @Composable (T) -> R,
    defaultValue: @Composable () -> R
): R = if (predicate(this)) transform(this) else defaultValue()

@Composable
fun <T> T.contentIfFalse(
    predicate: (T) -> Boolean,
    block: @Composable (T) -> Unit
) {
    if (!predicate(this)) block(this)
}

@Composable
fun <T, R> T.contentIfFalse(
    predicate: (T) -> Boolean,
    transform: @Composable (T) -> R
): R? = if (!predicate(this)) transform(this) else null

@Composable
fun <T, R> T.contentIfFalse(
    predicate: (T) -> Boolean,
    transform: @Composable (T) -> R,
    defaultValue: @Composable () -> R
): R = if (!predicate(this)) transform(this) else defaultValue()

// ---------- Nullable: contentIfNotNull ----------

@Composable
fun <T> T?.contentIfNotNull(block: @Composable (T) -> Unit) {
    this?.let { block(it) }
}

@Composable
fun <T, R> T?.contentIfNotNull(transform: @Composable (T) -> R): R? =
    this?.let { transform(it) }

@Composable
fun <T, R> T?.contentIfNotNull(
    transform: @Composable (T) -> R,
    defaultValue: @Composable () -> R
): R = this?.let { transform(it) } ?: defaultValue()

// ---------- Nullable: contentIfNull ----------

@Composable
fun <T> T?.contentIfNull(block: @Composable () -> Unit) {
    if (this == null) block()
}

@Composable
fun <T, R> T?.contentIfNull(transform: @Composable () -> R): R? =
    if (this == null) transform() else null

@Composable
fun <T, R> T?.contentIfNull(
    transform: @Composable () -> R,
    defaultValue: @Composable () -> R
): R = if (this == null) transform() else defaultValue()



// 1. Basic ifNotEmpty for any CharSequence
fun <T : CharSequence> T.ifNotEmpty(action: (T) -> Unit): T {
    if (isNotEmpty()) action(this)
    return this
}
// 2. ifNotEmpty with return value
fun <T : CharSequence, R> T.ifNotEmpty(transform: (T) -> R): R? {
    return if (isNotEmpty()) transform(this) else null
}
// 3. ifNotEmpty with default value
fun <T : CharSequence, R> T.ifNotEmpty(
    transform: (T) -> R,
    defaultValue: () -> R
): R {
    return if (isNotEmpty()) transform(this) else defaultValue()
}
// 4. ifNotEmpty for Collections
fun <T, R> Collection<T>.ifNotEmpty(transform: (Collection<T>) -> R): R? {
    return if (isNotEmpty()) transform(this) else null
}
// 5. ifNotEmpty for Collections with default
fun <T, R> Collection<T>.ifNotEmpty(
    transform: (Collection<T>) -> R,
    defaultValue: () -> R
): R {
    return if (isNotEmpty()) transform(this) else defaultValue()
}
// 6. ifNotEmpty for Maps
fun <K, V, R> Map<K, V>.ifNotEmpty(transform: (Map<K, V>) -> R): R? {
    return if (isNotEmpty()) transform(this) else null
}

// ---------- Nullable: ifNotNull ----------


fun <T, R> T?.ifNotNull(transform: (T) -> R): R? =
    this?.let(transform)

fun <T, R> T?.ifNotNull(
    transform: (T) -> R,
    defaultValue: () -> R
): R = this?.let(transform) ?: defaultValue()

// ---------- Nullable: ifNull ----------

fun <T, R> T?.ifNull(transform: () -> R): R? =
    if (this == null) transform() else null

fun <T, R> T?.ifNull(
    transform: () -> R,
    defaultValue: () -> R
): R = if (this == null) transform() else defaultValue()
// ---------- Boolean: ifTrue ----------
fun Boolean.ifTrue(action: () -> Unit): Boolean {
    if (this) action()
    return this
}

fun <R> Boolean.ifTrue(transform: () -> R): R? =
    if (this) transform() else null

fun <R> Boolean.ifTrue(
    transform: () -> R,
    defaultValue: () -> R
): R = if (this) transform() else defaultValue()

// ---------- Boolean: ifFalse ----------
fun Boolean.ifFalse(action: () -> Unit): Boolean {
    if (!this) action()
    return this
}

fun <R> Boolean.ifFalse(transform: () -> R): R? =
    if (!this) transform() else null

fun <R> Boolean.ifFalse(
    transform: () -> R,
    defaultValue: () -> R
): R = if (!this) transform() else defaultValue()

// Apply a condition to a value and act if it passes/fails
fun <T> T.ifTrue(predicate: (T) -> Boolean, action: (T) -> Unit): T {
    if (predicate(this)) action(this)
    return this
}

fun <T> T.ifFalse(predicate: (T) -> Boolean, action: (T) -> Unit): T {
    if (!predicate(this)) action(this)
    return this
}

