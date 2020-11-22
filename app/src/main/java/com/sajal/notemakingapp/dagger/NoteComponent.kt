package com.sajal.notemakingapp.dagger

import com.sajal.notemakingapp.CreateNote
import com.sajal.notemakingapp.HomeActivity
import dagger.Component

@Component(modules = [FirebaseModule::class])
interface NoteComponent {

    fun injectHomeActivity(homeActivity: HomeActivity)

    fun injectCreateNote(createNote: CreateNote)
}