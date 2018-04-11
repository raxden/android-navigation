/*
 * Copyright 2014 Ángel Gómez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.raxdenstudios.navigation

import android.app.Activity
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import kotlin.reflect.KClass

class NavigationManager(builder: Builder) {

    private val activity: Activity
    private val intentList: MutableList<Intent>
    private val requestCode: Int
    private val transitions: IntArray
    private val options: Bundle?

    init {
        activity = builder.activity
        intentList = builder.intentList
        requestCode = builder.requestCode
        transitions = builder.transitions
        options = builder.options

        for (intent in intentList) {
            intent.addFlags(builder.flags)
            if (intent.extras != null)
                intent.extras.putAll(builder.extras)
            else
                intent.putExtras(builder.extras)
        }
    }

    private fun launchActivity(intent: Intent, options: Bundle?) {
        if (hasRequestCode())
            activity.startActivityForResult(intent, requestCode, options)
        else
            activity.startActivity(intent)
        overridePendingTransitions()
    }

    private fun launchActivityStack(intentList: List<Intent>, options: Bundle?) {
        val stackBuilder = TaskStackBuilder.create(activity)
        for (intent in intentList)
            stackBuilder.addNextIntentWithParentStack(intent)
        stackBuilder.startActivities(options)
    }

    private fun overridePendingTransitions() {
        if (hasTransitions()) activity.overridePendingTransition(transitions[0], transitions[1])
    }

    private fun hasTransitions() = transitions.size == 2

    private fun hasRequestCode() = requestCode > 0

    class Launcher(val navigationManager: NavigationManager) {

        fun launch() {
            navigationManager.launchActivity(navigationManager.intentList.first(), navigationManager.options)
        }

        fun launchAndFinish() {
            navigationManager.launchActivity(navigationManager.intentList.first(), navigationManager.options)
            navigationManager.activity.finish()
        }
    }

    class StackLauncher(val navigationManager: NavigationManager) {

        fun launch() {
            navigationManager.launchActivityStack(navigationManager.intentList, navigationManager.options)
        }
    }

    class Builder(val activity: Activity) {

        internal var intentList: MutableList<Intent> = mutableListOf()
        internal var extras: Bundle = Bundle()
        internal var options: Bundle? = null
        internal var flags: Int = 0
        internal var requestCode: Int = 0
        internal val transitions = intArrayOf(android.R.anim.fade_in, android.R.anim.fade_out)

        fun putData(extras: Bundle): Builder {
            this.extras = extras
            return this
        }

        fun putData(data: Parcelable): Builder {
            this.extras.putParcelable(data.javaClass.simpleName, data)
            return this
        }

        fun putData(data: Serializable): Builder {
            this.extras.putSerializable(data.javaClass.simpleName, data)
            return this
        }

        fun putData(data: Map<String, Parcelable>): Builder {
            for ((key, value) in data)
                extras.putParcelable(key, value)
            return this
        }

        fun setFlags(flags: Int): Builder {
            this.flags = flags
            return this
        }

        fun setRequestCode(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        fun setTransition(transitionIn: Int, transitionOut: Int): Builder {
            this.transitions[0] = transitionIn
            this.transitions[1] = transitionOut
            return this
        }

        // ============== Class ====================================================================

        fun navigateToClass(classToStartIntent: Class<*>, options: Bundle = Bundle()): Launcher {
            intentList.add(Intent().setClass(activity, classToStartIntent))
            this.options = options
            return Launcher(NavigationManager(this))
        }

        fun navigateToClass(classToStartIntentList: List<Class<*>>, options: Bundle = Bundle()): StackLauncher {
            for (Class in classToStartIntentList)
                intentList.add(Intent().setClass(activity, Class))
            this.options = options
            return StackLauncher(NavigationManager(this))
        }

        // ============== KClass ===================================================================

        fun navigateToKClass(classToStartIntent: KClass<*>, options: Bundle = Bundle()): Launcher {
            intentList.add(Intent().setClass(activity, classToStartIntent.java))
            this.options = options
            return Launcher(NavigationManager(this))
        }

        fun navigateToKClass(classToStartIntentList: List<KClass<*>>, options: Bundle = Bundle()): StackLauncher {
            for (Class in classToStartIntentList)
                intentList.add(Intent().setClass(activity, Class.java))
            this.options = options
            return StackLauncher(NavigationManager(this))
        }

        // ============== Intent ===================================================================

        fun navigateToIntent(intent: Intent, options: Bundle = Bundle()): Launcher {
            intentList.add(intent)
            this.options = options
            return Launcher(NavigationManager(this))
        }

        fun navigateToIntent(intentList: List<Intent>, options: Bundle = Bundle()): StackLauncher {
            this.intentList.addAll(intentList)
            this.options = options
            return StackLauncher(NavigationManager(this))
        }

    }

}
