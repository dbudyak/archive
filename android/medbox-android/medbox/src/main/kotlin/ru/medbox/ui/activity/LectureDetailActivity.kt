package ru.medbox.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.view.View
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import ru.medbox.R
import ru.medbox.databinding.ActivityLectureBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.viewmodel.LectureDetailViewModel
import ru.medbox.utils.LECTURE_KEY

class LectureDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityLectureBinding
    private lateinit var viewModel: LectureDetailViewModel
    private lateinit var videoId: String

    private var errorSnackbar: Snackbar? = null

    private val mainHandler = Handler()
    private val bandwidthMeter = DefaultBandwidthMeter()
    private val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
    private val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(hashMapOf(LECTURE_KEY to intent.extras?.get(LECTURE_KEY)))).get(LectureDetailViewModel::class.java)
        viewModel.videoUrl.observe(this, Observer { it ->
            when {
                it == null -> {
                    binding.playerView.visibility = View.GONE
                }
                it.startsWith("https://www.youtube.com/watch") or
                        it.startsWith("https://youtube.com/watch") -> {
                }
                else -> {
                    binding.playerView.visibility = View.VISIBLE
                    val videoSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, "medbox-user-agent", bandwidthMeter))
                            .createMediaSource(Uri.parse(it))
                    player.prepare(videoSource)
                    binding.play.setOnClickListener {
                        player.playWhenReady = !player.playWhenReady
                        binding.play.visibility = View.GONE
                    }
                }
            }
        })

        viewModel.errorMessage.observe(this, Observer {
            if (it != null) showError(binding.root, viewModel.errorClickListener, it) else hideError()
        })

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lecture)
        binding.viewModel = viewModel
        binding.playerView.player = player
        binding.btnBack.setOnClickListener { finish() }
    }
}
