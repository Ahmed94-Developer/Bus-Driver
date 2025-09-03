import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.AutocompletePredictionBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLngBounds
import java.util.*
import java.util.concurrent.TimeUnit

class PlaceArrayAdapter(
    context: Context,
    resource: Int,
    private val mBounds: LatLngBounds,
    private val mPlaceFilter: AutocompleteFilter?
) : ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete>(context, resource), Filterable {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mResultList: ArrayList<PlaceAutocomplete>? = null

    fun setGoogleApiClient(googleApiClient: GoogleApiClient?) {
        mGoogleApiClient = if (googleApiClient != null && googleApiClient.isConnected) {
            googleApiClient
        } else {
            null
        }
    }

    override fun getCount(): Int = mResultList?.size ?: 0

    override fun getItem(position: Int): PlaceAutocomplete? = mResultList?.get(position)

    private fun getPredictions(constraint: CharSequence): ArrayList<PlaceAutocomplete>? {
        if (mGoogleApiClient == null) {
            Log.e(TAG, "Google API client is not connected.")
            return null
        }

        Log.i(TAG, "Executing autocomplete query for: $constraint")

        val results: PendingResult<AutocompletePredictionBuffer> = Places.GeoDataApi
            .getAutocompletePredictions(mGoogleApiClient!!, constraint.toString(), mBounds, mPlaceFilter)

        // Wait for predictions, set timeout to 60 seconds
        val autocompletePredictions: AutocompletePredictionBuffer? = results.await(60, TimeUnit.SECONDS)

        val status: Status? = autocompletePredictions?.status

        if (status == null || !status.isSuccess) {
            Toast.makeText(context, "Error: $status", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error getting place predictions: $status")
            autocompletePredictions?.release()
            return null
        }

        Log.i(TAG, "Query completed. Received ${autocompletePredictions.count} predictions.")

        val iterator = autocompletePredictions.iterator()
        val resultList = ArrayList<PlaceAutocomplete>(autocompletePredictions.count)

        while (iterator.hasNext()) {
            val prediction = iterator.next()
            resultList.add(PlaceAutocomplete(prediction.placeId!!
                , prediction.placeId.toString()))
        }

        autocompletePredictions.release()

        return resultList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null) {
                    mResultList = getPredictions(constraint)
                    if (mResultList != null) {
                        results.values = mResultList
                        results.count = mResultList?.size ?: 0
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    data class PlaceAutocomplete(val placeId: CharSequence, val description: CharSequence) {
        override fun toString(): String = description.toString()
    }

    companion object {
        private const val TAG = "PlaceArrayAdapter"
    }
}
