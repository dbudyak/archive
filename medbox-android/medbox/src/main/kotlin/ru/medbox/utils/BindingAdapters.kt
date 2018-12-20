package ru.medbox.utils

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.BindingAdapter
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import ru.medbox.R
import ru.medbox.utils.extension.getParentActivity

@BindingAdapter("adapter")
fun setAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    view.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
    view.adapter = adapter
}

@BindingAdapter("mutableVisibility")
fun setMutableVisibility(view: View, visibility: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && visibility != null) {
        visibility.observe(parentActivity, Observer(function = fun(value: Int?) {
            view.visibility = value ?: View.VISIBLE
        }))
    }
}

@BindingAdapter("mutableText")
fun setMutableText(view: TextView, text: MutableLiveData<String>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && text != null) {
        text.observe(parentActivity, Observer { value -> view.text = value ?: "" })
    }
}

@BindingAdapter("imageUrl")
fun setPicassoUrl(view: ImageView, url: MutableLiveData<String>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && url != null) {
        url.observe(parentActivity, Observer { value -> if (value?.trim()?.length != 0) Picasso.get().load(value).into(view) })
    }
}

@BindingAdapter("needBack")
fun setTitleStyle(view: TextView, needBack: Boolean) {
    if (needBack) {
        view.textSize = 16f
        view.setTextColor(ContextCompat.getColor(view.context, R.color.title_small_color))
    } else {
        view.textSize = 24f
        view.setTextColor(ContextCompat.getColor(view.context, R.color.title_large_color))
        view.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        view.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)

    }
}