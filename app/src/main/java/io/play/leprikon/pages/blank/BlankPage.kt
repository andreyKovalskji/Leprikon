package io.play.leprikon.pages.blank

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.play.leprikon.R
import io.play.leprikon.pages.games.GamesPage
import kotlinx.coroutines.launch

class BlankPage: Fragment() {
    private val vm = BlankVM()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.page_blank, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
            findViewById<AppCompatImageButton>(R.id.leaveGameButton).setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.pages, GamesPage())
                    .commit()
            }
            val bet10 = findViewById<AppCompatButton>(R.id.bet10Button)
            val bet25 = findViewById<AppCompatButton>(R.id.bet25Button)
            val bet50 = findViewById<AppCompatButton>(R.id.bet50Button)
            val bet100 = findViewById<AppCompatButton>(R.id.bet100Button)
            val bets = listOf(bet10, bet25, bet50, bet100)
            val values = listOf(10, 25, 50, 100)
            val tryButton = view.findViewById<AppCompatButton>(R.id.tryButton)
            val elements = view.findViewById<GridLayout>(R.id.elements).allViews.filter {it is AppCompatImageView}
                .map { it as AppCompatImageView }.toList()
            for((index, element) in elements.withIndex()) {
                element.setOnClickListener {
                    if(vm.gameStarted.value) {
                        it as AppCompatImageView
                        it.setImageResource(vm.checkElement(index))
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.selectedBet.collect {
                        for(i in 0..3) {
                            if(it == values[i]) {
                                when (it) {
                                    10 -> bets[i].setBackgroundResource(R.drawable.selected_bet_10_background)
                                    100 -> bets[i].setBackgroundResource(R.drawable.selected_bet_100_background)
                                    else -> bets[i].setBackgroundResource(R.drawable.selected_bet_background)
                                }
                                bets[i].setTextColor(Color.BLACK)
                            }
                            else {
                                bets[i].setBackgroundColor(0x00000000)
                                bets[i].setTextColor(Color.WHITE)
                            }
                        }
                    }
                }
            }
            val totalWin = view.findViewById<TextView>(R.id.totalWinText)
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.totalWin.collect {
                        totalWin.text = getString(R.string.total_win_text, it)
                    }
                }
            }
            for((ind, bet) in bets.withIndex()) {
                bet.setOnClickListener {
                    if(tryButton.isEnabled) {
                        vm.selectedBet.value = values[ind]
                    }
                }
            }
            tryButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    it.isEnabled = false
                    vm.doTry(elements)
                }
            }
            val gameState = view.findViewById<TextView>(R.id.gameState)
            vm.gameStateStrings = resources.getStringArray(R.array.game_states).toList()
            vm.gameState.value = vm.gameStateStrings[0]

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.gameState.collect {
                        gameState.text = it
                    }
                }
            }
            val balance = view.findViewById<TextView>(R.id.balanceText)
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.balance.collect {
                        balance.text = getString(R.string.balance, it)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.gameStarted.collect {
                        if(!it) {
                            tryButton.isEnabled = true
                            Log.i("Blank", "Try button is enabled.")
                        }
                    }
                }
            }
        }
    }
}