package com.ae.app1

import com.google.firebase.firestore.Exclude
import java.io.Serializable

/**
 * Item: A data class is a class that holds data (not doing any special operations)
 */
data class Item(
    var name: String,
    var postal: String,
    var phone: String,
    var temperature: String,
    var url: String,
) : Serializable {

    /**
     * @Exclude: We do not want to save the id as part of the document
     */
    @Exclude
    var id: String = ""

    /**
     * Empty constructor needed by Firebase!!!
     *
     * Firebase needs an empty constructor to create Item objects
     * from the documents in the database
     */
    constructor() : this(name = "", postal = "", phone = "", temperature = "", url = "")
}