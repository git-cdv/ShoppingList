package chkan.ua.shoppinglist.navigation

import kotlinx.serialization.Serializable

@Serializable
object FirstListRoute

@Serializable
object ListsRoute

@Serializable
data class ItemsRoute(val listId: Int, val listTitle: String)