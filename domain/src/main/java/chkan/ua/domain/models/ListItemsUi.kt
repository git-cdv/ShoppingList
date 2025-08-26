package chkan.ua.domain.models

data class ListItemsUi(
    val id: String,
    val title: String,
    val position: Int,
    val count: Int,
    val readyCount: Int,
    val progress: ListProgress,
    val items: List<Item>,
    val isShared: Boolean,
)

fun List<ListItems>.toUiModels() : List<ListItemsUi>{
    return this.map { ListItemsUi(
        id = it.id,
        title = it.title,
        position = it.position,
        count = it.items.size,
        readyCount = it.items.filter { it.isReady }.size,
        progress = ListProgress(it.items.size,it.items.filter { it.isReady }.size),
        items = it.items,
        isShared = it.isShared
    ) }
}

class ListProgress(count: Int, readyCount: Int){
    private val progress: Float

    init {
        progress = if (readyCount == 0){
            0f
        } else {
            (readyCount.toFloat() / count.toFloat())
        }
    }

    fun get() = progress
}
