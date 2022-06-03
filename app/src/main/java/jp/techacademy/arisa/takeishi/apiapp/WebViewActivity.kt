package jp.techacademy.arisa.takeishi.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import jp.techacademy.arisa.takeishi.apiapp.databinding.ActivityWebViewBinding
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity(){

    private lateinit var binding: ActivityWebViewBinding

    private var favoriteShop: FavoriteShop = FavoriteShop()

    private val items = mutableListOf<Shop>()

    //Kotlinでプロパティの初期化を遅らせる方法の一つで"by lazy"は対象のプロパティが初めて呼び出されたときに初期化される　"lateinit"に似てる
    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        //WebViewで指定されたURLを読み込み表示
        //webView.loadUrl(intent.getStringExtra(KEY_URL).toString())

        favoriteShop = intent.getSerializableExtra(KEY_FAVORITESHOP) as? FavoriteShop ?: return { finish() }()

        webView.loadUrl(favoriteShop.url)


        binding = ActivityWebViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //タップした時に行う処理を書く setOnClickListner
        //お気に入りの登録と削除,Alertを先に書いて、そのあとでAdapterの調整をする
        var flg = true

        //お気に入りマークの変化
        binding.webFavoriteImageView.setOnClickListener {
            if (flg) {
                binding.webFavoriteImageView.setImageResource(R.drawable.ic_star)
                flg = false
                FavoriteShop.insert(favoriteShop)
                (viewPagerAdapter.fragments[MainActivity.VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
            } else {
                binding.webFavoriteImageView.setImageResource(R.drawable.ic_star_border)
                flg = true
                FavoriteShop.delete(favoriteShop.id)
            }
        }


    }

    companion object {
        private const val KEY_URL = "key_url"
        private const val KEY_FAVORITESHOP = "favorite_shop"
        fun start(activity: Activity, url: String) { //WebViewActivityの遷移処理
            //startActivity()メソッドを呼び出し, その際の引数にURL情報を付加したIntentオブジェクトを入れ、渡す。　Urlを渡しているだけ
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_URL, url))
        }
        fun start(activity: Activity, favoriteShop: FavoriteShop) { //FavoriteShopのデータをWebViewActivityにわたす
            //startActivity()メソッドを呼び出し, その際の引数にURL情報を付加したIntentオブジェクトを入れ、渡す。　Urlを渡しているだけ
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_FAVORITESHOP, favoriteShop))

            //activity.startActivity(Intent(activity,WebViewActivity::class.java).putExtra())
            //putextraでShopを渡す
        }
        /*
        private const val VIEW_PAGER_POSITION_API = 0
        private const val VIEW_PAGER_POSITION_FAVORITE = 1

         */
    }


}