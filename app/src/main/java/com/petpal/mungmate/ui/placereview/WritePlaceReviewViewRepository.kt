import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.model.Place
import com.petpal.mungmate.model.Review
import kotlinx.coroutines.tasks.await

class WritePlaceReviewRepository {
//    private val db = FirebaseFirestore.getInstance()
//
//    suspend fun addReview(place: Place, review: Review): Boolean {
//        val placesRef = db.collection("places")
//        val placeDocument = placesRef.document(place.id)
//        val document = placeDocument.get().await()
//
//        if (!document.exists()) {
//            placeDocument.set(place).await()
//        }
//
//        return try {
//            addPlaceReview(place.id, review)
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    private suspend fun addPlaceReview(placeId: String, review: Review) {
//        val reviewRef = db.collection("places").document(placeId).collection("reviews")
//        reviewRef.add(review).await()
//    }
}