package jp.techacademy.arisa.takeishi.apiapp

import android.app.Application
import io.realm.Realm

//複数のActivityやFragment間で共有のデータなどを扱う場合 、
// Application クラスを継承したクラスを作成するのが有効

class ApiApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this) //初期化処理
    }
}