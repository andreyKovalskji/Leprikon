package io.play.leprikon.pages.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import io.play.leprikon.R
import io.play.leprikon.pages.blank.BlankPage
import io.play.leprikon.pages.slots.SlotsPage

class GamesPage: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.page_games, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
            val openSlotsCallback: (View) -> Unit = {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.pages, SlotsPage())
                    .commit()
            }
            val openBlankCallback: (View) -> Unit = {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.pages, BlankPage())
                    .commit()
            }
            findViewById<AppCompatImageButton>(R.id.openSlotsPageButton).setOnClickListener(openSlotsCallback)
            findViewById<TextView>(R.id.openSlotsPageText).setOnClickListener(openSlotsCallback)

            findViewById<AppCompatImageButton>(R.id.openBlankPageButton).setOnClickListener(openBlankCallback)
            findViewById<TextView>(R.id.openBlankPageText).setOnClickListener(openBlankCallback)
        }
    }
}