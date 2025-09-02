package chkan.ua.shoppinglist.session

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.domain.objects.LastOpenedList
import chkan.ua.domain.usecases.auth.SignInAnonymouslyUseCase
import chkan.ua.shoppinglist.core.services.SharedPreferencesService
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.IS_FIRST_LAUNCH
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_ID_INT
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_IS_SHARED
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_TITLE_STR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val spService: SharedPreferencesService,
) : ViewModel() {

    init {
        viewModelScope.launch (Dispatchers.IO) {
            val splashMinShowTime = 200L

            val dataJob = launch {
                isFirstLaunch = spService.get(IS_FIRST_LAUNCH, Boolean::class.java) ?: true
                if (isFirstLaunch) {
                    spService.set(IS_FIRST_LAUNCH, false)
                }
            }

            delay(splashMinShowTime)
            dataJob.join()
            _isLoadReady.value = true
        }
    }

    var isFirstLaunch = false

    private val _isLoadReady = mutableStateOf(false)
    val isLoadReady: State<Boolean> = _isLoadReady

    fun signInAnonymouslyIfNeed(){
        viewModelScope.launch {
            signInAnonymouslyUseCase()
        }
    }

    fun clearLastOpenedList() {
        spService.set(LAST_OPEN_LIST_ID_INT, 0)
        spService.set(LAST_OPEN_LIST_TITLE_STR, "")
        spService.set(LAST_OPEN_LIST_IS_SHARED, false)
    }

    fun getLastOpenedList(): LastOpenedList? {
        return try {
            val id = spService.get(LAST_OPEN_LIST_ID_INT, String::class.java) ?: ""
            val title = spService.get(LAST_OPEN_LIST_TITLE_STR, String::class.java) ?: ""
            val isShared = spService.get(LAST_OPEN_LIST_IS_SHARED, Boolean::class.java) ?: false
            LastOpenedList(id,title,isShared)
        } catch (e: Exception) {
            null
        }
    }
}