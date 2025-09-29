package ru.medbox.ui.handler

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.medbox.db.model.Doctor
import ru.medbox.db.model.Specialization
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.activity.DoctorDetailActivity
import ru.medbox.ui.activity.DoctorsActivity
import ru.medbox.ui.activity.PregnancyPlanActivity
import ru.medbox.utils.DOCTOR_KEY
import ru.medbox.utils.SPECIALIZATION_KEY
import ru.medbox.utils.SPECIALIZATION_NAME

class MainEventHandler(val context: Context?) {

    fun onPlanningClick() {
        if (context is BaseActivity) {
            context.startActivity(Intent(context, PregnancyPlanActivity::class.java))
        }
    }

    fun onPregnancyClick() {

    }

    fun onMotherClick() {

    }

    fun onSpecializationClick(specialization: Specialization) {
        if (context is BaseActivity) {
            val bundle = Bundle()
            bundle.putInt(SPECIALIZATION_KEY, specialization.id)
            bundle.putString(SPECIALIZATION_NAME, specialization.name)

            val intent = Intent(context, DoctorsActivity().javaClass)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    fun onDoctorClick(doctor: Doctor) {
        if (context is BaseActivity) {
            val bundle = Bundle()
            bundle.putInt(DOCTOR_KEY, doctor.id)
            bundle.putInt(SPECIALIZATION_KEY, doctor.specializationId)

            val intent = Intent(context, DoctorDetailActivity().javaClass)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

}