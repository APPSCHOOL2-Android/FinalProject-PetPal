import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.mungmate.model.Place
import com.petpal.mungmate.model.Review
import kotlinx.coroutines.launch

class WritePlaceReviewViewModel(private val repository: WritePlaceReviewRepository) : ViewModel() {
//
//    val isReviewAdded = MutableLiveData<Boolean>()
//
//    fun addReview(place: Place, review: Review) {
//        viewModelScope.launch {
//            val result = repository.addReview(place, review)
//            isReviewAdded.value = result
//        }
//    }
}