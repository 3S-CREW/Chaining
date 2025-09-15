package com.example.chaining.data.model

import com.google.gson.annotations.SerializedName

data class AreaCodeResponse(
    @SerializedName("response")
    val response: AreaCodeResponse,
) {
    data class AreaCodeResponse(
        @SerializedName("body")
        val body: AreaCodeBody,
        @SerializedName("header")
        val header: AreaCodeHeader,
    ) {
        data class AreaCodeBody(
            @SerializedName("items")
            val items: AreaCodeItems,
            @SerializedName("numOfRows")
            val numOfRows: Int,
            @SerializedName("pageNo")
            val pageNo: Int,
            @SerializedName("totalCount")
            val totalCount: Int,
        ) {
            data class AreaCodeItems(
                @SerializedName("item")
                val item: List<AreaCodeItem>,
            ) {
                data class AreaCodeItem(
                    @SerializedName("lDongRegnCd")
                    val lDongRegnCd: String,
                    @SerializedName("lDongRegnNm")
                    val lDongRegnNm: String,
                    @SerializedName("lDongSignguCd")
                    val lDongSignguCd: String,
                    @SerializedName("lDongSignguNm")
                    val lDongSignguNm: String,
                    @SerializedName("rnum")
                    val rnum: Int,
                )
            }
        }

        data class AreaCodeHeader(
            @SerializedName("resultCode")
            val resultCode: String,
            @SerializedName("resultMsg")
            val resultMsg: String,
        )
    }
}
