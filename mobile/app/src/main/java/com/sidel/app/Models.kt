package com.sidel.app

const val SESSION_PREFS = "sidel_session"
const val DEFAULT_API_URL = "http://192.168.1.2:8080/api"

data class LoggedUser(
    val userId: Int,
    val firstname: String,
    val lastname: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val bio: String,
    val isProvider: Boolean,
    val isAdmin: Boolean,
    val providerStatus: String,
)

data class Gig(
    val gigId: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val likeCount: Int,
    val likedByCurrentUser: Boolean,
    val providerId: Int,
    val providerName: String,
    val imageUrl: String,
    val imageUrls: List<String>,
)

data class Review(
    val reviewId: Int,
    val clientName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String,
)

data class Booking(
    val bookingId: Int,
    val gigId: Int,
    val clientId: Int,
    val providerId: Int,
    val gigTitle: String,
    val clientName: String,
    val providerName: String,
    val bookingDate: String,
    val contactPhone: String,
    val serviceAddress: String,
    val clientNotes: String,
    val receiptUrl: String,
    val status: String,
)

data class UserReport(
    val reportId: Int,
    val reporterId: Int,
    val reportedUserId: Int,
    val reportedUserName: String,
    val reason: String,
    val details: String,
    val status: String,
    val createdAt: String,
)


val serviceCategories = listOf(
    "All",
    "Electrical",
    "Plumbing",
    "Home Cleaning",
    "Appliance Repair",
    "Electronics Repair",
    "Gadget Repair",
    "Motorcycle Mechanic",
    "Car Mechanic",
    "HVAC",
    "Handyman",
    "Landscaping",
    "Pest Control",
    "Pool Maintenance",
    "Window Cleaning",
    "Carpet Cleaning",
)

enum class AppTab {
    Browse,
    Bookings,
    Jobs,
    Profile,
}

enum class AuthScreen {
    Landing,
    Login,
    Signup,
}

fun String.toStatusLabel(): String {
    return lowercase()
        .split("_")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
}

