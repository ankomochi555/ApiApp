package jp.techacademy.arisa.takeishi.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        //WebViewで指定されたURLを読み込み表示
        webView.loadUrl(intent.getStringExtra(KEY_URL).toString())
    }

    companion object {
        private const val KEY_URL = "key_url"
        fun start(activity: Activity, url: String) { //WebViewActivityの遷移処理
            //startActivity()メソッドを呼び出し, その際の引数にURL情報を付加したIntentオブジェクトを入れ、渡す。
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_URL, url))
        }
    }
}