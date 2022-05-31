package jp.techacademy.arisa.takeishi.apiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

//インターフェイスを実装し、そのインターフェイスで宣言した登録用のメソッドと削除用のメソッドを実装
//登録や削除を行う際、Fragmentへの通知を実施
//お気に入りを削除するときは、確認用のDialogを表示させる

class MainActivity : AppCompatActivity(), FragmentCallback {

    //Kotlinでプロパティの初期化を遅らせる方法の一つで"by lazy"は対象のプロパティが初めて呼び出されたときに初期化される　"lateinit"に似てる
    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ViewPager2の初期化
        viewPager2.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL  // スワイプの向き横（ORIENTATION_VERTICAL を指定すれば縦スワイプで実装可能です）
            offscreenPageLimit = viewPagerAdapter.itemCount // ViewPager2で保持する画面数
        }
        // TabLayoutの初期化
        // TabLayoutとViewPager2を紐づける
        // TabLayoutのTextを指定する
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()

    }


    //WebViewActivityに画面遷移させる
    override fun onClickItem(url: String) {
        WebViewActivity.start(this, url)
    }


    // Favoriteに追加するときのメソッド(Fragment -> Activity へ通知する)
    override fun onAddFavorite(shop: Shop) {
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            imageUrl = shop.logoImage
            url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc
        })
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
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
        private const val VIEW_PAGER_POSITION_API = 0
        const val VIEW_PAGER_POSITION_FAVORITE = 1
    }



}