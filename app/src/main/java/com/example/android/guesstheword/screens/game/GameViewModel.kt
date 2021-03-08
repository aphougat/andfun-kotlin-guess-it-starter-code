package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*


class GameViewModel: ViewModel() {

    companion object{

        private const val DONE = 0L

        private const val ONE_SECOND = 1000L

        private const val COUNTDOWN_TIME = 10000L
    }

    private val timer : CountDownTimer

    // The current score
    private val _score = MutableLiveData<Int>()
    private val _word = MutableLiveData<String>()
    private val _gameFinished = MutableLiveData<Boolean>()
    private val _timer = MutableLiveData<Long>()
    private val _buzz = MutableLiveData<BuzzType>()
    val score : LiveData<Int>
        get() = _score
    val word : LiveData<String>
        get() = _word
    val gameFinished: LiveData<Boolean>
        get() = _gameFinished
    val formattedTimer: LiveData<Long>
        get() = _timer
    val currentTime = Transformations.map(formattedTimer) { time ->
        DateUtils.formatElapsedTime(time)
    }
    val buzz: LiveData<BuzzType>
        get() = _buzz

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        Log.i("GameViewModel", "Called")
        _score.value = 0
        _gameFinished.value = false
        resetList()
        nextWord()
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
            override fun onTick(millisUntilFinished: Long) {
               _timer.value = millisUntilFinished/1000
            }

            override fun onFinish() {
                _gameFinished.value = true
            }
        }
        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Log.i("GameViewModel", "Clear Called")
    }

    /**
     * Moves to the next word in the list
     */
    @OptIn(ExperimentalStdlibApi::class)
    private fun nextWord() {
        //Select and remove a word from the list
        if(wordList.isEmpty())
        {
            resetList()
        }
        _word.value = wordList.removeFirst()

        //_buzz.value = BuzzType.NO_BUZZ

    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    fun onSkip()  {
        _buzz.value = BuzzType.COUNTDOWN_PANIC
        _score.value = (_score.value)?.dec()
        nextWord()
    }

    fun onCorrect() {
        _buzz.value = BuzzType.CORRECT
        _score.value  = _score.value?.inc()
        nextWord()
    }

    fun onGameEventFinished(){
        _buzz.value = BuzzType.GAME_OVER
        _gameFinished.value = false
    }

}