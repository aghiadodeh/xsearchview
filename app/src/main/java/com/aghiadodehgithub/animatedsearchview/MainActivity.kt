package com.aghiadodehgithub.animatedsearchview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aghiadodeh.xsearchview.SearchView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var open: MaterialButton
    private lateinit var enable: MaterialButton
    private lateinit var disable: MaterialButton
    private lateinit var close: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchView = findViewById(R.id.searchView)
        disable = findViewById(R.id.disable)
        enable = findViewById(R.id.enable)
        close = findViewById(R.id.close)
        open = findViewById(R.id.open)

        // for (open/hide) soft keyboard when focus/unFocus the searchView
        searchView.initActivity(activity = this)

        // open searchView
        open.setOnClickListener { searchView.openSearch() }

        // close searchView
        close.setOnClickListener { searchView.closeSearch() }

        // enable open searchView when click on search icon
        enable.setOnClickListener { searchView.openOnClick(true) }

        // disable open searchView when click on search icon
        disable.setOnClickListener { searchView.openOnClick(false) }

        // listen when user apply search
        searchView.setOnApplySearchListener {
            Log.d("ApplySearchListener", it)
        }

        // listen when editText value changed
        searchView.setOnSearchListener {
            Log.d("SearchListener", it)
        }
    }
}