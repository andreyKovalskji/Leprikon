package io.play.leprikon.pages.slots

import android.graphics.Color
import android.os.Bundle
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

class SlotsPage: Fragment() {
    private val vm = SlotsVM()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.page_slots, container, false)

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
            val spinButton = view.findViewById<AppCompatButton>(R.id.spinButton)
            val slots = view.findViewById<GridLayout>(R.id.slots).allViews.filter {it is AppCompatImageView}
                .map { it as AppCompatImageView }.toList()
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
            for((ind, bet) in bets.withIndex()) {
                bet.setOnClickListener {
                    if(spinButton.isEnabled) {
                        vm.selectedBet.value = values[ind]
                    }
                }
            }
            spinButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    it.isEnabled = false
                    vm.doSpin(slots)
                    it.isEnabled = true
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
        }
    }
}