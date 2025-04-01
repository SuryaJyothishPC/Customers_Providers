package com.example.sujimisscasestudyassignment

data class Request(
    var RequestID : String = "",
    val CustomerId: String = "",
    val ServiceType: String = "",
    val ProviderID: String = "",
    val ServicePrice: String = "",
    val ServiceTime: String = "",
    val ScheduledTimeFrom: String = "",
    val ScheduledTimeTo : String = "",
    val ScheduledDate: String = "",
    val RequestStatus: String = ""
)
