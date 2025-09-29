package ru.medbox.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.medcard_additional.view.*
import ru.medbox.R
import ru.medbox.api.Medcard
import ru.medbox.databinding.ActivityAddMedcardBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.viewmodel.MedcardViewModel

class MedcardAddActivity : BaseActivity() {
    private lateinit var binding: ActivityAddMedcardBinding
    private lateinit var viewModel: MedcardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(MedcardViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_medcard)
        binding.viewModel = viewModel
        val expandable = binding.expandable!!
        binding.btnExpandMedcard.setOnClickListener { if (expandable.visibility == View.GONE) expandable.visibility = View.VISIBLE else expandable.visibility = View.GONE }
        binding.inputBdayDay.adapter = createRangeAdapter(1, 31)
        binding.inputBdayMonth.adapter = createRangeAdapter(1, 12)
        binding.inputBdayYear.adapter = createRangeAdapter(1940, 2018)
        expandable.input_water.adapter = createRangeAdapter(0, 10)
        expandable.input_dream.adapter = createRangeAdapter(0, 24)
        expandable.input_sport.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Регулярно", "Периодически", "Редко", "Никогда"))

        binding.continueBtn.setOnClickListener {
            val data = mapOf(
                    binding.inputLastnameLay.hint!!.toString() to binding.inputLastname.text!!.toString(),
                    binding.inputMiddleNameLay.hint!!.toString() to binding.inputMiddleName.text!!.toString(),
                    binding.inputFirstNameLay.hint!!.toString() to binding.inputFirstName.text!!.toString(),
                    binding.inputTelLay.hint!!.toString() to binding.inputTel.text!!.toString(),
                    binding.keyBday.text.toString() to binding.inputBdayDay.selectedItem.toString() + "." + binding.inputBdayMonth.selectedItem.toString() + "." + binding.inputBdayYear.selectedItem.toString(),
                    expandable.input_height.hint.toString() to expandable.input_height.text.toString(),
                    expandable.input_weight.hint.toString() to expandable.input_weight.text.toString(),
                    expandable.key_water.text.toString() to expandable.input_water.selectedItem.toString(),
                    expandable.key_dream.text.toString() to expandable.input_dream.selectedItem.toString(),
                    expandable.key_sport.text.toString() to expandable.input_sport.selectedItem.toString(),
                    expandable.key_walk.text.toString() to expandable.input_walk_distance.text.toString(),
                    expandable.key_other_problems.text.toString() to expandable.input_other_problems.text.toString()
            )

            log(this, data.toString())

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            var medcard = Medcard()
            medcard.values = data
            var adapter:JsonAdapter<Medcard> = moshi.adapter(Medcard::class.java)
            val jsonStr = adapter.toJson(medcard)

            log(this, jsonStr)

            viewModel.data.value = jsonStr

        }
    }

    private fun createRangeAdapter(from: Int, to: Int): ArrayAdapter<String> {
        val values = Stream.rangeClosed(from, to).map { it.toString() }.collect(Collectors.toList())
        return ArrayAdapter(this, android.R.layout.simple_spinner_item, values)
    }
}