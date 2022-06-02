package jp.techacademy.arisa.takeishi.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import jp.techacademy.arisa.takeishi.apiapp.databinding.ActivityWebViewBinding
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity(), FragmentCallback{

    private lateinit var binding: ActivityWebViewBinding

    private val items = mutableListOf<Shop>()

    //Kotlinでプロパティの初期化を遅らせる方法の一つで"by lazy"は対象のプロパティが初めて呼び出されたときに初期化される　"lateinit"に似てる
    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        //WebViewで指定されたURLを読み込み表示
        webView.loadUrl(intent.getStringExtra(KEY_URL).toString())


        binding = ActivityWebViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //タップした時に行う処理を書く setOnClickListner
        //お気に入りの登録と削除,Alertを先に書いて、そのあとでAdapterの調整をする
        var flg = true
        // 生成されたViewHolderの位置を指定し、オブジェクトを代入
        //val data = items[position]
        // お気に入り状態を取得
        //val isFavorite = FavoriteShop.findBy(data.id) != null

        //お気に入りマークの変化
        binding.webFavoriteImageView.setOnClickListener {
            if (flg) {
                binding.webFavoriteImageView.setImageResource(R.drawable.ic_star)
                flg = false
            } else {
                binding.webFavoriteImageView.setImageResource(R.drawable.ic_star_border)
                flg = true
            }
        }


    }

    //〇6/2変更点
    override fun onClickItem(FavoriteShop: String) {
        TODO("Not yet implemented")
    }

    //〇6/2変更点
    override fun onClickItem(Shop: Shop) {
        TODO("Not yet implemented")
    }

    // Favoriteに追加するときのメソッド(Fragment -> Activity へ通知する)
    override fun onAddFavorite(shop: Shop) {
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            imageUrl = shop.logoImage
            url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc
        })
        (viewPagerAdapter.fragments[MainActivity.VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }


    // Favoriteから削除するときのメソッド(Fragment -> Activity へ通知する)
    override fun onDeleteFavorite(id: String) {
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_massage)
            .setPositiveButton(android.R.string.ok) {_, _ ->
                deleteFavorite(id)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->} //★_　は 今後使用しないの意味
            .create()
            .show()
    }

    private fun deleteFavorite(id: String) {
        FavoriteShop.delete(id)
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }

    companion object {
        private const val KEY_URL = "key_url"
        fun start(activity: Activity, url: String) { //WebViewActivityの遷移処理
            //startActivity()メソッドを呼び出し, その際の引数にURL情報を付加したIntentオブジェクトを入れ、渡す。　Urlを渡しているだけ
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_URL, url))

            //activity.startActivity(Intent(activity,WebViewActivity::class.java).putExtra())
            //putextraでShopを渡す
        }
        private const val VIEW_PAGER_POSITION_API = 0
        private const val VIEW_PAGER_POSITION_FAVORITE = 1
    }


}