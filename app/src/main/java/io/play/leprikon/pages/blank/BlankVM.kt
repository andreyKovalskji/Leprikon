package io.play.leprikon.pages.blank

import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import io.play.leprikon.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class BlankVM: ViewModel() {
    private val images = listOf(
        R.drawable.slot_element_1, R.drawable.slot_element_2, R.drawable.slot_element_3,
        R.drawable.slot_element_4, R.drawable.slot_element_5, R.drawable.slot_element_6,
        R.drawable.slot_element_7, R.drawable.slot_element_8, R.drawable.slot_element_9)

    private var elements = List(25) { R.drawable.slot_element_h }
    private var pickedElements = mutableMapOf<Int, Int>()

    lateinit var gameStateStrings: List<String>

    val selectedBet = MutableStateFlow(10)
    val balance = MutableStateFlow(2000)
    val totalWin = MutableStateFlow(0)
    val gameStarted = MutableStateFlow(false)
    val gameState = MutableStateFlow("")

    fun doTry(slots: List<AppCompatImageView>) {
        if(balance.value < selectedBet.value) return
        balance.update { it - selectedBet.value }
        slots.forEach {
            it.setImageResource(R.drawable.slot_element_h)
        }
        val tempElements = possibleElements.toMutableList()
        val resultList = mutableListOf<Int>()
        var randomIndex: Int
        for(i in 0..<tempElements.size) {
            randomIndex = Random.nextInt(0, tempElements.size)
            resultList.add(tempElements[randomIndex])
            tempElements.removeAt(randomIndex)
        }
        elements = resultList
        gameStarted.update { true }
        gameState.update { gameStateStrings[2] }
        totalWin.update { 0 }
        pickedElements = mutableMapOf()
        Log.i("Blank", "Game started")
    }

    fun checkElement(index: Int): Int {
        val pickedIndex = elements[index]
        val pickedElement = pickedElements[pickedIndex]
        /*
        <array name="game_states">
            <item>Tap on \"Try\" button</item> 0
            <item>Tap on \"Try again\" button</item> 1
            <item>Game in process.</item> 2
            <item>You win!</item> 3
            <item>You lose…</item> 4
            <item>Game over!</item> 5
        </array>
         */
        if(pickedElement != null) {
            val nextValue = pickedElement + 1
            pickedElements[pickedIndex] = nextValue
            if(nextValue == 3) {
                val value = (getElementValue(pickedIndex) * betMultiplier()).toInt()
                if(value == 0) {
                    gameStarted.update { false }
                    Log.i("Blank", "Game over")
                    if(totalWin.value != 0) {
                        gameState.update { gameStateStrings[5] }
                    }
                    else {
                        gameState.update { gameStateStrings[4] }
                    }
                }
                else {
                    totalWin.update { it + value }
                    balance.update { it + value }
                    if(totalWin.value == (3850 * betMultiplier()).toInt()) {
                        gameState.update { gameStateStrings[3] }
                        gameStarted.update { false }
                    }
                    Log.i("Blank", "Win $value")
                }
            }
        }
        else {
            pickedElements[pickedIndex] = 1
        }
        return pickedIndex
    }

    private fun betMultiplier() = selectedBet.value.toDouble() / 10.0

    private fun getElementValue(element: Int) = when(element) {
        R.drawable.slot_element_1 -> 100
        R.drawable.slot_element_2 -> 250
        R.drawable.slot_element_3 -> 500
        R.drawable.slot_element_4 -> 750
        R.drawable.slot_element_8 -> 1000
        R.drawable.slot_element_9 -> 1250
        else -> 0
    }

    companion object {
        private val possibleElements = listOf(
            R.drawable.slot_element_1, // 1
            R.drawable.slot_element_1, // 2
            R.drawable.slot_element_1, // 3
            R.drawable.slot_element_2, // 4
            R.drawable.slot_element_2, // 5
            R.drawable.slot_element_2, // 6
            R.drawable.slot_element_3, // 7
            R.drawable.slot_element_3, // 8
            R.drawable.slot_element_3, // 9
            R.drawable.slot_element_4, // 10
            R.drawable.slot_element_4, // 11
            R.drawable.slot_element_4, // 12
            R.drawable.slot_element_8, // 13
            R.drawable.slot_element_8, // 14
            R.drawable.slot_element_8, // 15
            R.drawable.slot_element_9, // 16
            R.drawable.slot_element_9, // 17
            R.drawable.slot_element_9, // 18
            R.drawable.slot_element_c, // 19
            R.drawable.slot_element_c, // 20
            R.drawable.slot_element_c, // 21
            R.drawable.slot_element_c, // 22
            R.drawable.slot_element_c, // 23
            R.drawable.slot_element_c, // 24
            R.drawable.slot_element_c  // 25
        )
    }
}