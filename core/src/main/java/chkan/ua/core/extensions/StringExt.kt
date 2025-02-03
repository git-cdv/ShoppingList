package chkan.ua.core.extensions

fun String.firstAsTitle() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }