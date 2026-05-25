package com.sidel.app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sidel.app.ui.theme.SideLTheme
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private enum class ImageUploadTarget {
    ProviderGig,
    Receipt,
}

class MainActivity : ComponentActivity() {
    private val providerImagePicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uploadSelectedImages(uris.take(5), ImageUploadTarget.ProviderGig)
    }
    private val receiptImagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) uploadSelectedImages(listOf(uri), ImageUploadTarget.Receipt)
    }

    private var authScreen by mutableStateOf(AuthScreen.Landing)
    private var email by mutableStateOf("")
    private var password by mutableStateOf("")
    private var signupFirstname by mutableStateOf("")
    private var signupLastname by mutableStateOf("")
    private var signupEmail by mutableStateOf("")
    private var signupPassword by mutableStateOf("")
    private var signupPhone by mutableStateOf("")
    private var signupAddress by mutableStateOf("")
    private var apiBaseUrl by mutableStateOf(DEFAULT_API_URL)
    private var isLoading by mutableStateOf(false)
    private var isGigsLoading by mutableStateOf(false)
    private var message by mutableStateOf("")
    private var loggedUser by mutableStateOf<LoggedUser?>(null)
    private var gigs by mutableStateOf<List<Gig>>(emptyList())
    private var selectedGig by mutableStateOf<Gig?>(null)
    private var selectedGigDetails by mutableStateOf<Gig?>(null)
    private var gigReviews by mutableStateOf<List<Review>>(emptyList())
    private var isReviewsLoading by mutableStateOf(false)
    private var bookingDate by mutableStateOf("")
    private var bookingPhone by mutableStateOf("")
    private var bookingAddress by mutableStateOf("")
    private var bookingNotes by mutableStateOf("")
    private var isBooking by mutableStateOf(false)
    private var clientBookings by mutableStateOf<List<Booking>>(emptyList())
    private var providerBookings by mutableStateOf<List<Booking>>(emptyList())
    private var providerGigs by mutableStateOf<List<Gig>>(emptyList())
    private var userReports by mutableStateOf<List<UserReport>>(emptyList())
    private var isBookingsLoading by mutableStateOf(false)
    private var isProviderGigsLoading by mutableStateOf(false)
    private var isReportsLoading by mutableStateOf(false)
    private var selectedBookingId by mutableStateOf<Int?>(null)
    private var receiptUrl by mutableStateOf("")
    private var reviewRating by mutableStateOf("")
    private var reviewComment by mutableStateOf("")
    private var reportTargetUserId by mutableStateOf<Int?>(null)
    private var reportBookingId by mutableStateOf<Int?>(null)
    private var reportReason by mutableStateOf("")
    private var reportDetails by mutableStateOf("")
    private var editingGigId by mutableStateOf<Int?>(null)
    private var providerGigTitle by mutableStateOf("")
    private var providerGigDescription by mutableStateOf("")
    private var providerGigCategory by mutableStateOf("Electrical")
    private var providerGigPrice by mutableStateOf("")
    private var providerGigImageUrl by mutableStateOf("")
    private var isEditingProfile by mutableStateOf(false)
    private var profileFirstname by mutableStateOf("")
    private var profileLastname by mutableStateOf("")
    private var profileEmail by mutableStateOf("")
    private var profilePhone by mutableStateOf("")
    private var profileAddress by mutableStateOf("")
    private var profileBio by mutableStateOf("")
    private var selectedTab by mutableStateOf(AppTab.Browse)
    private var searchQuery by mutableStateOf("")
    private var selectedCategory by mutableStateOf("All")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        restoreSession()

        setContent {
            SideLTheme(darkTheme = false, dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SideLApp(
                        authScreen = authScreen,
                        email = email,
                        password = password,
                        signupFirstname = signupFirstname,
                        signupLastname = signupLastname,
                        signupEmail = signupEmail,
                        signupPassword = signupPassword,
                        signupPhone = signupPhone,
                        signupAddress = signupAddress,
                        apiBaseUrl = apiBaseUrl,
                        isLoading = isLoading,
                        message = message,
                        loggedUser = loggedUser,
                        gigs = gigs,
                        isGigsLoading = isGigsLoading,
                        selectedGig = selectedGig,
                        selectedGigDetails = selectedGigDetails,
                        gigReviews = gigReviews,
                        isReviewsLoading = isReviewsLoading,
                        bookingDate = bookingDate,
                        bookingPhone = bookingPhone,
                        bookingAddress = bookingAddress,
                        bookingNotes = bookingNotes,
                        isBooking = isBooking,
                        clientBookings = clientBookings,
                        providerBookings = providerBookings,
                        providerGigs = providerGigs,
                        userReports = userReports,
                        isBookingsLoading = isBookingsLoading,
                        isProviderGigsLoading = isProviderGigsLoading,
                        isReportsLoading = isReportsLoading,
                        selectedBookingId = selectedBookingId,
                        receiptUrl = receiptUrl,
                        reviewRating = reviewRating,
                        reviewComment = reviewComment,
                        reportTargetUserId = reportTargetUserId,
                        reportBookingId = reportBookingId,
                        reportReason = reportReason,
                        reportDetails = reportDetails,
                        editingGigId = editingGigId,
                        providerGigTitle = providerGigTitle,
                        providerGigDescription = providerGigDescription,
                        providerGigCategory = providerGigCategory,
                        providerGigPrice = providerGigPrice,
                        providerGigImageUrl = providerGigImageUrl,
                        isEditingProfile = isEditingProfile,
                        profileFirstname = profileFirstname,
                        profileLastname = profileLastname,
                        profileEmail = profileEmail,
                        profilePhone = profilePhone,
                        profileAddress = profileAddress,
                        profileBio = profileBio,
                        selectedTab = selectedTab,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onSignupFirstnameChange = { signupFirstname = it },
                        onSignupLastnameChange = { signupLastname = it },
                        onSignupEmailChange = { signupEmail = it },
                        onSignupPasswordChange = { signupPassword = it },
                        onSignupPhoneChange = { signupPhone = it },
                        onSignupAddressChange = { signupAddress = it },
                        onApiBaseUrlChange = { apiBaseUrl = it },
                        onBookingDateChange = { bookingDate = it },
                        onBookingPhoneChange = { bookingPhone = it },
                        onBookingAddressChange = { bookingAddress = it },
                        onBookingNotesChange = { bookingNotes = it },
                        onLogin = ::login,
                        onSignup = ::signup,
                        onLogout = ::logout,
                        onShowLanding = {
                            authScreen = AuthScreen.Landing
                            message = ""
                        },
                        onShowLogin = {
                            authScreen = AuthScreen.Login
                            message = ""
                        },
                        onShowSignup = {
                            authScreen = AuthScreen.Signup
                            message = ""
                        },
                        onRefreshGigs = ::loadGigs,
                        onSelectGig = ::openBooking,
                        onToggleGigLike = ::toggleGigLike,
                        onOpenGigDetails = ::openGigDetails,
                        onCloseGigDetails = ::closeGigDetails,
                        onCancelBooking = ::closeBooking,
                        onSubmitBooking = ::submitBooking,
                        onRefreshBookings = ::loadBookings,
                        onUpdateBookingStatus = ::updateBookingStatus,
                        onSelectBooking = ::selectBooking,
                        onReceiptUrlChange = { receiptUrl = it },
                        onReviewRatingChange = { reviewRating = it },
                        onReviewCommentChange = { reviewComment = it },
                        onSubmitReceipt = ::submitReceipt,
                        onPickReceiptImage = ::pickReceiptImage,
                        onSubmitReview = ::submitReview,
                        onOpenReport = ::openReport,
                        onCloseReport = ::closeReport,
                        onReportReasonChange = { reportReason = it },
                        onReportDetailsChange = { reportDetails = it },
                        onSubmitReport = ::submitReport,
                        onRefreshProviderGigs = ::loadProviderGigs,
                        onEditProviderGig = ::startEditProviderGig,
                        onClearProviderGigForm = ::clearProviderGigForm,
                        onProviderGigTitleChange = { providerGigTitle = it },
                        onProviderGigDescriptionChange = { providerGigDescription = it },
                        onProviderGigCategoryChange = { providerGigCategory = it },
                        onProviderGigPriceChange = { providerGigPrice = it },
                        onProviderGigImageUrlChange = { providerGigImageUrl = it },
                        onPickProviderImages = ::pickProviderImages,
                        onSaveProviderGig = ::saveProviderGig,
                        onDisableProviderGig = ::disableProviderGig,
                        onStartEditProfile = ::startEditProfile,
                        onCancelEditProfile = ::cancelEditProfile,
                        onProfileFirstnameChange = { profileFirstname = it },
                        onProfileLastnameChange = { profileLastname = it },
                        onProfileEmailChange = { profileEmail = it },
                        onProfilePhoneChange = { profilePhone = it },
                        onProfileAddressChange = { profileAddress = it },
                        onProfileBioChange = { profileBio = it },
                        onSaveProfile = ::saveProfile,
                        onRefreshReports = ::loadReports,
                        onSelectTab = { selectedTab = it },
                        onSearchQueryChange = { searchQuery = it },
                        onSelectedCategoryChange = { selectedCategory = it },
                    )
                }
            }
        }
    }

    private fun login() {
        if (email.isBlank() || password.isBlank()) {
            message = "Please enter your email and password."
            return
        }

        isLoading = true
        message = ""

        Thread {
            try {
                val response = postJson(
                    endpoint = "/users/login",
                    body = JSONObject()
                        .put("email", email.trim())
                        .put("password", password)
                        .toString()
                )

                val json = JSONObject(response)
                val token = json.getString("token")
                val userJson = json.getJSONObject("user")
                val user = userJson.toLoggedUser()

                saveSession(token, user)
                runOnUiThread {
                    loggedUser = user
                    message = "Login successful."
                }
                loadGigs(user)
                loadBookings(user)
                loadReports(user)
                if (user.isProvider) {
                    loadProviderGigs(user)
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = if (
                        err.message?.contains("User not found", ignoreCase = true) == true ||
                        err.message?.contains("Invalid password", ignoreCase = true) == true
                    ) {
                        "Invalid email or password."
                    } else {
                        err.message ?: "Login failed."
                    }
                }
            } finally {
                runOnUiThread {
                    isLoading = false
                }
            }
        }.start()
    }

    private fun signup() {
        if (
            signupFirstname.isBlank() ||
            signupLastname.isBlank() ||
            signupEmail.isBlank() ||
            signupPassword.isBlank() ||
            signupPhone.isBlank() ||
            signupAddress.isBlank()
        ) {
            message = "Please complete all signup fields."
            return
        }

        isLoading = true
        message = ""

        Thread {
            try {
                postJson(
                    endpoint = "/users/register",
                    body = JSONObject()
                        .put("firstname", signupFirstname.trim())
                        .put("lastname", signupLastname.trim())
                        .put("email", signupEmail.trim())
                        .put("password", signupPassword)
                        .put("phoneNumber", signupPhone.trim())
                        .put("address", signupAddress.trim())
                        .toString()
                )

                runOnUiThread {
                    email = signupEmail.trim()
                    password = ""
                    signupFirstname = ""
                    signupLastname = ""
                    signupEmail = ""
                    signupPassword = ""
                    signupPhone = ""
                    signupAddress = ""
                    authScreen = AuthScreen.Login
                    message = "Account created. Please log in."
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to create account."
                }
            } finally {
                runOnUiThread {
                    isLoading = false
                }
            }
        }.start()
    }

    private fun loadGigs(currentUser: LoggedUser? = loggedUser) {
        val user = currentUser ?: return

        isGigsLoading = true
        if (gigs.isEmpty()) {
            message = "Loading gigs..."
        }

        Thread {
            try {
                val response = getJson("/services?userId=${user.userId}")
                val loadedGigs = JSONArray(response).toGigList()
                runOnUiThread {
                    gigs = loadedGigs
                    message = if (loadedGigs.isEmpty()) "No gigs available yet." else "Loaded ${loadedGigs.size} gigs."
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to load gigs."
                }
            } finally {
                runOnUiThread {
                    isGigsLoading = false
                }
            }
        }.start()
    }

    private fun openBooking(gig: Gig) {
        selectedGig = gig
        selectedGigDetails = null
        bookingDate = ""
        bookingPhone = ""
        bookingAddress = ""
        bookingNotes = ""
        message = ""
    }

    private fun closeBooking() {
        selectedGig = null
        bookingDate = ""
        bookingPhone = ""
        bookingAddress = ""
        bookingNotes = ""
    }

    private fun openGigDetails(gig: Gig) {
        selectedGigDetails = gig
        selectedGig = null
        gigReviews = emptyList()
        isReviewsLoading = true
        message = ""

        Thread {
            try {
                val response = getJson("/reviews/gig/${gig.gigId}")
                val loadedReviews = JSONArray(response).toReviewList()
                runOnUiThread {
                    gigReviews = loadedReviews
                    if (loadedReviews.isEmpty()) {
                        message = "No reviews yet for ${gig.title}."
                    }
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to load reviews."
                }
            } finally {
                runOnUiThread {
                    isReviewsLoading = false
                }
            }
        }.start()
    }

    private fun closeGigDetails() {
        selectedGigDetails = null
        gigReviews = emptyList()
    }

    private fun toggleGigLike(gig: Gig) {
        val user = loggedUser ?: return
        val nextLiked = !gig.likedByCurrentUser

        Thread {
            try {
                putJson("/gigs/${gig.gigId}/like/${user.userId}?liked=$nextLiked")
                loadGigs(user)
                if (user.isProvider) {
                    loadProviderGigs(user)
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to update like."
                }
            }
        }.start()
    }

    private fun submitBooking() {
        val user = loggedUser ?: return
        val gig = selectedGig ?: return

        if (bookingDate.isBlank() || bookingPhone.isBlank() || bookingAddress.isBlank()) {
            message = "Please enter schedule, phone, and service address."
            return
        }

        isBooking = true
        message = ""

        Thread {
            try {
                postJson(
                    endpoint = "/bookings",
                    includeToken = true,
                    body = JSONObject()
                        .put("client", JSONObject().put("userID", user.userId))
                        .put("gig", JSONObject().put("gigID", gig.gigId))
                        .put("bookingDate", bookingDate.trim())
                        .put("contactName", "${user.firstname} ${user.lastname}".trim())
                        .put("contactEmail", user.email)
                        .put("contactPhone", bookingPhone.trim())
                        .put("serviceAddress", bookingAddress.trim())
                        .put("clientNotes", bookingNotes.trim())
                        .toString()
                )

                runOnUiThread {
                    closeBooking()
                    selectedTab = AppTab.Bookings
                    message = "Booking request sent for ${gig.title}."
                }
                loadBookings(user)
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to book this service."
                }
            } finally {
                runOnUiThread {
                    isBooking = false
                }
            }
        }.start()
    }

    private fun loadBookings(currentUser: LoggedUser? = loggedUser) {
        val user = currentUser ?: return

        isBookingsLoading = true

        Thread {
            try {
                val clientResponse = getJson("/bookings/client/${user.userId}")
                val loadedClientBookings = JSONArray(clientResponse).toBookingList()
                val loadedProviderBookings = if (user.isProvider) {
                    JSONArray(getJson("/bookings/provider/${user.userId}")).toBookingList()
                } else {
                    emptyList()
                }

                runOnUiThread {
                    clientBookings = loadedClientBookings
                    providerBookings = loadedProviderBookings
                    if (message.startsWith("Loading")) {
                        message = ""
                    }
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to load bookings."
                }
            } finally {
                runOnUiThread {
                    isBookingsLoading = false
                }
            }
        }.start()
    }

    private fun loadReports(currentUser: LoggedUser? = loggedUser) {
        val user = currentUser ?: return

        isReportsLoading = true

        Thread {
            try {
                val response = getJson("/reports/user/${user.userId}")
                val loadedReports = JSONArray(response).toReportList()
                runOnUiThread {
                    userReports = loadedReports
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to load reports."
                }
            } finally {
                runOnUiThread {
                    isReportsLoading = false
                }
            }
        }.start()
    }

    private fun loadProviderGigs(currentUser: LoggedUser? = loggedUser) {
        val user = currentUser ?: return
        if (!user.isProvider) return

        isProviderGigsLoading = true

        Thread {
            try {
                val response = getJson("/gigs/provider/${user.userId}")
                val loadedGigs = JSONArray(response).toGigList()
                runOnUiThread {
                    providerGigs = loadedGigs
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to load provider gigs."
                }
            } finally {
                runOnUiThread {
                    isProviderGigsLoading = false
                }
            }
        }.start()
    }

    private fun startEditProviderGig(gig: Gig) {
        editingGigId = gig.gigId
        providerGigTitle = gig.title
        providerGigDescription = gig.description
        providerGigCategory = gig.category.ifBlank { "Electrical" }
        providerGigPrice = if (gig.price == 0.0) "" else gig.price.toInt().toString()
        providerGigImageUrl = gig.imageUrls.joinToString("\n")
        message = ""
    }

    private fun clearProviderGigForm() {
        editingGigId = null
        providerGigTitle = ""
        providerGigDescription = ""
        providerGigCategory = "Electrical"
        providerGigPrice = ""
        providerGigImageUrl = ""
    }

    private fun saveProviderGig() {
        val user = loggedUser ?: return
        val price = providerGigPrice.toDoubleOrNull()
        val imageUrls = providerGigImageUrl
            .split("\n", ",", ";")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (providerGigTitle.isBlank() || providerGigDescription.isBlank() || providerGigCategory.isBlank() || price == null) {
            message = "Please complete title, description, category, and valid price."
            return
        }
        if (imageUrls.size > 5) {
            message = "Please keep gig images to 5 max."
            return
        }

        isProviderGigsLoading = true
        message = ""

        Thread {
            try {
                val body = JSONObject()
                    .put("title", providerGigTitle.trim())
                    .put("description", providerGigDescription.trim())
                    .put("category", providerGigCategory.trim())
                    .put("price", price)
                    .put("imageUrls", imageUrls.joinToString("\n"))
                    .toString()

                if (editingGigId == null) {
                    postJson("/gigs/create/${user.userId}", body, includeToken = true)
                } else {
                    putJson("/gigs/$editingGigId/provider/${user.userId}", body)
                }

                runOnUiThread {
                    message = if (editingGigId == null) "Gig created." else "Gig updated."
                    clearProviderGigForm()
                }
                loadProviderGigs(user)
                loadGigs(user)
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to save gig."
                }
            } finally {
                runOnUiThread {
                    isProviderGigsLoading = false
                }
            }
        }.start()
    }

    private fun disableProviderGig(gig: Gig) {
        val user = loggedUser ?: return

        isProviderGigsLoading = true
        message = ""

        Thread {
            try {
                putJson("/gigs/${gig.gigId}/provider/${user.userId}/status?status=DISABLED")
                runOnUiThread {
                    message = "${gig.title} disabled."
                }
                loadProviderGigs(user)
                loadGigs(user)
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to disable gig."
                }
            } finally {
                runOnUiThread {
                    isProviderGigsLoading = false
                }
            }
        }.start()
    }

    private fun updateBookingStatus(booking: Booking, nextStatus: String) {
        isBookingsLoading = true
        message = ""

        Thread {
            try {
                putJson("/bookings/${booking.bookingId}/status?status=$nextStatus")
                runOnUiThread {
                    message = "Booking marked ${nextStatus.toStatusLabel()}."
                }
                loadBookings()
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to update booking."
                }
            } finally {
                runOnUiThread {
                    isBookingsLoading = false
                }
            }
        }.start()
    }

    private fun selectBooking(booking: Booking) {
        selectedBookingId = if (selectedBookingId == booking.bookingId) null else booking.bookingId
        receiptUrl = booking.receiptUrl
        reviewRating = ""
        reviewComment = ""
        message = ""
    }

    private fun submitReceipt(booking: Booking) {
        if (receiptUrl.isBlank()) {
            message = "Please upload or enter a receipt image first."
            return
        }

        isBookingsLoading = true
        message = ""

        Thread {
            try {
                putJson(
                    endpoint = "/bookings/${booking.bookingId}/receipt",
                    body = JSONObject().put("receiptUrl", receiptUrl.trim()).toString(),
                )
                runOnUiThread {
                    message = "Receipt submitted. Booking marked completed."
                    receiptUrl = ""
                    selectedBookingId = null
                }
                loadBookings()
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to submit receipt."
                }
            } finally {
                runOnUiThread {
                    isBookingsLoading = false
                }
            }
        }.start()
    }

    private fun submitReview(booking: Booking) {
        val user = loggedUser ?: return
        val rating = reviewRating.toIntOrNull()

        if (rating == null || rating !in 1..5) {
            message = "Rating must be from 1 to 5."
            return
        }

        isBookingsLoading = true
        message = ""

        Thread {
            try {
                postJson(
                    endpoint = "/reviews/gig/${booking.gigId}/client/${user.userId}",
                    includeToken = true,
                    body = JSONObject()
                        .put("rating", rating)
                        .put("comment", reviewComment.trim())
                        .toString()
                )
                runOnUiThread {
                    message = "Review submitted for ${booking.gigTitle}."
                    reviewRating = ""
                    reviewComment = ""
                    selectedBookingId = null
                }
                loadBookings()
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to submit review."
                }
            } finally {
                runOnUiThread {
                    isBookingsLoading = false
                }
            }
        }.start()
    }

    private fun openReport(targetUserId: Int, bookingId: Int?) {
        reportTargetUserId = targetUserId
        reportBookingId = bookingId
        reportReason = ""
        reportDetails = ""
        message = ""
    }

    private fun pickProviderImages() {
        providerImagePicker.launch("image/*")
    }

    private fun pickReceiptImage() {
        receiptImagePicker.launch("image/*")
    }

    private fun uploadSelectedImages(uris: List<Uri>, target: ImageUploadTarget) {
        if (uris.isEmpty()) return
        if (uris.size > 5) {
            message = "Please choose up to 5 images only."
            return
        }

        isLoading = true
        message = "Uploading image${if (uris.size > 1) "s" else ""}..."

        Thread {
            try {
                val uploadedPaths = uploadImages(uris)
                runOnUiThread {
                    if (target == ImageUploadTarget.ProviderGig) {
                        providerGigImageUrl = uploadedPaths.joinToString("\n")
                        message = "Uploaded ${uploadedPaths.size} gig image${if (uploadedPaths.size > 1) "s" else ""}."
                    } else {
                        receiptUrl = uploadedPaths.firstOrNull().orEmpty()
                        message = "Receipt image uploaded."
                    }
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to upload image."
                }
            } finally {
                runOnUiThread {
                    isLoading = false
                }
            }
        }.start()
    }

    private fun closeReport() {
        reportTargetUserId = null
        reportBookingId = null
        reportReason = ""
        reportDetails = ""
    }

    private fun submitReport() {
        val user = loggedUser ?: return
        val targetUserId = reportTargetUserId

        if (targetUserId == null) {
            message = "Choose who to report first."
            return
        }
        if (reportReason.isBlank() || reportDetails.isBlank()) {
            message = "Please enter report reason and details."
            return
        }

        isLoading = true
        message = ""

        Thread {
            try {
                val body = JSONObject()
                    .put("reporterId", user.userId)
                    .put("reportedUserId", targetUserId)
                    .put("reason", reportReason.trim())
                    .put("details", reportDetails.trim())

                if (reportBookingId != null) {
                    body.put("bookingId", reportBookingId)
                }

                postJson("/reports", body.toString(), includeToken = true)
                runOnUiThread {
                    closeReport()
                    message = "Report submitted. Admin will review it."
                }
                loadReports(user)
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to submit report."
                }
            } finally {
                runOnUiThread {
                    isLoading = false
                }
            }
        }.start()
    }

    private fun startEditProfile() {
        val user = loggedUser ?: return
        isEditingProfile = true
        profileFirstname = user.firstname
        profileLastname = user.lastname
        profileEmail = user.email
        profilePhone = user.phoneNumber
        profileAddress = user.address
        profileBio = user.bio
        message = ""
    }

    private fun cancelEditProfile() {
        isEditingProfile = false
        profileFirstname = ""
        profileLastname = ""
        profileEmail = ""
        profilePhone = ""
        profileAddress = ""
        profileBio = ""
    }

    private fun saveProfile() {
        val user = loggedUser ?: return

        if (profileFirstname.isBlank() || profileLastname.isBlank() || profileEmail.isBlank()) {
            message = "First name, last name, and email are required."
            return
        }

        isLoading = true
        message = ""

        Thread {
            try {
                val response = putJson(
                    endpoint = "/users/${user.userId}",
                    body = JSONObject()
                        .put("firstname", profileFirstname.trim())
                        .put("lastname", profileLastname.trim())
                        .put("email", profileEmail.trim())
                        .put("phoneNumber", profilePhone.trim())
                        .put("address", profileAddress.trim())
                        .put("bio", profileBio.trim())
                        .toString()
                )
                val updatedUser = JSONObject(response).toLoggedUser()
                runOnUiThread {
                    loggedUser = updatedUser
                    saveSession(getSavedToken(), updatedUser)
                    cancelEditProfile()
                    message = "Profile updated."
                }
            } catch (err: Exception) {
                runOnUiThread {
                    message = err.message ?: "Unable to update profile."
                }
            } finally {
                runOnUiThread {
                    isLoading = false
                }
            }
        }.start()
    }

    private fun postJson(endpoint: String, body: String, includeToken: Boolean = false): String {
        val cleanBaseUrl = apiBaseUrl.trim().trimEnd('/')
        val connection = URL("$cleanBaseUrl$endpoint").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.setRequestProperty("Content-Type", "application/json")
        if (includeToken) {
            val token = getSavedToken()
            if (token.isBlank()) throw Exception("Missing saved login token. Please log in again.")
            connection.setRequestProperty("Authorization", "Bearer $token")
        }
        connection.doOutput = true

        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(body)
        }

        val statusCode = connection.responseCode
        val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
        val response = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
        connection.disconnect()

        if (statusCode !in 200..299) {
            throw Exception(parseErrorMessage(response, statusCode))
        }

        return response
    }

    private fun putJson(endpoint: String, body: String? = null): String {
        val token = getSavedToken()
        if (token.isBlank()) {
            throw Exception("Missing saved login token. Please log in again.")
        }

        val cleanBaseUrl = apiBaseUrl.trim().trimEnd('/')
        val connection = URL("$cleanBaseUrl$endpoint").openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.setRequestProperty("Authorization", "Bearer $token")
        if (body != null) {
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(body)
            }
        }

        val statusCode = connection.responseCode
        val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
        val response = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
        connection.disconnect()

        if (statusCode !in 200..299) {
            throw Exception(parseErrorMessage(response, statusCode))
        }

        return response
    }

    private fun getJson(endpoint: String): String {
        val token = getSavedToken()
        if (token.isBlank()) {
            throw Exception("Missing saved login token. Please log in again.")
        }

        val cleanBaseUrl = apiBaseUrl.trim().trimEnd('/')
        val connection = URL("$cleanBaseUrl$endpoint").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.setRequestProperty("Authorization", "Bearer $token")

        val statusCode = connection.responseCode
        val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
        val response = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
        connection.disconnect()

        if (statusCode !in 200..299) {
            throw Exception(parseErrorMessage(response, statusCode))
        }

        return response
    }

    private fun uploadImages(uris: List<Uri>): List<String> {
        val cleanBaseUrl = apiBaseUrl.trim().trimEnd('/')
        val boundary = "SideLBoundary${System.currentTimeMillis()}"
        val connection = URL("$cleanBaseUrl/uploads/images").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 30000
        connection.readTimeout = 30000
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        val token = getSavedToken()
        if (token.isNotBlank()) {
            connection.setRequestProperty("Authorization", "Bearer $token")
        }
        connection.doOutput = true

        connection.outputStream.use { output ->
            uris.forEachIndexed { index, uri ->
                val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                val extension = when {
                    mimeType.endsWith("png") -> "png"
                    mimeType.endsWith("webp") -> "webp"
                    else -> "jpg"
                }
                output.write("--$boundary\r\n".toByteArray())
                output.write("Content-Disposition: form-data; name=\"images\"; filename=\"sidel-$index.$extension\"\r\n".toByteArray())
                output.write("Content-Type: $mimeType\r\n\r\n".toByteArray())
                contentResolver.openInputStream(uri)?.use { input ->
                    input.copyTo(output)
                }
                output.write("\r\n".toByteArray())
            }
            output.write("--$boundary--\r\n".toByteArray())
        }

        val statusCode = connection.responseCode
        val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
        val response = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
        connection.disconnect()

        if (statusCode !in 200..299) {
            throw Exception(parseErrorMessage(response, statusCode))
        }

        val json = JSONArray(response)
        return List(json.length()) { index -> json.getString(index) }
    }

    private fun getSavedToken(): String {
        return getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            .getString("authToken", null)
            .orEmpty()
    }

    private fun parseErrorMessage(response: String, statusCode: Int): String {
        if (response.isBlank()) return "Request failed with status $statusCode."
        return try {
            JSONObject(response).optString("message", response)
        } catch (_: Exception) {
            response
        }
    }

    private fun JSONObject.toLoggedUser(): LoggedUser {
        return LoggedUser(
            userId = optInt("userID"),
            firstname = optString("firstname"),
            lastname = optString("lastname"),
            email = optString("email"),
            phoneNumber = optString("phoneNumber"),
            address = optString("address"),
            bio = optString("bio"),
            isProvider = optBoolean("isProvider"),
            isAdmin = optBoolean("isAdmin"),
            providerStatus = optString("providerStatus", "NONE"),
        )
    }

    private fun JSONArray.toGigList(): List<Gig> {
        val origin = apiBaseUrl.trim().trimEnd('/').removeSuffix("/api")

        return List(length()) { index ->
            val gigJson = getJSONObject(index)
            val providerJson = gigJson.optJSONObject("provider")
            val imageUrls = gigJson.imageUrls(origin)
            val providerName = listOfNotNull(
                providerJson?.optString("firstname")?.takeIf { it.isNotBlank() },
                providerJson?.optString("lastname")?.takeIf { it.isNotBlank() },
            ).joinToString(" ").ifBlank { "Provider" }

            Gig(
                gigId = gigJson.optInt("gigID"),
                title = gigJson.optString("title", "Service"),
                description = gigJson.optString("description", "No description provided."),
                category = gigJson.optString("category", "Service"),
                price = gigJson.optDouble("price", 0.0),
                likeCount = gigJson.optInt("likeCount", 0),
                likedByCurrentUser = gigJson.optBoolean("likedByCurrentUser", false),
                providerId = providerJson?.optInt("userID") ?: 0,
                providerName = providerName,
                imageUrl = imageUrls.firstOrNull().orEmpty(),
                imageUrls = imageUrls,
            )
        }
    }

    private fun JSONObject.imageUrls(origin: String): List<String> {
        val rawImages = optString("imageUrls").ifBlank { optString("image") }
        return rawImages
            .split("\n", ",", ";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(5)
            .mapNotNull { image ->
                when {
                    image.startsWith("/uploads/") -> "$origin$image"
                    image.startsWith("http://") || image.startsWith("https://") -> image
                    else -> null
                }
            }
    }

    private fun JSONArray.toBookingList(): List<Booking> {
        return List(length()) { index ->
            getJSONObject(index).toBooking()
        }.sortedByDescending { it.bookingId }
    }

    private fun JSONArray.toReportList(): List<UserReport> {
        return List(length()) { index ->
            val reportJson = getJSONObject(index)
            val reporterJson = reportJson.optJSONObject("reporter")
            val reportedJson = reportJson.optJSONObject("reportedUser")

            UserReport(
                reportId = reportJson.optInt("reportID"),
                reporterId = reporterJson?.optInt("userID") ?: 0,
                reportedUserId = reportedJson?.optInt("userID") ?: 0,
                reportedUserName = fullName(reportedJson, "Reported user"),
                reason = reportJson.optString("reason"),
                details = reportJson.optString("details"),
                status = reportJson.optString("status", "OPEN"),
                createdAt = reportJson.optString("createdAt"),
            )
        }.sortedByDescending { it.reportId }
    }

    private fun JSONArray.toReviewList(): List<Review> {
        return List(length()) { index ->
            val reviewJson = getJSONObject(index)
            val clientJson = reviewJson.optJSONObject("client")

            Review(
                reviewId = reviewJson.optInt("reviewID"),
                clientName = fullName(clientJson, "Client"),
                rating = reviewJson.optInt("rating", 0),
                comment = reviewJson.optString("comment"),
                createdAt = reviewJson.optString("createdAt"),
            )
        }
    }

    private fun JSONObject.toBooking(): Booking {
        val gigJson = optJSONObject("gig")
        val clientJson = optJSONObject("client")
        val providerJson = gigJson?.optJSONObject("provider")

        return Booking(
            bookingId = optInt("bookingID"),
            gigId = gigJson?.optInt("gigID") ?: 0,
            clientId = clientJson?.optInt("userID") ?: 0,
            providerId = providerJson?.optInt("userID") ?: 0,
            gigTitle = gigJson?.optString("title")?.takeIf { it.isNotBlank() } ?: "Service booking",
            clientName = fullName(clientJson, "Client"),
            providerName = fullName(providerJson, "Provider"),
            bookingDate = optString("bookingDate"),
            contactPhone = optString("contactPhone"),
            serviceAddress = optString("serviceAddress"),
            clientNotes = optString("clientNotes"),
            receiptUrl = optString("receiptUrl"),
            status = optString("status", "PENDING"),
        )
    }

    private fun fullName(json: JSONObject?, fallback: String): String {
        return listOfNotNull(
            json?.optString("firstname")?.takeIf { it.isNotBlank() },
            json?.optString("lastname")?.takeIf { it.isNotBlank() },
        ).joinToString(" ").ifBlank { fallback }
    }

    private fun saveSession(token: String, user: LoggedUser) {
        getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString("authToken", token)
            .putString("apiBaseUrl", apiBaseUrl.trim())
            .putInt("userId", user.userId)
            .putString("firstname", user.firstname)
            .putString("lastname", user.lastname)
            .putString("email", user.email)
            .putString("phoneNumber", user.phoneNumber)
            .putString("address", user.address)
            .putString("bio", user.bio)
            .putBoolean("isProvider", user.isProvider)
            .putBoolean("isAdmin", user.isAdmin)
            .putString("providerStatus", user.providerStatus)
            .apply()
    }

    private fun restoreSession() {
        val prefs = getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
        apiBaseUrl = prefs.getString("apiBaseUrl", DEFAULT_API_URL) ?: DEFAULT_API_URL
        val token = prefs.getString("authToken", null)
        if (token.isNullOrBlank()) return

        loggedUser = LoggedUser(
            userId = prefs.getInt("userId", 0),
            firstname = prefs.getString("firstname", "").orEmpty(),
            lastname = prefs.getString("lastname", "").orEmpty(),
            email = prefs.getString("email", "").orEmpty(),
            phoneNumber = prefs.getString("phoneNumber", "").orEmpty(),
            address = prefs.getString("address", "").orEmpty(),
            bio = prefs.getString("bio", "").orEmpty(),
            isProvider = prefs.getBoolean("isProvider", false),
            isAdmin = prefs.getBoolean("isAdmin", false),
            providerStatus = prefs.getString("providerStatus", "NONE").orEmpty(),
        )
        loadGigs(loggedUser)
        loadBookings(loggedUser)
        loadReports(loggedUser)
        loadProviderGigs(loggedUser)
    }

    private fun logout() {
        getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        loggedUser = null
        gigs = emptyList()
        clientBookings = emptyList()
        providerBookings = emptyList()
        providerGigs = emptyList()
        userReports = emptyList()
        selectedBookingId = null
        receiptUrl = ""
        reviewRating = ""
        reviewComment = ""
        closeReport()
        clearProviderGigForm()
        cancelEditProfile()
        selectedGig = null
        selectedGigDetails = null
        gigReviews = emptyList()
        selectedTab = AppTab.Browse
        searchQuery = ""
        selectedCategory = "All"
        password = ""
        message = "Logged out."
    }
}

