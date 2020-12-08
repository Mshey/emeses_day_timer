package com.example.mysezzytimer

import android.content.Context
import android.media.MediaPlayer
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_sweaty_shredder.*
import kotlinx.android.synthetic.main.timer_counter_layout.*
import kotlinx.android.synthetic.main.timer_counter_layout.view.*

class SweatyShredderFragment : Fragment() {

    private val onTime = 30
    private val roundOffTime = 10
    private val circuitOffTime = 30
    private val roundNr = 6
    private var roundNrCountdown = 1
    private val circuitNr = 7
    private var circuitNrCountDown = 1

    enum class TimerState {
        Stopped, Paused, Resting, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerState = TimerState.Stopped
    private var previousTimerState = TimerState.Stopped

    private var timerLengthSeconds: Long = 0
    private var secondsRemaining: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sweaty_shredder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateButtons()
        round_numbers_text_view.text = "$roundNrCountdown/$roundNr"
        exercise_numbers_text_view.text = "$circuitNrCountDown/$circuitNr"
    }

    private fun playSound() {

        val resId = resources.getIdentifier(
            R.raw.simple_notification.toString(),
            "raw", activity?.packageName
        )

        val mediaPlayer = MediaPlayer.create(activity, resId)
        mediaPlayer.start()
    }

    private fun vibrate() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun onTimerFinished() {
        vibrate()
        playSound()
        when (timerState) {
            TimerState.Running -> {
                previousTimerState = timerState
                timerState = TimerState.Resting
                action_text_view.text = resources.getString(R.string.rest)
                if (roundNrCountdown == roundNr) {
                    secondsRemaining = circuitOffTime.toLong()
                    timerLengthSeconds = circuitOffTime.toLong()
                } else {
                    secondsRemaining = roundOffTime.toLong()
                    timerLengthSeconds = roundOffTime.toLong()
                }
            }
            TimerState.Stopped -> {
                previousTimerState = timerState
                timerState = TimerState.Running
                action_text_view.text = resources.getString(R.string.go)
                secondsRemaining = onTime.toLong()
                timerLengthSeconds = onTime.toLong()
            }
            TimerState.Paused -> {
                timerState = previousTimerState
            }
            TimerState.Resting -> {
                if (roundNrCountdown == roundNr) {
                    if (circuitNrCountDown == circuitNr) {
                        Toast.makeText(context, "You are done!", Toast.LENGTH_LONG).show()
                        timerState = TimerState.Stopped
                        Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                            .popBackStack()
                    } else {
                        circuitNrCountDown++
                    }
                    roundNrCountdown = 1
                } else {
                    roundNrCountdown++
                }
                previousTimerState = timerState
                timerState = TimerState.Running
                action_text_view.text = resources.getString(R.string.go)
                round_numbers_text_view.text = "$roundNrCountdown/$roundNr"
                exercise_numbers_text_view.text = "$circuitNrCountDown/$circuitNr"
                secondsRemaining = onTime.toLong()
                timerLengthSeconds = onTime.toLong()
            }
        }
        progress_countdown.max = timerLengthSeconds.toInt()
        progress_countdown.progress = 0

        updateCountdownUI()
        startTimer()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        if (countdown_text_vew != null && progress_countdown != null) {
            countdown_text_vew.text =
                "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
            progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun updateButtons() {
        included_timer_counter_layout.action_image_view.setOnClickListener {
            when (timerState) {
                TimerState.Running -> {
                    action_image_view.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    timer.cancel()
                    timerState = TimerState.Paused
                }
                TimerState.Stopped -> {
                    action_image_view.setImageResource(R.drawable.ic_baseline_pause_24)
                    startTimer()
                    timerState = TimerState.Running
                }
                TimerState.Paused -> {
                    action_image_view.setImageResource(R.drawable.ic_baseline_pause_24)
                    startTimer()
                }
                TimerState.Resting -> {
                    action_image_view.setImageResource(R.drawable.ic_baseline_pause_24)
                    timer.cancel()
                    timerState = TimerState.Paused
                }
            }
        }
    }

}