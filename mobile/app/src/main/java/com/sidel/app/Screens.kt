package com.sidel.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sidel.app.ui.theme.SideLTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

private val TextPrimary = Color(0xFF17202A)
private val TextSecondary = Color(0xFF334155)

@Composable
fun SideLApp(
    authScreen: AuthScreen,
    email: String,
    password: String,
    signupFirstname: String,
    signupLastname: String,
    signupEmail: String,
    signupPassword: String,
    signupPhone: String,
    signupAddress: String,
    apiBaseUrl: String,
    isLoading: Boolean,
    message: String,
    loggedUser: LoggedUser?,
    gigs: List<Gig>,
    isGigsLoading: Boolean,
    selectedGig: Gig?,
    selectedGigDetails: Gig?,
    gigReviews: List<Review>,
    isReviewsLoading: Boolean,
    bookingDate: String,
    bookingPhone: String,
    bookingAddress: String,
    bookingNotes: String,
    isBooking: Boolean,
    clientBookings: List<Booking>,
    providerBookings: List<Booking>,
    providerGigs: List<Gig>,
    userReports: List<UserReport>,
    isBookingsLoading: Boolean,
    isProviderGigsLoading: Boolean,
    isReportsLoading: Boolean,
    selectedBookingId: Int?,
    receiptUrl: String,
    reviewRating: String,
    reviewComment: String,
    reportTargetUserId: Int?,
    reportBookingId: Int?,
    reportReason: String,
    reportDetails: String,
    editingGigId: Int?,
    providerGigTitle: String,
    providerGigDescription: String,
    providerGigCategory: String,
    providerGigPrice: String,
    providerGigImageUrl: String,
    isEditingProfile: Boolean,
    profileFirstname: String,
    profileLastname: String,
    profileEmail: String,
    profilePhone: String,
    profileAddress: String,
    profileBio: String,
    selectedTab: AppTab,
    searchQuery: String,
    selectedCategory: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignupFirstnameChange: (String) -> Unit,
    onSignupLastnameChange: (String) -> Unit,
    onSignupEmailChange: (String) -> Unit,
    onSignupPasswordChange: (String) -> Unit,
    onSignupPhoneChange: (String) -> Unit,
    onSignupAddressChange: (String) -> Unit,
    onApiBaseUrlChange: (String) -> Unit,
    onBookingDateChange: (String) -> Unit,
    onBookingPhoneChange: (String) -> Unit,
    onBookingAddressChange: (String) -> Unit,
    onBookingNotesChange: (String) -> Unit,
    onLogin: () -> Unit,
    onSignup: () -> Unit,
    onLogout: () -> Unit,
    onShowLanding: () -> Unit,
    onShowLogin: () -> Unit,
    onShowSignup: () -> Unit,
    onRefreshGigs: () -> Unit,
    onSelectGig: (Gig) -> Unit,
    onToggleGigLike: (Gig) -> Unit,
    onOpenGigDetails: (Gig) -> Unit,
    onCloseGigDetails: () -> Unit,
    onCancelBooking: () -> Unit,
    onSubmitBooking: () -> Unit,
    onRefreshBookings: () -> Unit,
    onUpdateBookingStatus: (Booking, String) -> Unit,
    onSelectBooking: (Booking) -> Unit,
    onReceiptUrlChange: (String) -> Unit,
    onReviewRatingChange: (String) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReceipt: (Booking) -> Unit,
    onPickReceiptImage: () -> Unit,
    onSubmitReview: (Booking) -> Unit,
    onOpenReport: (Int, Int?) -> Unit,
    onCloseReport: () -> Unit,
    onReportReasonChange: (String) -> Unit,
    onReportDetailsChange: (String) -> Unit,
    onSubmitReport: () -> Unit,
    onRefreshProviderGigs: () -> Unit,
    onEditProviderGig: (Gig) -> Unit,
    onClearProviderGigForm: () -> Unit,
    onProviderGigTitleChange: (String) -> Unit,
    onProviderGigDescriptionChange: (String) -> Unit,
    onProviderGigCategoryChange: (String) -> Unit,
    onProviderGigPriceChange: (String) -> Unit,
    onProviderGigImageUrlChange: (String) -> Unit,
    onPickProviderImages: () -> Unit,
    onSaveProviderGig: () -> Unit,
    onDisableProviderGig: (Gig) -> Unit,
    onStartEditProfile: () -> Unit,
    onCancelEditProfile: () -> Unit,
    onProfileFirstnameChange: (String) -> Unit,
    onProfileLastnameChange: (String) -> Unit,
    onProfileEmailChange: (String) -> Unit,
    onProfilePhoneChange: (String) -> Unit,
    onProfileAddressChange: (String) -> Unit,
    onProfileBioChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onRefreshReports: () -> Unit,
    onSelectTab: (AppTab) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectedCategoryChange: (String) -> Unit,
) {
    val background = Color(0xFFF5F7FA)
    val green = Color(0xFF16715F)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loggedUser == null) {
            when (authScreen) {
                AuthScreen.Landing -> LandingCard(
                    green = green,
                    message = message,
                    onShowLogin = onShowLogin,
                    onShowSignup = onShowSignup,
                )
                AuthScreen.Login -> LoginCard(
                    email = email,
                    password = password,
                    apiBaseUrl = apiBaseUrl,
                    isLoading = isLoading,
                    message = message,
                    green = green,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onApiBaseUrlChange = onApiBaseUrlChange,
                    onLogin = onLogin,
                    onBack = onShowLanding,
                    onShowSignup = onShowSignup,
                )
                AuthScreen.Signup -> SignupCard(
                    firstname = signupFirstname,
                    lastname = signupLastname,
                    email = signupEmail,
                    password = signupPassword,
                    phone = signupPhone,
                    address = signupAddress,
                    apiBaseUrl = apiBaseUrl,
                    isLoading = isLoading,
                    message = message,
                    green = green,
                    onFirstnameChange = onSignupFirstnameChange,
                    onLastnameChange = onSignupLastnameChange,
                    onEmailChange = onSignupEmailChange,
                    onPasswordChange = onSignupPasswordChange,
                    onPhoneChange = onSignupPhoneChange,
                    onAddressChange = onSignupAddressChange,
                    onApiBaseUrlChange = onApiBaseUrlChange,
                    onSignup = onSignup,
                    onBack = onShowLanding,
                    onShowLogin = onShowLogin,
                )
            }
        } else {
            MobileHomeCard(
                user = loggedUser,
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
                message = message,
                green = green,
                onLogout = onLogout,
                onRefreshGigs = onRefreshGigs,
                onSelectGig = onSelectGig,
                onToggleGigLike = onToggleGigLike,
                onOpenGigDetails = onOpenGigDetails,
                onCloseGigDetails = onCloseGigDetails,
                onCancelBooking = onCancelBooking,
                onSubmitBooking = onSubmitBooking,
                onBookingDateChange = onBookingDateChange,
                onBookingPhoneChange = onBookingPhoneChange,
                onBookingAddressChange = onBookingAddressChange,
                onBookingNotesChange = onBookingNotesChange,
                onRefreshBookings = onRefreshBookings,
                onUpdateBookingStatus = onUpdateBookingStatus,
                onSelectBooking = onSelectBooking,
                onReceiptUrlChange = onReceiptUrlChange,
                onReviewRatingChange = onReviewRatingChange,
                onReviewCommentChange = onReviewCommentChange,
                onSubmitReceipt = onSubmitReceipt,
                onPickReceiptImage = onPickReceiptImage,
                onSubmitReview = onSubmitReview,
                onOpenReport = onOpenReport,
                onCloseReport = onCloseReport,
                onReportReasonChange = onReportReasonChange,
                onReportDetailsChange = onReportDetailsChange,
                onSubmitReport = onSubmitReport,
                onRefreshProviderGigs = onRefreshProviderGigs,
                onEditProviderGig = onEditProviderGig,
                onClearProviderGigForm = onClearProviderGigForm,
                onProviderGigTitleChange = onProviderGigTitleChange,
                onProviderGigDescriptionChange = onProviderGigDescriptionChange,
                onProviderGigCategoryChange = onProviderGigCategoryChange,
                onProviderGigPriceChange = onProviderGigPriceChange,
                onProviderGigImageUrlChange = onProviderGigImageUrlChange,
                onPickProviderImages = onPickProviderImages,
                onSaveProviderGig = onSaveProviderGig,
                onDisableProviderGig = onDisableProviderGig,
                onStartEditProfile = onStartEditProfile,
                onCancelEditProfile = onCancelEditProfile,
                onProfileFirstnameChange = onProfileFirstnameChange,
                onProfileLastnameChange = onProfileLastnameChange,
                onProfileEmailChange = onProfileEmailChange,
                onProfilePhoneChange = onProfilePhoneChange,
                onProfileAddressChange = onProfileAddressChange,
                onProfileBioChange = onProfileBioChange,
                onSaveProfile = onSaveProfile,
                onRefreshReports = onRefreshReports,
                onSelectTab = onSelectTab,
                onSearchQueryChange = onSearchQueryChange,
                onSelectedCategoryChange = onSelectedCategoryChange,
            )
        }
    }
}

@Composable
private fun LandingCard(
    green: Color,
    message: String,
    onShowLogin: () -> Unit,
    onShowSignup: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "sideL",
                color = green,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "Find trusted local services faster.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Browse nearby providers, book service requests, and track your jobs from one account.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onShowLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green),
            ) {
                Text("Login")
            }
            OutlinedButton(
                onClick = onShowSignup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text("Create account")
            }
            if (message.isNotBlank()) {
                Text(text = message, color = green)
            }
        }
    }
}

@Composable
private fun LoginCard(
    email: String,
    password: String,
    apiBaseUrl: String,
    isLoading: Boolean,
    message: String,
    green: Color,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onApiBaseUrlChange: (String) -> Unit,
    onLogin: () -> Unit,
    onBack: () -> Unit,
    onShowSignup: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "sideL",
                color = green,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Use the same account you registered on web. Mobile shares the same backend and database.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = onLogin,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Login")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Back")
                }
                OutlinedButton(onClick = onShowSignup, modifier = Modifier.weight(1f)) {
                    Text("Sign up")
                }
            }

            if (message.isNotBlank()) {
                Text(
                    text = message,
                    color = if (message.contains("successful", ignoreCase = true)) green else Color(0xFFB63F3F),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun SignupCard(
    firstname: String,
    lastname: String,
    email: String,
    password: String,
    phone: String,
    address: String,
    apiBaseUrl: String,
    isLoading: Boolean,
    message: String,
    green: Color,
    onFirstnameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onApiBaseUrlChange: (String) -> Unit,
    onSignup: () -> Unit,
    onBack: () -> Unit,
    onShowLogin: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Create account",
                color = green,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            OutlinedTextField(
                value = firstname,
                onValueChange = onFirstnameChange,
                label = { Text("First name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = lastname,
                onValueChange = onLastnameChange,
                label = { Text("Last name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Phone number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = onSignup,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Create account")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Back")
                }
                OutlinedButton(onClick = onShowLogin, modifier = Modifier.weight(1f)) {
                    Text("Login")
                }
            }
            if (message.isNotBlank()) {
                Text(
                    text = message,
                    color = if (message.contains("created", ignoreCase = true)) green else Color(0xFFB63F3F),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun MobileHomeCard(
    user: LoggedUser,
    gigs: List<Gig>,
    isGigsLoading: Boolean,
    selectedGig: Gig?,
    selectedGigDetails: Gig?,
    gigReviews: List<Review>,
    isReviewsLoading: Boolean,
    bookingDate: String,
    bookingPhone: String,
    bookingAddress: String,
    bookingNotes: String,
    isBooking: Boolean,
    clientBookings: List<Booking>,
    providerBookings: List<Booking>,
    providerGigs: List<Gig>,
    userReports: List<UserReport>,
    isBookingsLoading: Boolean,
    isProviderGigsLoading: Boolean,
    isReportsLoading: Boolean,
    selectedBookingId: Int?,
    receiptUrl: String,
    reviewRating: String,
    reviewComment: String,
    reportTargetUserId: Int?,
    reportBookingId: Int?,
    reportReason: String,
    reportDetails: String,
    editingGigId: Int?,
    providerGigTitle: String,
    providerGigDescription: String,
    providerGigCategory: String,
    providerGigPrice: String,
    providerGigImageUrl: String,
    isEditingProfile: Boolean,
    profileFirstname: String,
    profileLastname: String,
    profileEmail: String,
    profilePhone: String,
    profileAddress: String,
    profileBio: String,
    selectedTab: AppTab,
    searchQuery: String,
    selectedCategory: String,
    message: String,
    green: Color,
    onLogout: () -> Unit,
    onRefreshGigs: () -> Unit,
    onSelectGig: (Gig) -> Unit,
    onToggleGigLike: (Gig) -> Unit,
    onOpenGigDetails: (Gig) -> Unit,
    onCloseGigDetails: () -> Unit,
    onCancelBooking: () -> Unit,
    onSubmitBooking: () -> Unit,
    onBookingDateChange: (String) -> Unit,
    onBookingPhoneChange: (String) -> Unit,
    onBookingAddressChange: (String) -> Unit,
    onBookingNotesChange: (String) -> Unit,
    onRefreshBookings: () -> Unit,
    onUpdateBookingStatus: (Booking, String) -> Unit,
    onSelectBooking: (Booking) -> Unit,
    onReceiptUrlChange: (String) -> Unit,
    onReviewRatingChange: (String) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReceipt: (Booking) -> Unit,
    onPickReceiptImage: () -> Unit,
    onSubmitReview: (Booking) -> Unit,
    onOpenReport: (Int, Int?) -> Unit,
    onCloseReport: () -> Unit,
    onReportReasonChange: (String) -> Unit,
    onReportDetailsChange: (String) -> Unit,
    onSubmitReport: () -> Unit,
    onRefreshProviderGigs: () -> Unit,
    onEditProviderGig: (Gig) -> Unit,
    onClearProviderGigForm: () -> Unit,
    onProviderGigTitleChange: (String) -> Unit,
    onProviderGigDescriptionChange: (String) -> Unit,
    onProviderGigCategoryChange: (String) -> Unit,
    onProviderGigPriceChange: (String) -> Unit,
    onProviderGigImageUrlChange: (String) -> Unit,
    onPickProviderImages: () -> Unit,
    onSaveProviderGig: () -> Unit,
    onDisableProviderGig: (Gig) -> Unit,
    onStartEditProfile: () -> Unit,
    onCancelEditProfile: () -> Unit,
    onProfileFirstnameChange: (String) -> Unit,
    onProfileLastnameChange: (String) -> Unit,
    onProfileEmailChange: (String) -> Unit,
    onProfilePhoneChange: (String) -> Unit,
    onProfileAddressChange: (String) -> Unit,
    onProfileBioChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onRefreshReports: () -> Unit,
    onSelectTab: (AppTab) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectedCategoryChange: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AppHeader(user = user, green = green)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RoleChip(text = if (user.isProvider) "Provider" else "Client", green = green)
                RoleChip(text = user.providerStatus.ifBlank { "NONE" }, green = green)
            }

            AppTabs(
                selectedTab = selectedTab,
                isProvider = user.isProvider,
                green = green,
                onSelectTab = onSelectTab,
            )

            when (selectedTab) {
                AppTab.Browse -> BrowseTab(
                    gigs = gigs,
                    isGigsLoading = isGigsLoading,
                    selectedGig = selectedGig,
                    selectedGigDetails = selectedGigDetails,
                    gigReviews = gigReviews,
                    isReviewsLoading = isReviewsLoading,
                    reportTargetUserId = reportTargetUserId,
                    reportBookingId = reportBookingId,
                    reportReason = reportReason,
                    reportDetails = reportDetails,
                    searchQuery = searchQuery,
                    selectedCategory = selectedCategory,
                    bookingDate = bookingDate,
                    bookingPhone = bookingPhone,
                    bookingAddress = bookingAddress,
                    bookingNotes = bookingNotes,
                    isBooking = isBooking,
                    green = green,
                    onRefreshGigs = onRefreshGigs,
                    onSelectGig = onSelectGig,
                    onToggleGigLike = onToggleGigLike,
                    onOpenGigDetails = onOpenGigDetails,
                    onCloseGigDetails = onCloseGigDetails,
                    onOpenReport = onOpenReport,
                    onCloseReport = onCloseReport,
                    onReportReasonChange = onReportReasonChange,
                    onReportDetailsChange = onReportDetailsChange,
                    onSubmitReport = onSubmitReport,
                    onCancelBooking = onCancelBooking,
                    onSubmitBooking = onSubmitBooking,
                    onBookingDateChange = onBookingDateChange,
                    onBookingPhoneChange = onBookingPhoneChange,
                    onBookingAddressChange = onBookingAddressChange,
                    onBookingNotesChange = onBookingNotesChange,
                    onSearchQueryChange = onSearchQueryChange,
                    onSelectedCategoryChange = onSelectedCategoryChange,
                )
                AppTab.Bookings -> BookingSection(
                    title = "My bookings",
                    emptyText = "You have no booking requests yet.",
                    bookings = clientBookings,
                    isLoading = isBookingsLoading,
                    selectedBookingId = selectedBookingId,
                    receiptUrl = receiptUrl,
                    reviewRating = reviewRating,
                    reviewComment = reviewComment,
                    reportTargetUserId = reportTargetUserId,
                    reportBookingId = reportBookingId,
                    reportReason = reportReason,
                    reportDetails = reportDetails,
                    green = green,
                    mode = BookingMode.Client,
                    onRefresh = onRefreshBookings,
                    onUpdateStatus = onUpdateBookingStatus,
                    onSelectBooking = onSelectBooking,
                    onReceiptUrlChange = onReceiptUrlChange,
                    onReviewRatingChange = onReviewRatingChange,
                    onReviewCommentChange = onReviewCommentChange,
                    onSubmitReceipt = onSubmitReceipt,
                    onPickReceiptImage = onPickReceiptImage,
                    onSubmitReview = onSubmitReview,
                    onOpenReport = onOpenReport,
                    onCloseReport = onCloseReport,
                    onReportReasonChange = onReportReasonChange,
                    onReportDetailsChange = onReportDetailsChange,
                    onSubmitReport = onSubmitReport,
                )
                AppTab.Jobs -> if (user.isProvider) {
                    ProviderToolsTab(
                        providerGigs = providerGigs,
                        providerBookings = providerBookings,
                        isProviderGigsLoading = isProviderGigsLoading,
                        isBookingsLoading = isBookingsLoading,
                        selectedBookingId = selectedBookingId,
                        receiptUrl = receiptUrl,
                        reviewRating = reviewRating,
                        reviewComment = reviewComment,
                        reportTargetUserId = reportTargetUserId,
                        reportBookingId = reportBookingId,
                        reportReason = reportReason,
                        reportDetails = reportDetails,
                        editingGigId = editingGigId,
                        title = providerGigTitle,
                        description = providerGigDescription,
                        category = providerGigCategory,
                        price = providerGigPrice,
                        imageUrl = providerGigImageUrl,
                        green = green,
                        onRefreshProviderGigs = onRefreshProviderGigs,
                        onRefreshBookings = onRefreshBookings,
                        onEditProviderGig = onEditProviderGig,
                        onClearProviderGigForm = onClearProviderGigForm,
                        onTitleChange = onProviderGigTitleChange,
                        onDescriptionChange = onProviderGigDescriptionChange,
                        onCategoryChange = onProviderGigCategoryChange,
                        onPriceChange = onProviderGigPriceChange,
                        onImageUrlChange = onProviderGigImageUrlChange,
                        onPickProviderImages = onPickProviderImages,
                        onSaveProviderGig = onSaveProviderGig,
                        onDisableProviderGig = onDisableProviderGig,
                        onUpdateBookingStatus = onUpdateBookingStatus,
                        onSelectBooking = onSelectBooking,
                        onReceiptUrlChange = onReceiptUrlChange,
                        onReviewRatingChange = onReviewRatingChange,
                        onReviewCommentChange = onReviewCommentChange,
                        onSubmitReceipt = onSubmitReceipt,
                        onPickReceiptImage = onPickReceiptImage,
                        onSubmitReview = onSubmitReview,
                        onOpenReport = onOpenReport,
                        onCloseReport = onCloseReport,
                        onReportReasonChange = onReportReasonChange,
                        onReportDetailsChange = onReportDetailsChange,
                        onSubmitReport = onSubmitReport,
                    )
                } else {
                    EmptyPanel(
                        title = "Provider tools",
                        text = "Apply as a provider on web first. Once approved, your incoming jobs will appear here.",
                    )
                }
                AppTab.Profile -> ProfileTab(
                    user = user,
                    green = green,
                    isEditing = isEditingProfile,
                    firstname = profileFirstname,
                    lastname = profileLastname,
                    email = profileEmail,
                    phone = profilePhone,
                    address = profileAddress,
                    bio = profileBio,
                    onOpenBookings = { onSelectTab(AppTab.Bookings) },
                    onOpenProviderTools = { onSelectTab(AppTab.Jobs) },
                    onStartEdit = onStartEditProfile,
                    onCancelEdit = onCancelEditProfile,
                    onFirstnameChange = onProfileFirstnameChange,
                    onLastnameChange = onProfileLastnameChange,
                    onEmailChange = onProfileEmailChange,
                    onPhoneChange = onProfilePhoneChange,
                    onAddressChange = onProfileAddressChange,
                    onBioChange = onProfileBioChange,
                    onSaveProfile = onSaveProfile,
                    reports = userReports,
                    isReportsLoading = isReportsLoading,
                    onRefreshReports = onRefreshReports,
                    onLogout = onLogout,
                )
            }

            if (message.isNotBlank()) {
                Text(text = message, color = green)
            }
        }
    }
}

@Composable
private fun AppHeader(user: LoggedUser, green: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "sideL",
            color = green,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = "Welcome, ${user.firstname}",
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(text = user.email, color = TextSecondary)
    }
}

@Composable
private fun AppTabs(
    selectedTab: AppTab,
    isProvider: Boolean,
    green: Color,
    onSelectTab: (AppTab) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TabButton(
                text = "Browse",
                selected = selectedTab == AppTab.Browse,
                green = green,
                modifier = Modifier.weight(1f),
                onClick = { onSelectTab(AppTab.Browse) },
            )
            TabButton(
                text = "Bookings",
                selected = selectedTab == AppTab.Bookings,
                green = green,
                modifier = Modifier.weight(1f),
                onClick = { onSelectTab(AppTab.Bookings) },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TabButton(
                text = if (isProvider) "Jobs" else "Provider",
                selected = selectedTab == AppTab.Jobs,
                green = green,
                modifier = Modifier.weight(1f),
                onClick = { onSelectTab(AppTab.Jobs) },
            )
            TabButton(
                text = "Profile",
                selected = selectedTab == AppTab.Profile,
                green = green,
                modifier = Modifier.weight(1f),
                onClick = { onSelectTab(AppTab.Profile) },
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    green: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green, contentColor = Color.White),
        ) {
            Text(text, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(44.dp),
        ) {
            Text(text, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BrowseTab(
    gigs: List<Gig>,
    isGigsLoading: Boolean,
    selectedGig: Gig?,
    selectedGigDetails: Gig?,
    gigReviews: List<Review>,
    isReviewsLoading: Boolean,
    reportTargetUserId: Int?,
    reportBookingId: Int?,
    reportReason: String,
    reportDetails: String,
    searchQuery: String,
    selectedCategory: String,
    bookingDate: String,
    bookingPhone: String,
    bookingAddress: String,
    bookingNotes: String,
    isBooking: Boolean,
    green: Color,
    onRefreshGigs: () -> Unit,
    onSelectGig: (Gig) -> Unit,
    onToggleGigLike: (Gig) -> Unit,
    onOpenGigDetails: (Gig) -> Unit,
    onCloseGigDetails: () -> Unit,
    onOpenReport: (Int, Int?) -> Unit,
    onCloseReport: () -> Unit,
    onReportReasonChange: (String) -> Unit,
    onReportDetailsChange: (String) -> Unit,
    onSubmitReport: () -> Unit,
    onCancelBooking: () -> Unit,
    onSubmitBooking: () -> Unit,
    onBookingDateChange: (String) -> Unit,
    onBookingPhoneChange: (String) -> Unit,
    onBookingAddressChange: (String) -> Unit,
    onBookingNotesChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectedCategoryChange: (String) -> Unit,
) {
    val filteredGigs = gigs.filter { gig ->
        val query = searchQuery.trim()
        val matchesCategory = selectedCategory == "All" || gig.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = query.isBlank() ||
            gig.title.contains(query, ignoreCase = true) ||
            gig.description.contains(query, ignoreCase = true) ||
            gig.category.contains(query, ignoreCase = true) ||
            gig.providerName.contains(query, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (selectedGig != null) {
            BookingPanel(
                gig = selectedGig,
                bookingDate = bookingDate,
                bookingPhone = bookingPhone,
                bookingAddress = bookingAddress,
                bookingNotes = bookingNotes,
                isBooking = isBooking,
                green = green,
                onBookingDateChange = onBookingDateChange,
                onBookingPhoneChange = onBookingPhoneChange,
                onBookingAddressChange = onBookingAddressChange,
                onBookingNotesChange = onBookingNotesChange,
                onCancelBooking = onCancelBooking,
                onSubmitBooking = onSubmitBooking,
            )
        }

        if (selectedGigDetails != null) {
            GigDetailsPanel(
                gig = selectedGigDetails,
                reviews = gigReviews,
                isReviewsLoading = isReviewsLoading,
                green = green,
                onBook = { onSelectGig(selectedGigDetails) },
                onClose = onCloseGigDetails,
                onReportProvider = { onOpenReport(selectedGigDetails.providerId, null) },
                onToggleLike = { onToggleGigLike(selectedGigDetails) },
            )
        }

        if (reportTargetUserId != null && reportBookingId == null) {
            ReportPanel(
                reason = reportReason,
                details = reportDetails,
                green = green,
                onReasonChange = onReportReasonChange,
                onDetailsChange = onReportDetailsChange,
                onSubmitReport = onSubmitReport,
                onClose = onCloseReport,
            )
        }

        SearchAndCategoryFilter(
            searchQuery = searchQuery,
            selectedCategory = selectedCategory,
            green = green,
            onSearchQueryChange = onSearchQueryChange,
            onSelectedCategoryChange = onSelectedCategoryChange,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Available gigs",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            OutlinedButton(onClick = onRefreshGigs, enabled = !isGigsLoading) {
                Text(if (isGigsLoading) "Loading" else "Refresh")
            }
        }

        if (isGigsLoading && gigs.isEmpty()) {
            CircularProgressIndicator(color = green)
        }

        if (gigs.isEmpty() && !isGigsLoading) {
            Text(
                text = "No gigs found yet.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (gigs.isNotEmpty() && filteredGigs.isEmpty()) {
            Text(
                text = "No gigs match your search.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        filteredGigs.chunked(6).forEachIndexed { index, gigGroup ->
            GigCarousel(
                title = if (index == 0) "Recommended services" else "More services",
                gigs = gigGroup,
            green = green,
            onOpenDetails = onOpenGigDetails,
            onBook = onSelectGig,
            onToggleLike = onToggleGigLike,
        )
    }
}
}

@Composable
private fun GigCarousel(
    title: String,
    gigs: List<Gig>,
    green: Color,
    onOpenDetails: (Gig) -> Unit,
    onBook: (Gig) -> Unit,
    onToggleLike: (Gig) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            color = TextPrimary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            gigs.forEach { gig ->
                GigCard(
                    gig = gig,
                    green = green,
                    modifier = Modifier.width(282.dp),
                    onOpenDetails = { onOpenDetails(gig) },
                    onBook = { onBook(gig) },
                    onToggleLike = { onToggleLike(gig) },
                )
            }
        }
    }
}

@Composable
private fun SearchAndCategoryFilter(
    searchQuery: String,
    selectedCategory: String,
    green: Color,
    onSearchQueryChange: (String) -> Unit,
    onSelectedCategoryChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search service, keyword, or provider") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        CategoryRows(
            selectedCategory = selectedCategory,
            green = green,
            onSelectedCategoryChange = onSelectedCategoryChange,
        )
    }
}

@Composable
private fun CategoryRows(
    selectedCategory: String,
    green: Color,
    onSelectedCategoryChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        serviceCategories.chunked(8).forEach { rowCategories ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowCategories.forEach { category ->
                    CategoryButton(
                        category = category,
                        selected = selectedCategory == category,
                        green = green,
                        onClick = { onSelectedCategoryChange(category) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryButton(
    category: String,
    selected: Boolean,
    green: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green, contentColor = Color.White),
        ) {
            Text(category, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(40.dp),
        ) {
            Text(category, color = TextPrimary, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileTab(
    user: LoggedUser,
    green: Color,
    isEditing: Boolean,
    firstname: String,
    lastname: String,
    email: String,
    phone: String,
    address: String,
    bio: String,
    onOpenBookings: () -> Unit,
    onOpenProviderTools: () -> Unit,
    onStartEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onFirstnameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    reports: List<UserReport>,
    isReportsLoading: Boolean,
    onRefreshReports: () -> Unit,
    onLogout: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFB),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("My profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (isEditing) {
                OutlinedTextField(value = firstname, onValueChange = onFirstnameChange, label = { Text("First name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = lastname, onValueChange = onLastnameChange, label = { Text("Last name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Phone number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = bio, onValueChange = onBioChange, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSaveProfile,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = green),
                    ) {
                        Text("Save")
                    }
                    OutlinedButton(onClick = onCancelEdit, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                }
            } else {
                Text("${user.firstname} ${user.lastname}", fontWeight = FontWeight.Bold)
                Text(user.email, color = TextSecondary)
                if (user.phoneNumber.isNotBlank()) Text(user.phoneNumber, color = TextSecondary)
                if (user.address.isNotBlank()) Text(user.address, color = TextSecondary)
                if (user.bio.isNotBlank()) Text(user.bio, color = TextSecondary)
                RoleChip(text = if (user.isProvider) "Approved Provider" else "Client", green = green)
                RoleChip(text = "Provider status: ${user.providerStatus.ifBlank { "NONE" }}", green = green)
                Button(
                    onClick = onStartEdit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                ) {
                    Text("Edit profile")
                }
                Button(
                    onClick = onOpenBookings,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                ) {
                    Text("My bookings")
                }
                OutlinedButton(onClick = onOpenProviderTools, modifier = Modifier.fillMaxWidth()) {
                    Text(if (user.isProvider) "Provider jobs" else "Apply as provider")
                }
                if (!user.isProvider) {
                    Text(
                        text = "Provider application is best completed on web for now so admin testing stays simple.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                ReportHistoryPanel(
                    reports = reports,
                    isLoading = isReportsLoading,
                    currentUserId = user.userId,
                    green = green,
                    onRefresh = onRefreshReports,
                )
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
private fun ReportHistoryPanel(
    reports: List<UserReport>,
    isLoading: Boolean,
    currentUserId: Int,
    green: Color,
    onRefresh: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Reports", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = onRefresh, enabled = !isLoading) {
                    Text(if (isLoading) "Loading" else "Refresh")
                }
            }
            if (reports.isEmpty() && !isLoading) {
                Text("No reports yet.", color = TextSecondary)
            }
            reports.forEach { report ->
                val direction = if (report.reporterId == currentUserId) "You reported" else "Reported about you"
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFB),
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(direction, color = TextPrimary, fontWeight = FontWeight.Bold)
                            RoleChip(text = report.status.toStatusLabel(), green = green)
                        }
                        Text(report.reportedUserName, color = TextPrimary)
                        Text(report.reason, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (report.createdAt.isNotBlank()) {
                            Text(report.createdAt, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPanel(title: String, text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFB),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text, color = TextSecondary)
        }
    }
}

@Composable
private fun ProviderToolsTab(
    providerGigs: List<Gig>,
    providerBookings: List<Booking>,
    isProviderGigsLoading: Boolean,
    isBookingsLoading: Boolean,
    selectedBookingId: Int?,
    receiptUrl: String,
    reviewRating: String,
    reviewComment: String,
    reportTargetUserId: Int?,
    reportBookingId: Int?,
    reportReason: String,
    reportDetails: String,
    editingGigId: Int?,
    title: String,
    description: String,
    category: String,
    price: String,
    imageUrl: String,
    green: Color,
    onRefreshProviderGigs: () -> Unit,
    onRefreshBookings: () -> Unit,
    onEditProviderGig: (Gig) -> Unit,
    onClearProviderGigForm: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onPickProviderImages: () -> Unit,
    onSaveProviderGig: () -> Unit,
    onDisableProviderGig: (Gig) -> Unit,
    onUpdateBookingStatus: (Booking, String) -> Unit,
    onSelectBooking: (Booking) -> Unit,
    onReceiptUrlChange: (String) -> Unit,
    onReviewRatingChange: (String) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReceipt: (Booking) -> Unit,
    onPickReceiptImage: () -> Unit,
    onSubmitReview: (Booking) -> Unit,
    onOpenReport: (Int, Int?) -> Unit,
    onCloseReport: () -> Unit,
    onReportReasonChange: (String) -> Unit,
    onReportDetailsChange: (String) -> Unit,
    onSubmitReport: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ProviderGigForm(
            editingGigId = editingGigId,
            title = title,
            description = description,
            category = category,
            price = price,
            imageUrl = imageUrl,
            green = green,
            onTitleChange = onTitleChange,
            onDescriptionChange = onDescriptionChange,
            onCategoryChange = onCategoryChange,
            onPriceChange = onPriceChange,
            onImageUrlChange = onImageUrlChange,
            onPickProviderImages = onPickProviderImages,
            onSaveProviderGig = onSaveProviderGig,
            onClearProviderGigForm = onClearProviderGigForm,
        )

        ProviderGigList(
            providerGigs = providerGigs,
            isLoading = isProviderGigsLoading,
            green = green,
            onRefresh = onRefreshProviderGigs,
            onEdit = onEditProviderGig,
            onDisable = onDisableProviderGig,
        )

        BookingSection(
            title = "Incoming jobs",
            emptyText = "No clients booked your gigs yet.",
            bookings = providerBookings,
            isLoading = isBookingsLoading,
            selectedBookingId = selectedBookingId,
            receiptUrl = receiptUrl,
            reviewRating = reviewRating,
            reviewComment = reviewComment,
            reportTargetUserId = reportTargetUserId,
            reportBookingId = reportBookingId,
            reportReason = reportReason,
            reportDetails = reportDetails,
            green = green,
            mode = BookingMode.Provider,
            onRefresh = onRefreshBookings,
            onUpdateStatus = onUpdateBookingStatus,
            onSelectBooking = onSelectBooking,
            onReceiptUrlChange = onReceiptUrlChange,
            onReviewRatingChange = onReviewRatingChange,
            onReviewCommentChange = onReviewCommentChange,
            onSubmitReceipt = onSubmitReceipt,
            onPickReceiptImage = onPickReceiptImage,
            onSubmitReview = onSubmitReview,
            onOpenReport = onOpenReport,
            onCloseReport = onCloseReport,
            onReportReasonChange = onReportReasonChange,
            onReportDetailsChange = onReportDetailsChange,
            onSubmitReport = onSubmitReport,
        )
    }
}

@Composable
private fun ProviderGigForm(
    editingGigId: Int?,
    title: String,
    description: String,
    category: String,
    price: String,
    imageUrl: String,
    green: Color,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onPickProviderImages: () -> Unit,
    onSaveProviderGig: () -> Unit,
    onClearProviderGigForm: () -> Unit,
) {
    val imageCount = imageUrl
        .split("\n", ",", ";")
        .map { it.trim() }
        .count { it.isNotBlank() }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFE8F5F0),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = if (editingGigId == null) "Create gig" else "Edit gig",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(value = title, onValueChange = onTitleChange, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = onDescriptionChange, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = onCategoryChange, label = { Text("Category") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = onPriceChange, label = { Text("Price") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = imageUrl,
                onValueChange = onImageUrlChange,
                label = { Text("Uploaded image paths") },
                supportingText = { Text("$imageCount/5 images") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedButton(onClick = onPickProviderImages, modifier = Modifier.fillMaxWidth()) {
                Text("Upload gig images")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSaveProviderGig,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                ) {
                    Text(if (editingGigId == null) "Create" else "Save")
                }
                OutlinedButton(onClick = onClearProviderGigForm, modifier = Modifier.weight(1f)) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
private fun ProviderGigList(
    providerGigs: List<Gig>,
    isLoading: Boolean,
    green: Color,
    onRefresh: () -> Unit,
    onEdit: (Gig) -> Unit,
    onDisable: (Gig) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("My gigs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = onRefresh, enabled = !isLoading) {
                Text(if (isLoading) "Loading" else "Refresh")
            }
        }

        if (providerGigs.isEmpty() && !isLoading) {
            Text("You have no gigs yet.", color = TextSecondary)
        }

        providerGigs.forEach { gig ->
            ProviderGigCard(gig = gig, green = green, onEdit = { onEdit(gig) }, onDisable = { onDisable(gig) })
        }
    }
}

@Composable
private fun ProviderGigCard(
    gig: Gig,
    green: Color,
    onEdit: () -> Unit,
    onDisable: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFB),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(gig.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(gig.description, color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RoleChip(text = gig.category.ifBlank { "Service" }, green = green)
                RoleChip(text = "PHP ${gig.price.toInt()}", green = green)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                ) {
                    Text("Edit")
                }
                OutlinedButton(onClick = onDisable, modifier = Modifier.weight(1f)) {
                    Text("Disable")
                }
            }
        }
    }
}

@Composable
private fun GigCard(
    gig: Gig,
    green: Color,
    modifier: Modifier = Modifier,
    onOpenDetails: () -> Unit,
    onBook: () -> Unit,
    onToggleLike: () -> Unit,
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onOpenDetails),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFB),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = gig.providerName,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "View profile and reviews",
                        color = green,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                RoleChip(text = gig.category.ifBlank { "Service" }, green = green)
            }
            GigImageCarousel(imageUrls = gig.imageUrls, label = gig.category, green = green)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${gig.likeCount} likes",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
                HeartButton(liked = gig.likedByCurrentUser, green = green, onClick = onToggleLike)
            }
            Text(
                text = gig.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = gig.description,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = gig.providerName,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "PHP ${gig.price.toInt()}",
                    color = green,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Black,
                )
            }
            Button(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = green)
            ) {
                Text("Book service")
            }
        }
    }
}

@Composable
private fun GigDetailsPanel(
    gig: Gig,
    reviews: List<Review>,
    isReviewsLoading: Boolean,
    green: Color,
    onBook: () -> Unit,
    onClose: () -> Unit,
    onReportProvider: () -> Unit,
    onToggleLike: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFE8F5F0),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = gig.providerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                OutlinedButton(onClick = onClose) {
                    Text("Close")
                }
            }
            GigImageCarousel(imageUrls = gig.imageUrls, label = gig.category, green = green)
            Text(text = gig.title, fontWeight = FontWeight.Bold)
            Text(text = gig.description, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RoleChip(text = gig.category.ifBlank { "Service" }, green = green)
                RoleChip(text = "PHP ${gig.price.toInt()}", green = green)
                RoleChip(text = "${gig.likeCount} likes", green = green)
            }

            Text("Reviews", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            if (isReviewsLoading) {
                CircularProgressIndicator(color = green)
            } else if (reviews.isEmpty()) {
                Text("No reviews yet.", color = TextSecondary)
            } else {
                reviews.forEach { review ->
                    ReviewItem(review = review, green = green)
                }
            }

            Button(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = green),
            ) {
                Text("Book this provider")
            }
            HeartButton(
                liked = gig.likedByCurrentUser,
                green = green,
                onClick = onToggleLike,
                modifier = Modifier.align(Alignment.End),
            )
            OutlinedButton(onClick = onReportProvider, modifier = Modifier.fillMaxWidth()) {
                Text("Report provider")
            }
        }
    }
}

@Composable
private fun HeartButton(
    liked: Boolean,
    green: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(42.dp)
            .height(34.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(100.dp),
        color = if (liked) Color(0xFFFFECEF) else Color(0xFFE8F5F0),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = if (liked) "♥" else "♡",
                color = if (liked) Color(0xFFD92D4B) else green,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ReportPanel(
    reason: String,
    details: String,
    green: Color,
    onReasonChange: (String) -> Unit,
    onDetailsChange: (String) -> Unit,
    onSubmitReport: () -> Unit,
    onClose: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFFF7ED),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Submit report", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = reason,
                onValueChange = onReasonChange,
                label = { Text("Reason") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = details,
                onValueChange = onDetailsChange,
                label = { Text("Details") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSubmitReport,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                ) {
                    Text("Submit")
                }
                OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(review: Review, green: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(review.clientName, fontWeight = FontWeight.Bold)
                Text("${review.rating}/5", color = green, fontWeight = FontWeight.Bold)
            }
            Text(review.comment.ifBlank { "No comment provided." }, color = TextSecondary)
        }
    }
}

@Composable
private fun BookingPanel(
    gig: Gig,
    bookingDate: String,
    bookingPhone: String,
    bookingAddress: String,
    bookingNotes: String,
    isBooking: Boolean,
    green: Color,
    onBookingDateChange: (String) -> Unit,
    onBookingPhoneChange: (String) -> Unit,
    onBookingAddressChange: (String) -> Unit,
    onBookingNotesChange: (String) -> Unit,
    onCancelBooking: () -> Unit,
    onSubmitBooking: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFE8F5F0),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Book ${gig.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Date format: 2026-05-24T10:00:00",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
            )
            OutlinedTextField(
                value = bookingDate,
                onValueChange = onBookingDateChange,
                label = { Text("Schedule") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = bookingPhone,
                onValueChange = onBookingPhoneChange,
                label = { Text("Phone number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = bookingAddress,
                onValueChange = onBookingAddressChange,
                label = { Text("Service address") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = bookingNotes,
                onValueChange = onBookingNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCancelBooking, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
                Button(
                    onClick = onSubmitBooking,
                    enabled = !isBooking,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = green)
                ) {
                    Text(if (isBooking) "Sending" else "Submit")
                }
            }
        }
    }
}

@Composable
private fun GigImageCarousel(imageUrls: List<String>, label: String, green: Color) {
    var currentIndex by remember(imageUrls) { mutableStateOf(0) }
    val safeImages = imageUrls.filter { it.isNotBlank() }.take(5)
    val currentImage = safeImages.getOrNull(currentIndex).orEmpty()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        GigImage(imageUrl = currentImage, label = label, green = green)
        if (safeImages.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ImageArrowButton(
                    onClick = { currentIndex = (currentIndex - 1 + safeImages.size) % safeImages.size },
                    text = "<",
                    green = green,
                )
                Text(
                    text = "${currentIndex + 1}/${safeImages.size}",
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(horizontal = 8.dp),
                )
                ImageArrowButton(
                    onClick = { currentIndex = (currentIndex + 1) % safeImages.size },
                    text = ">",
                    green = green,
                )
            }
        }
    }
}

@Composable
private fun ImageArrowButton(onClick: () -> Unit, text: String, green: Color) {
    Surface(
        modifier = Modifier
            .width(42.dp)
            .height(34.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(100.dp),
        color = Color(0xFFE8F5F0),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = text,
                color = green,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun GigImage(imageUrl: String, label: String, green: Color) {
    var bitmap by remember(imageUrl) { mutableStateOf<Bitmap?>(null) }
    var failed by remember(imageUrl) { mutableStateOf(false) }

    LaunchedEffect(imageUrl) {
        bitmap = null
        failed = false
        if (imageUrl.isBlank()) return@LaunchedEffect

        val loadedBitmap = withContext(Dispatchers.IO) {
            try {
                URL(imageUrl).openStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } catch (_: Exception) {
                null
            }
        }
        bitmap = loadedBitmap
        failed = loadedBitmap == null
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.55f),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFEDF2FA),
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "$label work image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = if (failed) "Image unavailable" else label.ifBlank { "Service" },
                color = green,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}

private enum class BookingMode {
    Client,
    Provider,
}

@Composable
private fun BookingSection(
    title: String,
    emptyText: String,
    bookings: List<Booking>,
    isLoading: Boolean,
    selectedBookingId: Int?,
    receiptUrl: String,
    reviewRating: String,
    reviewComment: String,
    reportTargetUserId: Int?,
    reportBookingId: Int?,
    reportReason: String,
    reportDetails: String,
    green: Color,
    mode: BookingMode,
    onRefresh: () -> Unit,
    onUpdateStatus: (Booking, String) -> Unit,
    onSelectBooking: (Booking) -> Unit,
    onReceiptUrlChange: (String) -> Unit,
    onReviewRatingChange: (String) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReceipt: (Booking) -> Unit,
    onPickReceiptImage: () -> Unit,
    onSubmitReview: (Booking) -> Unit,
    onOpenReport: (Int, Int?) -> Unit,
    onCloseReport: () -> Unit,
    onReportReasonChange: (String) -> Unit,
    onReportDetailsChange: (String) -> Unit,
    onSubmitReport: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            OutlinedButton(onClick = onRefresh, enabled = !isLoading) {
                Text(if (isLoading) "Loading" else "Refresh")
            }
        }

        if (bookings.isEmpty()) {
            Text(
                text = emptyText,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        bookings.forEach { booking ->
            BookingCard(
                booking = booking,
                green = green,
                mode = mode,
                expanded = selectedBookingId == booking.bookingId,
                receiptUrl = receiptUrl,
                reviewRating = reviewRating,
                reviewComment = reviewComment,
                reportTargetUserId = reportTargetUserId,
                reportBookingId = reportBookingId,
                reportReason = reportReason,
                reportDetails = reportDetails,
                onSelectBooking = { onSelectBooking(booking) },
                onUpdateStatus = onUpdateStatus,
                onReceiptUrlChange = onReceiptUrlChange,
                onReviewRatingChange = onReviewRatingChange,
                onReviewCommentChange = onReviewCommentChange,
                onSubmitReceipt = { onSubmitReceipt(booking) },
                onPickReceiptImage = onPickReceiptImage,
                onSubmitReview = { onSubmitReview(booking) },
                onOpenReport = onOpenReport,
                onCloseReport = onCloseReport,
                onReportReasonChange = onReportReasonChange,
                onReportDetailsChange = onReportDetailsChange,
                onSubmitReport = onSubmitReport,
            )
        }
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    green: Color,
    mode: BookingMode,
    expanded: Boolean,
    receiptUrl: String,
    reviewRating: String,
    reviewComment: String,
    reportTargetUserId: Int?,
    reportBookingId: Int?,
    reportReason: String,
    reportDetails: String,
    onSelectBooking: () -> Unit,
    onUpdateStatus: (Booking, String) -> Unit,
    onReceiptUrlChange: (String) -> Unit,
    onReviewRatingChange: (String) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReceipt: () -> Unit,
    onPickReceiptImage: () -> Unit,
    onSubmitReview: () -> Unit,
    onOpenReport: (Int, Int?) -> Unit,
    onCloseReport: () -> Unit,
    onReportReasonChange: (String) -> Unit,
    onReportDetailsChange: (String) -> Unit,
    onSubmitReport: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelectBooking),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFB),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = booking.gigTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                RoleChip(text = booking.status.toStatusLabel(), green = green)
            }
            Text(
                text = if (mode == BookingMode.Provider) "Client: ${booking.clientName}" else "Provider: ${booking.providerName}",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(text = "Schedule: ${booking.bookingDate}", color = TextSecondary)
            Text(text = "Phone: ${booking.contactPhone}", color = TextSecondary)
            Text(text = "Address: ${booking.serviceAddress}", color = TextSecondary)
            if (booking.clientNotes.isNotBlank()) {
                Text(text = "Notes: ${booking.clientNotes}", color = TextSecondary)
            }
            if (booking.receiptUrl.isNotBlank()) {
                Text(text = "Receipt: ${booking.receiptUrl}", color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }

            if (expanded) {
                BookingDetailActions(
                    booking = booking,
                    mode = mode,
                    green = green,
                    receiptUrl = receiptUrl,
                    reviewRating = reviewRating,
                    reviewComment = reviewComment,
                    onReceiptUrlChange = onReceiptUrlChange,
                    onReviewRatingChange = onReviewRatingChange,
                    onReviewCommentChange = onReviewCommentChange,
                    onSubmitReceipt = onSubmitReceipt,
                    onPickReceiptImage = onPickReceiptImage,
                    onSubmitReview = onSubmitReview,
                )
                if (reportTargetUserId != null && reportBookingId == booking.bookingId) {
                    ReportPanel(
                        reason = reportReason,
                        details = reportDetails,
                        green = green,
                        onReasonChange = onReportReasonChange,
                        onDetailsChange = onReportDetailsChange,
                        onSubmitReport = onSubmitReport,
                        onClose = onCloseReport,
                    )
                }
            } else {
                Text("Tap to view details", color = green, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }

            if (mode == BookingMode.Provider) {
                ProviderBookingActions(
                    booking = booking,
                    green = green,
                    onUpdateStatus = onUpdateStatus,
                )
            } else if (booking.status == "PENDING" || booking.status == "ACCEPTED") {
                OutlinedButton(
                    onClick = { onUpdateStatus(booking, "CANCELLED") },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel booking")
                }
            }
            if (expanded) {
                val targetUserId = if (mode == BookingMode.Provider) booking.clientId else booking.providerId
                OutlinedButton(
                    onClick = { onOpenReport(targetUserId, booking.bookingId) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (mode == BookingMode.Provider) "Report client" else "Report provider")
                }
            }
        }
    }
}

@Composable
private fun BookingDetailActions(
    booking: Booking,
    mode: BookingMode,
    green: Color,
    receiptUrl: String,
    reviewRating: String,
    reviewComment: String,
    onReceiptUrlChange: (String) -> Unit,
    onReviewRatingChange: (String) -> Unit,
    onReviewCommentChange: (String) -> Unit,
    onSubmitReceipt: () -> Unit,
    onPickReceiptImage: () -> Unit,
    onSubmitReview: () -> Unit,
) {
    if (mode == BookingMode.Provider) {
        Text(
            text = "Use the action buttons below to update this job status.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
        return
    }

    when (booking.status) {
        "ACCEPTED", "IN_PROGRESS" -> {
            Text(
                text = "Submit receipt to complete this service.",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(
                value = receiptUrl,
                onValueChange = onReceiptUrlChange,
                label = { Text("Uploaded receipt path") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedButton(onClick = onPickReceiptImage, modifier = Modifier.fillMaxWidth()) {
                Text("Upload receipt image")
            }
            Button(
                onClick = onSubmitReceipt,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = green),
            ) {
                Text("Submit receipt")
            }
        }
        "COMPLETED" -> {
            Text(
                text = "Review this completed service.",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(
                value = reviewRating,
                onValueChange = onReviewRatingChange,
                label = { Text("Rating 1-5") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = reviewComment,
                onValueChange = onReviewCommentChange,
                label = { Text("Review comment") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = onSubmitReview,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = green),
            ) {
                Text("Submit review")
            }
        }
        "PENDING" -> Text(
            text = "Waiting for provider approval.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
        "REJECTED", "CANCELLED" -> Text(
            text = "This booking is closed.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ProviderBookingActions(
    booking: Booking,
    green: Color,
    onUpdateStatus: (Booking, String) -> Unit,
) {
    when (booking.status) {
        "PENDING" -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onUpdateStatus(booking, "ACCEPTED") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = green),
            ) {
                Text("Accept")
            }
            OutlinedButton(
                onClick = { onUpdateStatus(booking, "REJECTED") },
                modifier = Modifier.weight(1f),
            ) {
                Text("Reject")
            }
        }
        "ACCEPTED" -> Button(
            onClick = { onUpdateStatus(booking, "IN_PROGRESS") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = green),
        ) {
            Text("Start job")
        }
        "IN_PROGRESS" -> Button(
            onClick = { onUpdateStatus(booking, "COMPLETED") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = green),
        ) {
            Text("Mark completed")
        }
    }
}

@Composable
private fun RoleChip(text: String, green: Color) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = Color(0xFFE8F5F0),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = green,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    SideLTheme(dynamicColor = false) {
        SideLApp(
            authScreen = AuthScreen.Landing,
            email = "client@test.com",
            password = "Password@123",
            signupFirstname = "",
            signupLastname = "",
            signupEmail = "",
            signupPassword = "",
            signupPhone = "",
            signupAddress = "",
            apiBaseUrl = DEFAULT_API_URL,
            isLoading = false,
            message = "",
            loggedUser = null,
            gigs = emptyList(),
            isGigsLoading = false,
            selectedGig = null,
            selectedGigDetails = null,
            gigReviews = emptyList(),
            isReviewsLoading = false,
            bookingDate = "",
            bookingPhone = "",
            bookingAddress = "",
            bookingNotes = "",
            isBooking = false,
            clientBookings = emptyList(),
            providerBookings = emptyList(),
            providerGigs = emptyList(),
            userReports = emptyList(),
            isBookingsLoading = false,
            isProviderGigsLoading = false,
            isReportsLoading = false,
            selectedBookingId = null,
            receiptUrl = "",
            reviewRating = "",
            reviewComment = "",
            reportTargetUserId = null,
            reportBookingId = null,
            reportReason = "",
            reportDetails = "",
            editingGigId = null,
            providerGigTitle = "",
            providerGigDescription = "",
            providerGigCategory = "Electrical",
            providerGigPrice = "",
            providerGigImageUrl = "",
            isEditingProfile = false,
            profileFirstname = "",
            profileLastname = "",
            profileEmail = "",
            profilePhone = "",
            profileAddress = "",
            profileBio = "",
            selectedTab = AppTab.Browse,
            searchQuery = "",
            selectedCategory = "All",
            onEmailChange = {},
            onPasswordChange = {},
            onSignupFirstnameChange = {},
            onSignupLastnameChange = {},
            onSignupEmailChange = {},
            onSignupPasswordChange = {},
            onSignupPhoneChange = {},
            onSignupAddressChange = {},
            onApiBaseUrlChange = {},
            onBookingDateChange = {},
            onBookingPhoneChange = {},
            onBookingAddressChange = {},
            onBookingNotesChange = {},
            onLogin = {},
            onSignup = {},
            onLogout = {},
            onShowLanding = {},
            onShowLogin = {},
            onShowSignup = {},
            onRefreshGigs = {},
            onSelectGig = {},
            onToggleGigLike = {},
            onOpenGigDetails = {},
            onCloseGigDetails = {},
            onCancelBooking = {},
            onSubmitBooking = {},
            onRefreshBookings = {},
            onUpdateBookingStatus = { _, _ -> },
            onSelectBooking = {},
            onReceiptUrlChange = {},
            onReviewRatingChange = {},
            onReviewCommentChange = {},
            onSubmitReceipt = {},
            onPickReceiptImage = {},
            onSubmitReview = {},
            onOpenReport = { _, _ -> },
            onCloseReport = {},
            onReportReasonChange = {},
            onReportDetailsChange = {},
            onSubmitReport = {},
            onRefreshProviderGigs = {},
            onEditProviderGig = {},
            onClearProviderGigForm = {},
            onProviderGigTitleChange = {},
            onProviderGigDescriptionChange = {},
            onProviderGigCategoryChange = {},
            onProviderGigPriceChange = {},
            onProviderGigImageUrlChange = {},
            onPickProviderImages = {},
            onSaveProviderGig = {},
            onDisableProviderGig = {},
            onStartEditProfile = {},
            onCancelEditProfile = {},
            onProfileFirstnameChange = {},
            onProfileLastnameChange = {},
            onProfileEmailChange = {},
            onProfilePhoneChange = {},
            onProfileAddressChange = {},
            onProfileBioChange = {},
            onSaveProfile = {},
            onRefreshReports = {},
            onSelectTab = {},
            onSearchQueryChange = {},
            onSelectedCategoryChange = {},
        )
    }
}
