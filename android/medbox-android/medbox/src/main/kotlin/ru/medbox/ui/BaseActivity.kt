package ru.medbox.ui

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import ru.medbox.R
import ru.medbox.utils.Loggable

abstract class BaseActivity : AppCompatActivity(), Loggable {

    private var errorSnackbar: Snackbar? = null

    protected fun showError(rootView: View, errorClickListener: View.OnClickListener, errorMessage: String) {
        errorSnackbar = Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.setAction(R.string.retry, errorClickListener)
        errorSnackbar?.show()
    }

    protected fun hideError() {
        errorSnackbar?.dismiss()
    }
}