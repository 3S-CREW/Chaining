package com.example.chaining.data.model


import com.google.gson.annotations.SerializedName

data class AreaCodeResponse(
    @SerializedName("response")
    val response: Response
) {
    data class Response(
        @SerializedName("body")
        val body: Body,
        @SerializedName("header")
        val header: Header
    ) {
        data class Body(
            @SerializedName("items")
            val items: Items,
            @SerializedName("numOfRows")
            val numOfRows: Int,
            @SerializedName("pageNo")
            val pageNo: Int,
            @SerializedName("totalCount")
            val totalCount: Int
        ) {
            data class Items(
                @SerializedName("item")
                val item: List<Item>
            ) {
                data class Item(
                    @SerializedName("lDongRegnCd")
                    val lDongRegnCd: String,
                    @SerializedName("lDongRegnNm")
                    val lDongRegnNm: String,
                    @SerializedName("lDongSignguCd")
                    val lDongSignguCd: String,
                    @SerializedName("lDongSignguNm")
                    val lDongSignguNm: String,
                    @SerializedName("rnum")
                    val rnum: Int
                )
            }
        }

        data class Header(
            @SerializedName("resultCode")
            val resultCode: String,
            @SerializedName("resultMsg")
            val resultMsg: String
        )
    }
}