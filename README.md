# Android Animated  SearchView

### Implementation
add `maven { url 'https://jitpack.io' }` to build.gradle in your project:
``` groovy
allprojects {
    repositories {
		...
        maven { url 'https://jitpack.io' }
    }
}
```
and in your app.gradle:
``` groovy
	dependencies {
			...
		    implementation 'com.github.aghiadodeh:xsearchview:1.0.3'
	}
```


### Overview
![](https://s9.gifyu.com/images/ezgif-7-167945117de9.gif)

## 

### Usage
                    
Attribute  | format | Default Value
------------- | -------------
search_view_opened  | boolean | false
search_view_hide_close_icon  | boolean  | false
search_view_debounce_duration  | integer | 200 (ms)
search_view_open_on_click  | boolean  | true
search_view_input_color  | color | #4A959595
search_view_icons_color  | color  | #555555

in `your_activity.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/topAppBar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="@color/light_grey">

            <com.aghiadodeh.xsearchview.SearchView
                android:id="@+id/searchView"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:search_view_debounce_duration="300"
                app:search_view_opened="true"
                app:search_view_icons_color="@color/black"
                app:search_view_input_color="@color/white"/>

        </com.google.android.material.appbar.MaterialToolbar>

    </com.google.android.material.appbar.AppBarLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
```

in `YourActivity.kt`:
```kotlin

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
        searchView.initActivity(this)

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

        // listen when open/close searchView
        searchView.initToggleListener {
            Log.d("initToggleListener", "$it") // true or false
        }

        // for any edit (styling or set listeners)
        searchView.getEditText()
    }
}
```