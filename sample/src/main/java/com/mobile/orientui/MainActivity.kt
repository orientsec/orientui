package com.mobile.orientui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mobile.orientui.pinnedrecyclerview.PinnedRVActivity
import com.mobile.orientui.rankinggroup.RankingGroupActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun btnClickEvent(v: View) {
        val intent = when (v.id) {
            R.id.button -> {
                Intent(this, PinnedRVActivity::class.java)
            }
            R.id.button2 -> {
                Intent(this, RankingGroupActivity::class.java)
            }
            R.id.button3 -> {
                Intent(this, PinnedRVActivity::class.java)
            }
            else -> {
                Intent(this, PinnedRVActivity::class.java)
            }
        }

        startActivity(intent)
    }

}
