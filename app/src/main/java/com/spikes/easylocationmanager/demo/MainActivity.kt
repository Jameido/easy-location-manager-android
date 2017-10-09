/*
 * Copyright 2017.  Luca Rossi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.spikes.easylocationmanager.demo

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.spikes.easylocationmanager.ActivityEasyLocationManager
import com.spikes.easylocationmanager.EasyLocationManager

class MainActivity : AppCompatActivity(), EasyLocationManager.OnLocationChangedListener {

    lateinit var mEasyLocationManager : ActivityEasyLocationManager

    override fun onLocationChanged(location: Location?) {
        TODO("use new location")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mEasyLocationManager = ActivityEasyLocationManager(this)
        mEasyLocationManager.setOnLocationChangedListener(this)
        mEasyLocationManager.requestLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mEasyLocationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        mEasyLocationManager.onDestroy()
        super.onDestroy()
    }
}
