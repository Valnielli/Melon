/*
 * Copyright (c) 2018.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.forcetower.uefs

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.forcetower.sagres.SagresNavigator
import com.forcetower.uefs.core.injection.AppComponent
import com.forcetower.uefs.core.injection.AppInjection
import com.forcetower.uefs.core.work.sync.SyncMainWorker
import com.forcetower.uefs.service.NotificationHelper
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector
import dagger.android.support.HasSupportFragmentInjector
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

/**
 * Representa o aplicativo por completo.
 *
 * Iniciar o aplicativo por qualquer meio irá iniciar os processos de injeção de dependençias
 */
class UApplication : Application(), HasActivityInjector, HasSupportFragmentInjector, HasBroadcastReceiverInjector, HasServiceInjector {
    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var receiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    lateinit var component: AppComponent

    @Volatile
    private var injected = false

    override fun onCreate() {
        // O Crashlytics é desativado em builds de debug
        initCrashlytics()
        // O log timber só existe em build de debug
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        // Injeta as dependências. Este é o ponto inicial
        injectApplicationIfNecessary()
        super.onCreate()
        // Redefine os trabalhos de sincronização
        defineWorker()
    }

    /**
     * Este método irá observar qual o tipo de sincronização selecionado e tentará cria apenas se ele
     * tiver sido apagado
     */
    private fun defineWorker() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val worker = preferences.getString("stg_sync_worker_type", "0")?.toIntOrNull() ?: 0
        val period = preferences.getString("stg_sync_frequency", "60")?.toIntOrNull() ?: 60
        when (worker) {
            0 -> SyncMainWorker.createWorker(this, period)
            1 -> Unit // SyncLinkedWorker.createWorker(period, false)
        }
    }

    /**
     * Inicia o Crashlytics para envio de erros que acontecem no aplicativo
     */
    private fun initCrashlytics() {
        val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        val kit = Crashlytics.Builder().core(core).build()
        Fabric.with(this, kit)
    }

    /**
     * Cria o componente do Dagger.
     * Este processo pode ser simplicado se a classe extendesse de DaggerApplication
     */
    private fun createApplicationInjector() = AppInjection.create(this)

    /**
     * Injetar as dependencias!
     */
    private fun injectApplicationIfNecessary() {
        if (!injected) {
            synchronized(this) {
                if (!injected) {
                    component = createApplicationInjector()
                    component.inject(this)
                    if (!injected)
                        throw IllegalStateException("Attempt to inject the app has failed")
                }
            }
        }
    }

    /**
     * Marca aplicação como injetada
     */
    @Inject
    fun setInjected() {
        injected = true
    }

    /**
     * Inicializa o objeto de conexão com o Sagres
     */
    @Inject
    fun configureSagresNavigator() {
        SagresNavigator.initialize(this)
    }

    /**
     * Cria/Apaga os canais de notificação
     */
    @Inject
    fun configureNotifications() {
        NotificationHelper(this).createChannels()
    }

    override fun activityInjector() = activityInjector
    override fun supportFragmentInjector() = fragmentInjector
    override fun broadcastReceiverInjector() = receiverInjector
    override fun serviceInjector() = serviceInjector
}