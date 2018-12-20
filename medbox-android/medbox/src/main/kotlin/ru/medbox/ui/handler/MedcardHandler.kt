package ru.medbox.ui.handler

import android.content.Context
import android.content.Intent
import ru.medbox.ui.activity.MainActivity
import ru.medbox.ui.activity.MedcardAddActivity

class MedcardHandler(val context: Context?) {

    fun onAddMedcardClick() {
        if (context is MainActivity) {
            context.startActivity(Intent(context, MedcardAddActivity::class.java))
        }
    }

    fun onSendMedcardClick() {

    }

}