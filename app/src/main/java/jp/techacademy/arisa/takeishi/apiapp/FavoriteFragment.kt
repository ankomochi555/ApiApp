package jp.techacademy.arisa.takeishi.apiapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_api.*

//お気に入りリストを表示するFavoriteFragment.ktファイル

class FavoriteFragment: Fragment() {

    private val favoriteAdapter by lazy { FavoriteAdapter(requireContext()) } //4.4Fragment non-null型について Adapterはデータと画面を紐づける役割を持つため、non-null型　クラス、アクティビティでnullになるのとならないものがある。

    // FavoriteFragment -> MainActivity に削除を通知する
    private var fragmentCallback : FragmentCallback? = null

    // 課題:クーポン詳細ページでもお気に入りの追加削除
    override fun onResume() { //updateDataを読んで画面をリロードする
        super.onResume()
        updateData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback) {
            fragmentCallback = context
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_favorite.xmlが反映されたViewを作成して、returnします
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う
        // FavoriteAdapterのお気に入り削除用のメソッドの追加を行う
        favoriteAdapter.apply {
            // Adapterの処理をそのままActivityに通知
            onClickDeleteFavorite = {
                fragmentCallback?.onDeleteFavorite(it.id)
            }
            // Itemをクリックしたとき
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }

        // RecyclerViewの初期化
        recyclerView.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext()) // 一列ずつ表示
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    fun updateData() {
        favoriteAdapter.refresh(FavoriteShop.findAll())
        swipeRefreshLayout.isRefreshing = false
    }
}