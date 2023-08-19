package com.tanfra.shopmob.smob.data.net.api
//
//import com.tanfra.shopmob.Constants
//import com.tanfra.shopmob.smob.data.net.nto.Nto
//import io.ktor.client.HttpClient
//import io.ktor.client.request.setBody
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//
//
//class SmobItemApiImpl<NTO: Nto>(
//    private val ktor: HttpClient,
//    private val tableUrlPart: String,
//): SmobItemApi<NTO> {
//
//    override suspend fun getSmobItemById(id: String): Result<NTO> =
//        ktor.getItem("${Constants.SMOB_API_URL}/$tableUrlPart/$id") {
//            contentType(ContentType.Application.Json)
//        }
//
//    override suspend fun getSmobItems(): Result<ArrayList<NTO>> =
//        ktor.getItem("${Constants.SMOB_API_URL}/$tableUrlPart") {
//            contentType(ContentType.Application.Json)
//        }
//
//    override suspend fun saveSmobItem(newItem: NTO): Result<Void> =
//        ktor.postItem("${Constants.SMOB_API_URL}/$tableUrlPart") {
//            contentType(ContentType.Application.Json)
//            setBody(newItem)
//        }
//
//    override suspend fun updateSmobItemById(id: String, newItem: NTO): Result<Void> =
//        ktor.putItem("${Constants.SMOB_API_URL}/$tableUrlPart/$id") {
//            contentType(ContentType.Application.Json)
//            setBody(newItem)
//        }
//
//    override suspend fun deleteSmobItemById(id: String): Result<Void> =
//        ktor.deleteItem("${Constants.SMOB_API_URL}/$tableUrlPart/$id") {
//            contentType(ContentType.Application.Json)
//        }
//
//}