package com.task.busdriver.domain.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Suppress("PLUGIN_IS_NOT_ENABLED")
@Serializable
data class Data (
  @SerialName("id") var id : Int?    = null,
  @SerialName("name") var name : String? = null,
  @SerialName("userName") var userName : String? = null,
  @SerialName("email") var email : String? = null,
  @SerialName("language") var language : String? = null,
  @SerialName("phone") var phone : String? = null,
  @SerialName("about") var about : String? = null,
  @SerialName("unit_code") var unitCode : String? = null,
  @SerialName("photo") var photo : String? = null

)