package chkan.ua.shoppinglist.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.domain.usecases.auth.SignInAnonymouslyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase
) : ViewModel() {

    fun signInAnonymouslyIfNeed(){
        viewModelScope.launch {
            signInAnonymouslyUseCase()
        }
    }
}