package com.kazukinakano.stampcard

import com.google.firebase.auth.FirebaseAuth

class DataRepository private constructor() {
    lateinit var auth: FirebaseAuth
    var email: String? = null
    var password: String? = null
    var numberOfVisits: Long = 0

    companion object {
        private var instance : DataRepository? = null

        fun  getInstance(): DataRepository {
            if (instance == null)
                instance = DataRepository()

            return instance!!
        }
    }
}