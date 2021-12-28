package com.tanfra.shopmob.smob.data.net.utils

import com.squareup.moshi.*
import com.squareup.moshi.Types.collectionElementType
import java.io.IOException
import java.lang.reflect.Type


// from: https://gist.github.com/jishindev/9fd8f0191225dc8f0d1f7a6f6f0511a1#file-arraylistadapter-kt
//
// also see: https://bladecoder.medium.com/advanced-json-parsing-techniques-using-moshi-and-kotlin-daf56a7b963d
// (explains the mechanics behind the above code)
@Suppress("UNCHECKED_CAST")
class ArrayListAdapter<T>(private val adapter: JsonAdapter<T>) : JsonAdapter<ArrayList<T>>() {

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): ArrayList<T> {
        val arrayList = arrayListOf<T>()

        reader.beginArray()
        while (reader.hasNext()) {
            (adapter.fromJson(reader))?.let { arrayList.add(it) }
        }
        reader.endArray()

        return arrayList
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: ArrayList<T>?) {
        value ?: return
        writer.beginArray()
        for (element in value) {
            adapter.toJson(writer, element)
        }
        writer.endArray()
    }

    class Factory<T : Any> : JsonAdapter.Factory {
        override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
            val rawType = Types.getRawType(type)
            if (annotations.isNotEmpty()) return null
            return if (rawType == ArrayList::class.java) {
                newListAdapter<T>(type, moshi).nullSafe()
            } else null
        }

        private fun <T : Any> newListAdapter(type: Type, moshi: Moshi): JsonAdapter<ArrayList<T>> {
            val elementType = collectionElementType(type, ArrayList::class.java)
            val elementAdapter = moshi.adapter<T>(elementType)
            return ArrayListAdapter(elementAdapter)
        }
    }
}
