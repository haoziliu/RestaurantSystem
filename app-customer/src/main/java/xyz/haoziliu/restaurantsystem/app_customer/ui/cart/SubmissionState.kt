package xyz.haoziliu.restaurantsystem.app_customer.ui.cart

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Loading : SubmissionState()
    data class Success(val ticketNum: String) : SubmissionState()
    data class Error(val msg: String) : SubmissionState()
}