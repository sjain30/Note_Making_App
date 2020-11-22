package com.sajal.notemakingapp.dagger

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    fun provideDatabaseReference() : DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("notes/" + FirebaseAuth.getInstance().currentUser!!.uid)
    }
}