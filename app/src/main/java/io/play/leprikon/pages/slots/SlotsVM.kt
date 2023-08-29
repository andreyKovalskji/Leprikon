package io.play.leprikon.pages.slots

import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import io.play.leprikon.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class SlotsVM: ViewModel() {
    private val images = listOf(
        R.drawable.slot_element_1, R.drawable.slot_element_2, R.drawable.slot_element_3,
        R.drawable.slot_element_4, R.drawable.slot_element_5, R.drawable.slot_element_6,
        R.drawable.slot_element_7, R.drawable.slot_element_8, R.drawable.slot_element_9)

    val selectedBet = MutableStateFlow(10)
    val balance = MutableStateFlow(2000)

    suspend fun doSpin(slots: List<AppCompatImageView>) = coroutineScope {
        if(balance.value < selectedBet.value) return@coroutineScope
        balance.update {it - selectedBet.value}
        var slotsImagesList = images + images + images.take(2)
        var columnsState = listOf(1, 1, 1, 1, 1)
        launch {
            delay(3000)
            for(i in 0..4){
                columnsState = List(i+1) {0} + columnsState.drop(i+1)
                if(i != 4) {
                    delay(500)
                }
            }
        }
        while(!columnsState.all {it == 0}) {
            slotsImagesList = changeSlotsImage(slots, columnsState, slotsImagesList)
            delay(100)
        }
//        slotsImagesList = slotsImagesList.dropLast(5) + List(5) { images.last() }
//        for((index, slot) in slots.withIndex()) {
//            slot.setImageResource(slotsImagesList[index])
//        }
        check(slotsImagesList)
    }

    private fun changeSlotsImage(slots: List<AppCompatImageView>, columnsState: List<Int>, slotsImagesList: List<Int>): List<Int> {
        var newSlotsImagesList = List(5) { images.random() } + slotsImagesList.dropLast(5)
        if(columnsState.any { it == 0 }) {
            val columns1 = getColumns(slotsImagesList)
            val columns2 = getColumns(newSlotsImagesList)
            val t = mutableListOf<List<Int>>()
            for(i in 0..4) {
                t.add(
                    if(columnsState[i] == 0) columns1[i] else columns2[i]
                )
            }
            newSlotsImagesList = getFromColumns(t)
        }
        for((index, slot) in slots.withIndex()) {
            slot.setImageResource(newSlotsImagesList[index])
        }
        return newSlotsImagesList
    }
    private fun check(slotsImagesList: List<Int>) {
        Log.i("Check", "slotsImagesList: $slotsImagesList")
        val columns = getColumns(slotsImagesList).map {
            it.distinct()
        }
        var result = 0
        Log.i("Check", "First column: ${columns[0]}")
        for(i in columns[0].indices) {
            for((index, column) in columns.drop(1).withIndex()) {
                Log.i("Check", "${index + 2} column: $column")
                if(column.contains(columns[0][i])) {
                    Log.i("Check", "Contains")
                    if(index == columns.size - 2) {
                        Log.i("Check", "Win")
                        result += resultByImage(columns[0][i])
                    }
                }
                else {
                    Log.i("Check", "NOT contains")
                    break
                }
            }
        }
        Log.i("Result", "Result: $result")
        balance.update { it + result }
        Log.i("Result", "Balance: ${balance.value}")
    }

    private fun resultByImage(image: Int) = (when(image) {
        images[0] -> 50
        images[1] -> 100
        images[2] -> 200
        images[3] -> 300
        images[4] -> 5000
        images[5] -> 5000
        images[6] -> Random.nextInt(50, 5000)
        images[7] -> 1000
        images[8] -> 2000
        else -> 0
    } * betMultiplier()).toInt()

    private fun betMultiplier() = selectedBet.value.toDouble() / 10.0

    private fun getColumns(list: List<Int>): List<List<Int>> {
        return List(5) {
            listOf(list[it], list[it+5],
                list[it+10], list[it+15])
        }
    }
    private fun getFromColumns(list: List<List<Int>>): List<Int> {
        val t = mutableListOf<Int>()

        for(i in 0..3) {
            for(j in 0..4) {
                t.add(list[j][i])
            }
        }
        return t
    }
}