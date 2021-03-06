package com.iqonic.store.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseResponse(val message: String? = null)
data class UpdateCartResponse(val message: String, val quantity: Int)
data class AddCartResponse(
    val code: String,
    val data: AddCartData,
    val message: String
)

data class GetStripeClientSecret(
    val client_secret: String,
    val message: String
)

data class ProfileImage(
    val code: Int = 0,
    val message: String = "",
    val profile_image: String = ""
)

data class AddCartData(
    val status: Int
)


data class CheckoutResponse(
    val checkout_url: String
)

class CreateOrderResponse (

    val id : Int,
    val parent_id : Int,
    val number : Int,
    val order_key : String,
    val created_via : String,
    val version : String,
    val status : String,
    val currency : String,
    val date_created : String,
    val date_created_gmt : String,
    val date_modified : String,
    val date_modified_gmt : String,
    val discount_total : Double,
    val discount_tax : Double,
    val shipping_total : Double,
    val shipping_tax : Double,
    val cart_tax : Double,
    val total : Double,
    val total_tax : Double,
    val prices_include_tax : Boolean,
    val customer_id : Int,
    val customer_ip_address : String,
    val customer_user_agent : String,
    val customer_note : String,
    val billing : Billing,
    val shipping : Shipping,
    val payment_method : String,
    val payment_method_title : String,
    val transaction_id : String,
    val date_paid : String,
    val date_paid_gmt : String,
    val date_completed : String,
    val date_completed_gmt : String,
    val cart_hash : String,
    val meta_data : List<String>,
    val line_items : List<Line_items>,
    val tax_lines : List<String>,
    val shipping_lines : List<ShippingLines>,
    val fee_lines : List<String>,
    val coupon_lines : List<CouponLines>,
    val refunds : List<String>,
    val currency_symbol : String
) : Serializable


class CartResponse {
    val code: String = ""
    val cart_id: String = ""
    val created_at: String = ""
    val full: String = ""
    val name: String = ""
    val price: String = "0.0"
    var pro_id: String = ""
    var quantity: String = ""
    val regular_price: String = "0.0"
    val sale_price: String = "0.0"
    val on_sale: Boolean = false
    val sku: String = ""
    val thumbnail: String = ""
}

data class RegisterResponse(
    val code: Int,
    val data: Data,
    val message: String
)

data class Data(
    val ID: String,
    val display_name: String,
    val user_activation_key: String,
    val user_email: String,
    val user_login: String,
    val user_nicename: String,
    val user_registered: String,
    val user_status: String,
    val user_url: String
)

data class loginResponse(
    val avatar: String,
    val profile_image: String,
    val token: String,
    val first_name: String,
    val last_name: String,
    val user_display_name: String,
    val user_email: String,
    val user_id: Int,
    val user_nicename: String,
    val user_role: List<String>,
    val shipping: Shipping,
    val billing: Billing

)

data class CustomerData(
    // val _links: CustomerLinks,
    val avatar_url: String,
    val billing: Billing,
    val date_created: String,
    val date_created_gmt: String,
    val date_modified: String,
    val date_modified_gmt: String,
    val email: String,
    val first_name: String,
    val id: Int,
    val is_paying_customer: Boolean,
    val last_name: String,
    // val meta_data: List<MetaData>,
    val role: String,
    val shipping: Shipping,
    val username: String
)

data class CustomerLinks(
    val collection: List<CustomerCollection>,
    val self: List<CustomerSelf>
)

data class CustomerCollection(
    val href: String
)

data class CustomerSelf(
    val href: String
)

class Billing : Serializable {
    var address_1: String = ""
    var address_2: String = ""
    var city: String = ""
    var country: String = ""
    var first_name: String = ""
    var last_name: String = ""
    var phone: String = ""
    var postcode: String = ""
    var state: String = ""
    var company: String = ""
    var email: String = ""
    fun getFullAddress(sap:String=","):String{
        return "$address_1$sap$address_2$sap$city $postcode$sap$state$sap$country"
    }
}

data class MetaData(
    val id: Int,
    val key: String,
    val value: String
) : Serializable


class Shipping : Serializable {
    var address_1: String = ""
    var address_2: String = ""
    var city: String = ""
    var country: String = ""
    var first_name: String = ""
    var last_name: String = ""
    var phone: String = ""
    var postcode: String = ""
    var state: String = ""
    var company: String = ""
    fun getFullAddress(sap:String=","):String{
        return "$address_1$sap$address_2$sap$city $postcode$sap$state$sap$country"
    }
}

open class Card {
    @SerializedName("card_img")
    var cardImg: Int? = null

    @SerializedName("card_type")
    var cardType: String? = null

    @SerializedName("bank_name")
    var bankName: String? = null

    @SerializedName("card_number")
    var cardNumber: String? = null

    @SerializedName("valid_date")
    var validDate: String? = null

    @SerializedName("holder_name")
    var holderName: String? = null
}

open class Address : Serializable {

    @SerializedName("full_name")
    var fullName: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("phone_no")
    var mobileNo: String? = null

    @SerializedName("address_type")
    var addressType: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("pin_code")
    var pincode: String? = null

    @SerializedName("state")
    var state: String? = null

    @SerializedName("is_default")
    var isDefault: Boolean? = false

}

open class Reward {
    @SerializedName("reward_img")
    var rewardImg: Int? = null

    @SerializedName("reward_value")
    var rewardValue: String? = null

    @SerializedName("reward")
    var reward: Int? = null

}

data class Attribute(
    val count: Int = 0,
    val description: String = "",
    val id: Int = 0,
    val menu_order: Int = 0,
    val name: String = "",
    val slug: String = ""
) : Serializable

data class Order(
    val billing: Billing,
    val cart_hash: String,
    val cart_tax: String,
    // val coupon_lines: List<Any>,
    val created_via: String,
    val currency: String,
    val customer_id: Int,
    val customer_ip_address: String,
    val customer_note: String,
    val customer_user_agent: String,
    val date_completed: Any,
    val date_created: DateCreated,
    val date_modified: DateModified,
    val date_paid: DatePaid,
    val discount_tax: String,
    val discount_total: String,
    val fee_lines: List<Any>,
    val id: Int,
    val line_items: List<LineItems>,
    val meta_data: List<Any>,
    val number: String,
    val order_key: String,
    val parent_id: Int,
    val payment_method: String,
    val payment_method_title: String,
    val prices_include_tax: Boolean,
    val shipping: Shipping,
    //val shipping_lines: List<shipping_lines>,
    val shipping_tax: String,
    val shipping_total: String,
    val status: String,
    val tax_lines: List<Any>,
    val total: String,
    val total_tax: String,
    val transaction_id: String,
    val version: String
) : Serializable


data class DateCreated(
    val date: String,
    val timezone: String,
    val timezone_type: Int
) : Serializable

data class DateModified(
    val date: String,
    val timezone: String,
    val timezone_type: Int
) : Serializable

data class DatePaid(
    val date: String,
    val timezone: String,
    val timezone_type: Int
) : Serializable

data class LineItems(
    val id: Int,
    val meta_data: List<MetaData>,
    val name: String,
    val order_id: Int,
    val product_id: Int,
    val product_images: List<ProductImage>,
    val quantity: Int,
    val subtotal: String,
    val subtotal_tax: String,
    val tax_class: String,
    val taxes: Taxes,
    val total: String,
    val total_tax: String,
    val variation_id: Int
) : Serializable


data class ProductImage(
    val alt: String,
    val date_created: String,
    val date_modified: String,
    val id: Int,
    val name: String,
    val position: Int,
    val src: String
) : Serializable

data class Taxes(
    val subtotal: List<Any>,
    val total: List<Any>
) : Serializable


data class OrderTrack(
    val date_shipped: String,
    val tracking_id: String,
    val tracking_link: String,
    val tracking_number: String,
    val tracking_provider: String
) : Serializable

data class OrderNotes(
    val _links: Links,
    val author: String,
    val customer_note: Boolean,
    val date_created: String,
    val date_created_gmt: String,
    val id: Int,
    val note: String
) : Serializable

data class Up(
    val href: String
)

data class StoreProductAttribute(
    val has_archives: Int,
    val id: String,
    val name: String,
    val order_by: String,
    val slug: String,
    val terms: List<Term>,
    val type: String
)

data class Term(
    val count: Int = 0,
    val description: String = "",
    val filter: String = "",
    var name: String = "",
    val parent: Int = 0,
    val slug: String = "",
    val taxonomy: String = "",
    val term_group: Int = 0,
    val term_id: Int = 0,
    val term_taxonomy_id: Int = 0,
    var isSelected: Boolean = false,
    var isParent: Boolean = false
)
