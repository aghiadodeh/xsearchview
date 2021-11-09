package com.aghiadodeh.xsearchview

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.aghiadodeh.xsearchview.databinding.ViewSearchBinding
import java.util.*
import android.graphics.PorterDuff
import androidx.core.graphics.drawable.DrawableCompat

import android.graphics.drawable.Drawable


class SearchView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private var activity: Activity? = null
    private var binding: ViewSearchBinding = ViewSearchBinding.inflate(LayoutInflater.from(context), this, true)

    private var inputColor: Int = ContextCompat.getColor(context, R.color.search_view_input_background)
    private var iconsColor: Int = ContextCompat.getColor(context, R.color.search_view_icons_color)

    companion object {
        private var searchToggleListener: OnSearchToggleListener? = null
        private var onSearchListener: OnSearchListener? = null
        private var openOnClick: Boolean = true
        private var debounceDuration: Int = 200
    }

    init {
        binding.openSearchButton.setOnClickListener { openSearch() }
        binding.closeSearchButton.setOnClickListener { closeSearch() }
        initAttributes(attrs)
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SearchView, 0, 0)
        array.getBoolean(R.styleable.SearchView_search_view_opened, false).let {
            if (it) {
                binding.searchOpenView.visibility = View.VISIBLE
                binding.openSearchButton.visibility = View.INVISIBLE
                binding.searchInputText.post { requestFocus() }
            }
        }
        array.getBoolean(R.styleable.SearchView_search_view_hide_close_icon, false).let {
            binding.closeSearchButton.visibility = if (!it) VISIBLE else GONE
        }
        array.getInteger(R.styleable.SearchView_search_view_debounce_duration, debounceDuration).let {
            debounceDuration = it
        }
        array.getBoolean(R.styleable.SearchView_search_view_open_on_click, true).let {
            openOnClick = it
        }
        array.getColor(R.styleable.SearchView_search_view_input_color, inputColor).let {
            inputColor = it
            binding.searchInputCard.setCardBackgroundColor(inputColor)
        }
        array.getColor(R.styleable.SearchView_search_view_icons_color, iconsColor).let {
            iconsColor = it
            binding.openSearchButton.setColorFilter(iconsColor, PorterDuff.Mode.SRC_IN)
            binding.closeSearchButton.setColorFilter(iconsColor, PorterDuff.Mode.SRC_IN)
            setTextViewDrawableColor(binding.searchInputText)
        }
        array.recycle()
    }

    fun initActivity(activity: Activity) {
        this.activity = activity
    }

    fun initToggleListener(mSearchToggleListener: OnSearchToggleListener) {
        searchToggleListener = mSearchToggleListener
    }

    fun openOnClick(open: Boolean) {
        openOnClick = open
    }

    fun getEditText(): EditText {
        return binding.searchInputText
    }

    fun openSearch() {
        if (!openOnClick) {
            searchToggleListener?.toggle(opened = true)
            return
        }
        binding.searchInputText.removeTextChangedListener(textChangeListener)
        binding.searchInputText.setText("")
        binding.searchInputText.addTextChangedListener(textChangeListener)
        binding.searchOpenView.visibility = View.VISIBLE
        binding.openSearchButton.visibility = View.INVISIBLE
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            binding.searchOpenView,
            (binding.openSearchButton.right + binding.openSearchButton.left) / 2,
            (binding.openSearchButton.top + binding.openSearchButton.bottom) / 2,
            0f,
            width.toFloat()
        )
        circularReveal.duration = 300
        circularReveal.start()
        binding.searchInputText.requestFocus()
        searchToggleListener?.toggle(opened = true)
        activity?.apply {
            runOnUiThread { showSoftKeyboard(this) }
        }
    }

    fun closeSearch() {
        if (binding.openSearchButton.visibility == View.VISIBLE) return
        activity?.apply { runOnUiThread { hideSoftKeyboard(this) } }
        val circularConceal = ViewAnimationUtils.createCircularReveal(
            binding.searchOpenView,
            (binding.openSearchButton.right + binding.openSearchButton.left) / 2,
            (binding.openSearchButton.top + binding.openSearchButton.bottom) / 2,
            width.toFloat(),
            0f
        )
        binding.searchInputText.clearFocus()
        circularConceal.duration = 300
        circularConceal.start()
        circularConceal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?) {
                binding.openSearchButton.visibility = View.VISIBLE
                binding.searchOpenView.visibility = View.INVISIBLE
                circularConceal.removeAllListeners()
                searchToggleListener?.toggle(opened = false)
            }
        })
    }

    private fun hideSoftKeyboard(activity: Activity) {
        activity.currentFocus?.let {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    @Suppress("DEPRECATION")
    private fun showSoftKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun setOnApplySearchListener(onSearchApplyListener: OnSearchApplyListener) {
        binding.searchInputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchApplyListener.applySearch(binding.searchInputText.text.toString().trim())
                closeSearch()
            }
            true
        }
    }

    fun setOnSearchListener(onSearchListener: OnSearchListener) {
        SearchView.onSearchListener = onSearchListener
        binding.searchInputText.addTextChangedListener(textChangeListener)
    }

    private val textChangeListener = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        private var timer: Timer = Timer()
        override fun afterTextChanged(editable: Editable) {
            timer.cancel()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    onSearchListener?.textChanges(editable.toString())
                }
            }, debounceDuration.toLong())
        }
    }

    private fun setTextViewDrawableColor(textView: EditText) {
        val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_search) //Your drawable image
        drawable?.let {
            val d = DrawableCompat.wrap(it)
            DrawableCompat.setTint(d, iconsColor)
            DrawableCompat.setTintMode(d, PorterDuff.Mode.SRC_IN)
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }


    fun interface OnSearchListener {
        fun textChanges(text: String)
    }

    fun interface OnSearchApplyListener {
        fun applySearch(text: String)
    }

    fun interface OnSearchToggleListener {
        fun toggle(opened: Boolean)
    }
}