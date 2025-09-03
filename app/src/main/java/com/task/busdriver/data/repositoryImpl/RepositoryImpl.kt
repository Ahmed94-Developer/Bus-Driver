package com.task.busdriver.data.repositoryImpl
import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.task.busdriver.data.local.dao.UserDao
import com.task.busdriver.domain.entities.LoginParams
import com.task.busdriver.domain.entities.TripEntity
import com.task.busdriver.domain.entities.TripPointEntity
import com.task.busdriver.view.states.Result
import com.task.busdriver.view.states.Result.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import java.security.MessageDigest
import javax.inject.Inject

class RepositoryImpl @Inject constructor(val userDao: UserDao) {

    private val firebaseAuth = FirebaseAuth.getInstance()


    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser) // Emits the FirebaseUser or null
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }.distinctUntilChanged()

    fun saveTripPoints(predefinedRoute: ArrayList<TripPointEntity>,trip: TripEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertFullTrip(trip, predefinedRoute)
        }
    }



     @OptIn(DelicateCoroutinesApi::class)
     fun cacheUserLocally(username: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val hashedPassword = hashPassword(password)
            userDao.insertUser(LoginParams(username, hashedPassword))
        }

    }



    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
    suspend fun signInWithEmail(email: String, password: String, context: Context): Result<LoginParams> {
        return try {
            if (!isNetworkAvailable(context)) {
                // Offline login
                val user = userDao.getUser(email)
                return if (user != null && user.passwordHash == hashPassword(password)) {
                    Result.Success(user)
                } else {
                    Result.Failure(Exception("Invalid offline credentials"))
                }
            }

            // Online login via Firebase
            val result = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = result.user ?: throw Exception("Firebase user not found")

            // Cache user for offline login
            cacheUserLocally(email, password)

            val user = LoginParams(username = email, passwordHash = hashPassword(password))
            Result.Success(user)

        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    suspend fun registerUserWithEmail(
        email: String,
        password: String,
        context: Context
    ): Result<LoginParams> {
        return try {
            // Check network before attempting Firebase registration
            if (!isNetworkAvailable(context)) {
                return Result.Failure(Exception("No internet connection"))
            }

            // Firebase registration
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await() // Requires `kotlinx-coroutines-play-services`

            val firebaseUser = result.user ?: throw Exception("Firebase user is null")

            // Optionally cache the user locally for offline use
            cacheUserLocally(email, password)

            val user = LoginParams(username = email, passwordHash = hashPassword(password))
            Result.Success(user)

        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


}
